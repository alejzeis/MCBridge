package mcbridge.network.minecraft;

import java.nio.ByteBuffer;

public class SetTimePacket implements MinecraftPacket {
	
	public final static byte PID = MinecraftConstants.SET_TIME;
	public long time;
	public byte started = (byte) 0x80;
	
	private ByteBuffer buf;
	
	public SetTimePacket(long time){
		this.time = time;
		buf = ByteBuffer.allocate(6);
	}
	
	public void encode(){
		buf.put(PID);
		buf.putInt((int) time);
		buf.put(started);
	}
	
	public void decode() { }
	
	public ByteBuffer getBuffer(){
		return buf;
	}

}
