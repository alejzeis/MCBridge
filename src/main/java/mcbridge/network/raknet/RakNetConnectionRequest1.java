package mcbridge.network.raknet;

import java.nio.ByteBuffer;

public class RakNetConnectionRequest1 implements RakNetPacket {
	
	public static final byte PID = RakNetConstants.RAKNET_OPEN_CONNECTION_REQUEST_1;
	public byte[] MAGIC = new byte[16];
	public byte rakNetVersion;
	public int nullPayloadLength;
	
	private ByteBuffer buf;
	
	public RakNetConnectionRequest1(byte[] data){
		buf = ByteBuffer.wrap(data);
	}
	
	public void encode() { }
	
	public void decode(){
		buf.get(); //PID
		buf.get(MAGIC);
		rakNetVersion = buf.get();
		nullPayloadLength = buf.remaining();
	}
	
	public ByteBuffer getBuffer(){
		return buf;
	}

}
