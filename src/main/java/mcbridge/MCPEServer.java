package mcbridge;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;

import mcbridge.network.CustomPacket;
import mcbridge.network.HandlePacketWorker;
import mcbridge.network.PEChatManager;
import mcbridge.network.minecraft.LoginPacket;
import mcbridge.network.minecraft.MinecraftConstants;
import mcbridge.network.raknet.AckPacket;
import mcbridge.network.raknet.NackPacket;
import mcbridge.network.raknet.RakNetBroadcastPing;
import mcbridge.network.raknet.RakNetBroadcastPong;
import mcbridge.network.raknet.RakNetConnectionReply1;
import mcbridge.network.raknet.RakNetConnectionReply2;
import mcbridge.network.raknet.RakNetConnectionRequest1;
import mcbridge.network.raknet.RakNetConnectionRequest2;
import mcbridge.network.raknet.RakNetConstants;
import mcbridge.network.raknet.RakNetPacket;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;

import com.comphenix.protocol.ProtocolManager;

/**
 * Represents a Minecraft: Pocket Edition Server.
 * @author jython234
 *
 */
public class MCPEServer extends Thread {
	public final static int protocolVersion = MinecraftConstants.CURRENT_PROTOCOL;
	
	
	public final int port;
	public final World world;
	public final long serverID;
	public final String serverName;
	public final String motd;
	public final int defaultGamemode;
	public final long startTime;
	
	public final PEChatManager chatManager;
	
	public byte[] MAGIC;
	
	private PluginCore plugin;
	private Server server;
	private Logger logger;
	private DatagramSocket sock;
	private boolean running;
	private int nextEntityID = 0;
	
	private ArrayList<PocketPlayer> connectedPlayers;
	
	public MCPEServer(int bindPort, PluginCore plugin){
		this.plugin = plugin;
		server = plugin.getServer();
		logger = plugin.logger;
		defaultGamemode = plugin.gamemode;
		
		port = bindPort;
		world = server.getWorld(plugin.worldName);
		serverID = new Random().nextLong();
		serverName = plugin.serverName;
		motd = plugin.motd;
		chatManager = new PEChatManager(this);
		connectedPlayers = new ArrayList<PocketPlayer>();
		
		startTime = System.currentTimeMillis();
	}
	
	public void run(){
		setName("MCPEServer");
		logger.info("This is MCBridge version "+PluginCore.version);
		logger.info("Starting MCPE server on port: "+port);
		
		try{
			sock = new DatagramSocket(port);
			logger.info("MCPE Server started.");
			sock.setSoTimeout(2000);
			while(running){
				byte[] buffer = new byte[1536];
				DatagramPacket dp = new DatagramPacket(buffer, 1536);
				try{
					sock.receive(dp);
					dp.setData(Arrays.copyOf(buffer, dp.getLength()));
					handlePacket(dp);
				} catch(SocketTimeoutException te){
					logger.fine("No packets recieved for 2 seconds :/");
				} catch(IOException e){
					logger.warning("IOException while handling packets:");
					logger.warning(e.getMessage());
				}
			}
			
		} catch(SocketException e){
			logger.severe("FAILED TO CREATE SOCKET!");
			logger.severe(e.getMessage());
			logger.severe("Perhaps another server is running on the port?");
			server.getPluginManager().disablePlugin(plugin);
		}
	}
	
	public void Start(){
		if(!running){
			running = true;
			start();
		} else {
			throw new RuntimeException("This instance is already running!");
		}
	}
	
	public void Stop() throws InterruptedException{
		if(running){
			logger.info("Attempting to stop server...");
			running = false;
			join();
			logger.info("Server Stopped.");
		} else {
			throw new RuntimeException("This instance is not running!");
		}
	}
	
	public void sendPacket(RakNetPacket p, InetAddress addr, int port) throws IOException{
		byte[] buf = p.getBuffer().array();
		DatagramPacket dp = new DatagramPacket(buf, buf.length, addr, port);
		sock.send(dp);
	}
	
