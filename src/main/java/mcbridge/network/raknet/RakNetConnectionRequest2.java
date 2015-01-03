package mcbridge.network.raknet;

import java.nio.ByteBuffer;

public class RakNetConnectionRequest2 implements RakNetPacket {
	
	public final static byte PID = RakNetConstants.RAKNET_OPEN_CONNECTION_REQUEST_2;
	public byte[] MAGIC = new byte[16];
	public byte[] securityCookie = new byte[4];
	public short serverPort;
	public short mtuSize;
	public long clientID;
	
	private ByteBuffer buf;
	
	public RakNetConnectionRequest2(byte[] data){
		buf = ByteBuffer.wrap(data);
	}
	
	public void encode() { }
	
	public void decode() {
		buf.get(); //PID
		buf.get(MAGIC);
		buf.get(securityCookie);
		serverPort = buf.getShort();
		mtuSize = buf.getShort();
		clientID = buf.getLong();
	}
	
	public ByteBuffer getBuffer(){
		return buf;
	}

}
