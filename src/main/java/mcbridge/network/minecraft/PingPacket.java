package mcbridge.network.minecraft;

import java.nio.ByteBuffer;

import mcbridge.network.raknet.RakNetConstants;

public class PingPacket implements MinecraftPacket {
	
	public final static byte PID_Ping = MinecraftConstants.PING;
	public final static byte PID_Pong = MinecraftConstants.PONG;
	public long identifier;
	
	private ByteBuffer buf;
	
	public PingPacket(byte[] data){
		buf = ByteBuffer.wrap(data);
	}
	
	public PingPacket(long identifier){
		buf = ByteBuffer.allocate(9);
		this.identifier = identifier;
	}
	
	public void encode() {
		buf.put(PID_Pong);
		buf.putLong(identifier);
	}
	
	public void decode() {
		buf.get(); //PID
		identifier = buf.getLong();
	}
	
	public ByteBuffer getBuffer(){
		return buf;
	}

}
