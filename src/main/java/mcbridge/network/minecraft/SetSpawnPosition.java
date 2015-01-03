package mcbridge.network.minecraft;

import java.nio.ByteBuffer;

public class SetSpawnPosition implements MinecraftPacket {
	
	public static final byte PID = MinecraftConstants.SET_SPAWN_POSITION;
	public int x;
	public int z;
	public byte y;
	
	private ByteBuffer buf;
	
	public SetSpawnPosition(int x, int y, int z){
		this.x = x;
		this.y = (byte) y;
		this.z = z;
		buf = ByteBuffer.allocate(10);
	}
	
	public void encode(){
		buf.put(PID);
		buf.putInt(x);
		buf.putInt(z);
		buf.put(y);
	}
	
	public void decode(){ }
	
	public ByteBuffer getBuffer(){
		return buf;
	}


}
