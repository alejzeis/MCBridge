package mcbridge.network.minecraft;

import java.nio.ByteBuffer;

public class SetHealthPacket implements MinecraftPacket {
	
	public final static byte PID = MinecraftConstants.SET_HEALTH;
	public byte health;
	
	private ByteBuffer buf;
	
	public SetHealthPacket(int health){
		this.health = (byte) health;
		buf = ByteBuffer.allocate(2);
	}
	
	public SetHealthPacket(byte[] buffer){
		buf = ByteBuffer.wrap(buffer);
	}
	
	public void encode() {
		buf.put(PID);
		buf.put(health);
	}
	
	public void decode() {
		buf.get(); //PID
		health = buf.get();
	}
	
	public ByteBuffer getBuffer(){
		return buf;
	}

}
