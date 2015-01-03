package mcbridge;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType.Sender;

import mcbridge.network.CustomPacket;
import mcbridge.network.DisconnectPacket;
import mcbridge.network.InternalPacket;
import mcbridge.network.chunk.ChunkSender;
import mcbridge.network.minecraft.AdventureSettingsPacket;
import mcbridge.network.minecraft.ClientConnect;
import mcbridge.network.minecraft.ClientHandshakePacket;
import mcbridge.network.minecraft.FullChunkDataPacket;
import mcbridge.network.minecraft.LoginPacket;
import mcbridge.network.minecraft.LoginStatusPacket;
import mcbridge.network.minecraft.MinecraftConstants;
import mcbridge.network.minecraft.MinecraftPacket;
import mcbridge.network.minecraft.MovePlayerPacket;
import mcbridge.network.minecraft.PingPacket;
import mcbridge.network.minecraft.ServerHandshake;
import mcbridge.network.minecraft.SetDifficultyPacket;
import mcbridge.network.minecraft.SetHealthPacket;
import mcbridge.network.minecraft.SetSpawnPosition;
import mcbridge.network.minecraft.SetTimePacket;
import mcbridge.network.minecraft.StartGamePacket;
import mcbridge.network.raknet.AckPacket;
import mcbridge.network.raknet.NackPacket;

public class PocketPlayer {
	public final short mtu;
	
	public String name;
	public double x;
	public double y;
	public double z;
	public float yaw;
	public float pitch;
	public long entityID;
	public int gamemode;
	public int health;
	
	public InetAddress ip;
	public int port;
	public String address;
	
	protected int pingTimer = 10;
	protected boolean hasStartedPing = false;
	protected boolean loggedIn = false;
	
	private MCPEServer server;
	protected CustomPacket currentQueue = new CustomPacket();
	protected List<Integer> ACKQueue = new ArrayList<Integer>();
	protected List<Integer> NACKQueue = new ArrayList<Integer>();
	protected Map<Integer, CustomPacket> recoveryQueue = new HashMap<Integer, CustomPacket>();
	
	private int currentSequenceNum = 0;
	private int lastSequenceNum = 0;
	private int messageIndex = 0;
	private int splitID = 0;
	
	@SuppressWarnings("deprecation")
	public PocketPlayer(MCPEServer server, long entityID, short mtu){
		this.server = server;
		this.entityID = entityID;
		this.gamemode = server.defaultGamemode;
		//this.mtu = mtu;
		this.mtu = 1447;
		server.getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(server.getPlugin(), new BukkitRunnable(){
			
			@Override
			public void run() {
				update();
			}
		}, 0L, 10L);
	}
	
	public void update(){
		synchronized (ACKQueue) {
			if(!ACKQueue.isEmpty()){
				int[] numbers = new int[ACKQueue.size()];
				
				for(int offset = 0; offset < numbers.length; offset++){
					numbers[offset] = ACKQueue.get(offset);
				}
				
				AckPacket ack = new AckPacket(numbers);
				ack.encode();
				try {
					server.sendPacket(ack.getBuffer().array(), ip, port);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					ACKQueue.clear();
				}
			}
		}
		
		synchronized (NACKQueue) {
			if(!NACKQueue.isEmpty()){
				int[] numbers = new int[NACKQueue.size()];
				
				for(int offset = 0; offset < numbers.length; offset++){
					numbers[offset] = NACKQueue.get(offset);
				}
				
				NackPacket nack = new NackPacket(numbers);
				nack.encode();
				try {
					server.sendPacket(nack.getBuffer().array(), ip, port);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					NACKQueue.clear();
				}
			}
		}
		
		synchronized (currentQueue) {
			if(!currentQueue.packets.isEmpty()){
				currentQueue.sequenceNumber = currentSequenceNum++;
				currentQueue.encode();
				try {
					server.sendPacket(currentQueue.getBuffer().array(), ip, port);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					currentQueue.packets.clear();
				}
			}
		}
	}
	
