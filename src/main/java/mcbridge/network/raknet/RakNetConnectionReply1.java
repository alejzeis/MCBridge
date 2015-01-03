package mcbridge.network.raknet;

import java.nio.ByteBuffer;

import mcbridge.MCPEServer;

public class RakNetConnectionReply1 implements RakNetPacket {
	
	public final static byte PID = RakNetConstants.RAKNET_OPEN_CONNECTION_REPLY_1;
	public byte[] MAGIC;
	public long serverID;
	public byte security;
	public short mtuSize;
	
	private ByteBuffer buf;
	
	public RakNetConnectionReply1(RakNetConnectionRequest1 cr1, MCPEServer server){
		MAGIC = cr1.MAGIC;
		serverID = server.serverID;
		security = 0;
		mtuSize = (short) cr1.nullPayloadLength;
		
		buf = ByteBuffer.allocate(28);
	}
	
	public void encode(){
		buf.put(PID);
		buf.put(MAGIC);
		buf.putLong(serverID);
		buf.put(security);
		buf.putShort(mtuSize);
	}
	
	public void decode() { }
	
	public ByteBuffer getBuffer(){
		return buf;
	}

}