	public void sendPacket(byte[] payload, InetAddress addr, int port) throws IOException{
		DatagramPacket dp = new DatagramPacket(payload, payload.length, addr, port);
		sock.send(dp);
	}
	
	
	private synchronized void handlePacket(DatagramPacket dp){
		//logger.info("Packet recieved!");
		byte pid = dp.getData()[0];
		
		try{
			switch(pid){
			
			case RakNetConstants.RAKNET_BROADCAST_PING_1:
				RakNetBroadcastPing ping = new RakNetBroadcastPing(dp.getData());
				ping.decode();
				MAGIC = ping.MAGIC;
				advertise(dp);
				break;
				
			case RakNetConstants.RAKNET_OPEN_CONNECTION_REQUEST_1:
				RakNetConnectionRequest1 cr1 = new RakNetConnectionRequest1(dp.getData());
				cr1.decode();
				
				//logger.info("Nullpayload is "+cr1.nullPayloadLength);
				
				RakNetConnectionReply1 reply1 = new RakNetConnectionReply1(cr1, this);
				reply1.encode();
				
				sendPacket(reply1, dp.getAddress(), dp.getPort());
				//logger.info("0x06 OUT.");
				break;
				
			case RakNetConstants.RAKNET_OPEN_CONNECTION_REQUEST_2:
				RakNetConnectionRequest2 cr2 = new RakNetConnectionRequest2(dp.getData());
				cr2.decode();
				
				RakNetConnectionReply2 reply2 = new RakNetConnectionReply2(this, (short) dp.getPort(), cr2.mtuSize);
				reply2.encode();
				
				sendPacket(reply2, dp.getAddress(), dp.getPort());
				
				PocketPlayer player = new PocketPlayer(this, nextEntityID++, cr2.mtuSize);
				player.ip = dp.getAddress();
				player.port = dp.getPort();
				player.address = dp.getAddress().toString()+":"+dp.getPort();
				connectedPlayers.add(player);
				
				break;
				
			default:
				if(pid >= RakNetConstants.DATA_PACKET_0 &&  pid <= RakNetConstants.DATA_PACKET_F){
					handleDataPacket(dp);
				} else if(pid == RakNetConstants.RAKNET_ACK){
					handleACK(dp);
				} else if(pid == RakNetConstants.RAKNET_NACK){
					handleNACK(dp);
				} else {
					logger.warning("Unknown packet recieved! PID: "+pid);
					logger.warning("Skipped "+dp.getLength()+" bytes.");
				}
				break;
				
			}
		} catch(IOException e){
			logger.warning("Failed to handle packet: "+e.getMessage());
		}
	}
	
	private synchronized void handleDataPacket(DatagramPacket dp) throws IOException{
		PocketPlayer searchingplayer;
		PocketPlayer player = null;
		//System.out.println("Size is: "+connectedPlayers.size());
		for(int i=0; i < connectedPlayers.size(); i++){
			searchingplayer = connectedPlayers.get(i);
			//System.out.println("Compare: Packet: "+dp.getAddress().toString()+":"+dp.getPort()+" Player: "+searchingplayer.address);
			if(searchingplayer.address.equals(dp.getAddress().toString()+":"+dp.getPort())){
				player = searchingplayer;
				CustomPacket ep = new CustomPacket(dp.getData());
				ep.decode();
				player.handlePacket(ep);
				//HandlePacketWorker worker = new HandlePacketWorker(player, ep);
				//worker.start();
				break;
			}
		}
		if(player == null){
			logger.warning("Failed to handle packet, no handler found for: "+dp.getAddress().toString()+":"+dp.getPort());
		}
		
	}
	
	private void handleACK(DatagramPacket dp){
		AckPacket ack = new AckPacket(dp.getData());
		ack.decode();
		
		try{
			getPlayerByAddress(dp.getAddress(), dp.getPort()).handleAck(ack);
		} catch(NullPointerException e){
			logger.warning("Could not handle ACK from "+dp.getAddress().toString()+":"+dp.getPort()+", player not found.");
		}
		
	}
	
	private void handleNACK(DatagramPacket dp) throws IOException{
		NackPacket nack = new NackPacket(dp.getData());
		nack.decode();
		
		try{
			getPlayerByAddress(dp.getAddress(), dp.getPort()).handleNack(nack);
		} catch(NullPointerException e){
			logger.warning("Could not handle NACK from "+dp.getAddress().toString()+":"+dp.getPort()+", player not found.");
		}
	}
	
	private void advertise(DatagramPacket p) throws IOException{
		RakNetBroadcastPong pong = new RakNetBroadcastPong(this);
		pong.encode();
		sendPacket(pong, p.getAddress(), p.getPort());
		//logger.info("Advertisment packet OUT.");
	}
	
	public PluginCore getPlugin(){
		return plugin;
	}
	
	public ArrayList<PocketPlayer> getConnectedPlayers(){
		return connectedPlayers;
	}
	
	public PocketPlayer getPlayerByEntityID(int entityID){
		PocketPlayer player = null;
		for(int i = 0; i < connectedPlayers.size(); i++){
			if(connectedPlayers.get(i).entityID == entityID){
				player = connectedPlayers.get(i);
			}
		}
		
		return player;
	}
	
	public PocketPlayer getPlayerByAddress(InetAddress addr, int port){
		PocketPlayer player = null;
		for(int i = 0; i < connectedPlayers.size(); i++){
			if(connectedPlayers.get(i).ip.equals(addr) && ((Integer)connectedPlayers.get(i).port).equals((Integer)port)){
				player = connectedPlayers.get(i);
			}
		}
		
		return player;
	}
	
	public void removePlayer(int entityID){
		for(int i = 0; i < connectedPlayers.size(); i++){
			if(connectedPlayers.get(i).entityID == entityID){
				connectedPlayers.remove(i);
			}
		}
	}
	
	protected synchronized void playerJoin(LoginPacket lp, PocketPlayer player){
		logger.info(lp.username + "["+player.address+"] logged in with entity id "+player.entityID+" at X: "+player.x+", Y: "+player.y+", Z: "+player.z);
		chatManager.broadcastPC(ChatColor.AQUA+"[MCB]: "+ChatColor.YELLOW+lp.username+" joined the game.");
		chatManager.broadcastPE("[MCB]: "+lp.username+" joined the game.");
	}

}
