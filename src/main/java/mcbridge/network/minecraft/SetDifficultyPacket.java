package mcbridge.network.minecraft;

import java.nio.ByteBuffer;

public class SetDifficultyPacket implements MinecraftPacket {
	
	public final static byte PID = MinecraftConstants.SET_DIFFICULTY;
	public int difficulty;
	
	private ByteBuffer buf;
	
	public SetDifficultyPacket(int difficulty){
		this.difficulty = difficulty;
		buf = ByteBuffer.allocate(5);
	}
	
	public void encode(){
		buf.put(PID);
		buf.putInt(difficulty);
	}
	
	public void decode(){ }
	
	public ByteBuffer getBuffer(){
		return buf;
	}

}
