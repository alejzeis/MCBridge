package mcbridge.network.raknet;

import java.nio.ByteBuffer;

import mcbridge.MCPEServer;

public class RakNetBroadcastPong implements RakNetPacket {
	public final static byte PID = RakNetConstants.RAKNET_BROADCAST_PONG_1;
	public final static String identifier = "MCCPP;Demo;";
	public long pingID;
	public long serverID;
	public byte[] MAGIC;
	public String advertiseName;
	
	private ByteBuffer buf;
	
	public RakNetBroadcastPong(MCPEServer server){
		pingID = server.startTime;
		serverID = server.serverID;
		MAGIC = server.MAGIC;
		advertiseName = RakNetBroadcastPong.identifier + server.serverName;
		
		buf = ByteBuffer.allocate(35 + advertiseName.length());
	}
	
	public void encode(){
		byte[] name = advertiseName.getBytes();
		buf.put(PID);
		buf.putLong(pingID);
		buf.putLong(serverID);
		buf.put(MAGIC);
		buf.putShort((short) name.length);
		buf.put(name);
	}
	
	public void decode() { }
	
	public ByteBuffer getBuffer(){
		return buf;
	}

}
