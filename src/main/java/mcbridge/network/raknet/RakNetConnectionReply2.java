package mcbridge.network.raknet;

import java.nio.ByteBuffer;

import mcbridge.MCPEServer;

public class RakNetConnectionReply2 implements RakNetPacket {
	
	public final static byte PID = RakNetConstants.RAKNET_OPEN_CONNECTION_REPLY_2;
	public byte[] MAGIC;
	public long serverID;
	public short clientPort;
	public short mtuSize;
	public byte security = 0x00;
	
	private ByteBuffer buf;
	
	public RakNetConnectionReply2(MCPEServer server, short clientPort, short mtu){
		buf = ByteBuffer.allocate(30);
		MAGIC = server.MAGIC;
		serverID = server.serverID;
		this.clientPort = clientPort;
		mtuSize = mtu;
	}
	
	public void encode(){
		buf.put(PID);
		buf.put(MAGIC);
		buf.putLong(serverID);
		buf.putShort(clientPort);
		buf.putShort(mtuSize);
		buf.put(security);
	}
	
	public void decode() { }
	
	public ByteBuffer getBuffer(){
		return buf;
	}

}
