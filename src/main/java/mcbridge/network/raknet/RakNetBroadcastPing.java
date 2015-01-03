package mcbridge.network.raknet;

import java.nio.ByteBuffer;

public class RakNetBroadcastPing implements RakNetPacket {
	public final byte PID = RakNetConstants.RAKNET_BROADCAST_PING_1;
	public long pingID;
	public byte[] MAGIC = new byte[16];
	
	private ByteBuffer buf;
	
	public RakNetBroadcastPing(byte[] buffer){
		buf = ByteBuffer.wrap(buffer);
	}
	
	public void encode() { }
	
	public void decode() {
		buf.get(); //PID
		pingID = buf.getLong();
		buf.get(MAGIC);
	}
	
	public ByteBuffer getBuffer(){
		return buf;
	}

}