	public void handlePacket(CustomPacket cp) throws IOException{
		//System.out.println("Handling Custom packet from "+ip.toString()+":"+port+"\nThere are "+cp.packets.size()+" packets encapsualted.");
		synchronized (NACKQueue) {
			if(cp.sequenceNumber - lastSequenceNum == 1){
				lastSequenceNum = cp.sequenceNumber;
			} else {
				for(int i = lastSequenceNum; i < cp.sequenceNumber; ++i){
					NACKQueue.add(i);
				}
			}
		}
		synchronized (ACKQueue) {
			System.out.println("Seq. Num is: "+cp.sequenceNumber);
			ACKQueue.add(cp.sequenceNumber);
		}
		
		for(InternalPacket ipk: cp.packets){
			//System.out.println("Internal Packet: "+Arrays.toString(ipk.buffer));
			byte pid = ipk.buffer[0];
		
			switch(pid){
			
			case MinecraftConstants.CLIENT_CONNECT:
				ClientConnect cc = new ClientConnect(ipk.buffer);
				cc.decode();
				
				
				ServerHandshake sh = new ServerHandshake(server, cc.session);
				//sh.encode();
				
				addToQueue(sh);
				//server.getPlugin().logger.info("ServerHandshake OUT!");
				break;
				
			case MinecraftConstants.CLIENT_HANDSHAKE:
				ClientHandshakePacket chp = new ClientHandshakePacket(ipk.buffer);
				chp.decode();
				break;
				
			case MinecraftConstants.LOGIN:
				LoginPacket login = new LoginPacket(ipk.buffer);
				login.decode();
				if(! loggedIn){
					login(login);
					StartGamePacket sgp = new StartGamePacket(this, server.world);
					//sgp.encode();
					addToQueue(sgp);
					System.out.println("StartGame OUT.");
					
					SetTimePacket stp = new SetTimePacket(server.world.getTime());
					addToQueue(stp);
					
					SetSpawnPosition ssp = new SetSpawnPosition((int) x, (int) y, (int) z);
					addToQueue(ssp);
					
					SetHealthPacket shp = new SetHealthPacket(health);
					addToQueue(shp);
					
					SetDifficultyPacket sdp = new SetDifficultyPacket(1);
					addToQueue(sdp);
					
					new ChunkSender(this, server).runTaskLater(server.getPlugin(), 40);
					initPlayer();
				}
				
				break;
				
			case MinecraftConstants.PING:
				if(!hasStartedPing){
					hasStartedPing = true;
				}
				PingPacket ping = new PingPacket(ipk.buffer);
				ping.decode();
				
				PingPacket pong = new PingPacket(ping.identifier);
				//pong.encode();
				
				pingTimer = 10;
				
				addToQueue(pong);
				break;
			
			default:
				server.getPlugin().logger.warning("Could not handle packet in Player: "+name+"(EID: "+entityID+")");
				server.getPlugin().logger.warning("PID is: "+pid);
				break;
			}
		}
	}
	
	private void initPlayer() {
		Location loc = new Location(server.world, (int) x, (int) y, (int) z, yaw, pitch);
		SetTimePacket stp = new SetTimePacket(server.world.getTime());
		MovePlayerPacket mpp = new MovePlayerPacket(loc, (byte) 0x80);
		AdventureSettingsPacket asp = new AdventureSettingsPacket(new byte[] {0x20});
		addToQueue(stp);
		addToQueue(mpp);
		addToQueue(asp);
	}

	public void close(String reason){
		DisconnectPacket disconnect = new DisconnectPacket(reason);
		//disconnect.encode();
		
		addToQueue(disconnect);
		
		server.getPlugin().getLogger().info(name+"["+address+", EID: "+entityID+"] logged out due to: "+reason);
		server.removePlayer((int) entityID);
	}
	
	public void close(){
		DisconnectPacket disconnect = new DisconnectPacket("Unspecified reason.");
		//disconnect.encode();
		
		addToQueue(disconnect);
		
		server.getPlugin().getLogger().info(name+"["+address+", EID: "+entityID+"] logged out due to unspecified reason.");
		server.removePlayer((int) entityID);
	}
	
	protected synchronized void handleAck(AckPacket ack){
		//System.out.println("Ack length is: "+ack.packetNumbers.length);
		for(int i = 0; i < ack.packetNumbers.length; i++){
			//server.getPlugin().getLogger().info("ACK Packet recieved count: "+ack.packetNumbers[i]);
			recoveryQueue.remove(i);
		}
	}
	
	protected synchronized void handleNack(NackPacket nack) throws IOException{
		for(int i: nack.packetNumbers){
			server.getPlugin().getLogger().warning("NACK Packet recieved count: "+i);
			server.sendPacket(recoveryQueue.get(i).getBuffer().array(), ip, port);
		}
	}
	
	public synchronized void addToQueue(MinecraftPacket p){
		addToQueue(p, 2);
	}
	
	public synchronized void addToQueue(MinecraftPacket p, int reliability){
		p.encode();
		
		//System.out.println("Packet OUT!");
		/*
		InternalPacket ipk = new InternalPacket();
		ipk.buffer = p.getBuffer().array();
		ipk.reliability = (byte) reliability;
		ipk.messageIndex = messageIndex++;
		ipk.toBinary();
		*/
		InternalPacket[] ipk = InternalPacket.fromMinecraftPacket(this, p, reliability);
		
		if(p instanceof FullChunkDataPacket){
			System.out.println("Compressed length is: "+p.getBuffer().capacity());
		}
		
		if(currentQueue.getLength() >= mtu && !currentQueue.packets.isEmpty()){
			currentQueue.sequenceNumber = currentSequenceNum++;
			currentQueue.encode();
			try {
				server.sendPacket(currentQueue.getBuffer().array(), ip, port);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				currentQueue.packets.clear();
			}
			
		}
		
		for(InternalPacket ipck: ipk){
			ipck.toBinary();
			currentQueue.packets.add(ipck);
		}
	}
	
	private void login(LoginPacket lp) throws IOException{
		System.out.println("Protocol 1: "+lp.protocol+", Protocol 2: "+lp.protocol2);
		if(lp.protocol == MCPEServer.protocolVersion || lp.protocol2 == MCPEServer.protocolVersion){
			//Good login, so far.
			if(lp.username.length() < 3 || lp.username.length() > 15){
				close("Username is not valid.");
			} else {
				//TODO: Load player data
				//DUMMY
				x = server.world.getSpawnLocation().getX();
				y = server.world.getSpawnLocation().getY();
				z = server.world.getSpawnLocation().getZ();
				
				health = 20;
				
				server.playerJoin(lp, this);
				name = lp.username;
				
				LoginStatusPacket lsp = new LoginStatusPacket(0);
				//lsp.encode();
				addToQueue(lsp);
				
				loggedIn = true;
			}
		} else {
			if(lp.protocol > MCPEServer.protocolVersion || lp.protocol2 > MCPEServer.protocolVersion){ //Server Outdated
				LoginStatusPacket lsp = new LoginStatusPacket(1);
				//lsp.encode();
				addToQueue(lsp);
				
				close("Outdated Server! I'm on "+MCPEServer.protocolVersion);
			} else if(lp.protocol < MCPEServer.protocolVersion || lp.protocol2 < MCPEServer.protocolVersion){ //Client Outdated
				LoginStatusPacket lsp = new LoginStatusPacket(2);
				//lsp.encode();
				addToQueue(lsp);
				
				close("Outdated Client! I'm on "+MCPEServer.protocolVersion);
			}
		}
	}
	
	public int getMessageIndex(){
		return messageIndex;
	}
	
	public int getSplitID(){
		return splitID;
	}
	
	public void setMessageIndex(int index){
		messageIndex = index;
	}
	
	public void setSplitID(int id){
		splitID = id;
	}

}
