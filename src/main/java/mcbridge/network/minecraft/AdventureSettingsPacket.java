package mcbridge.network.minecraft;

import java.nio.ByteBuffer;

public class AdventureSettingsPacket implements MinecraftPacket {
	
	public final static byte PID = MinecraftConstants.ADVENTURE_SETTINGS;
	public byte[] flags;
	
	private ByteBuffer buf;
	
	public AdventureSettingsPacket(byte[] flags){
		this.flags = flags;
		buf = ByteBuffer.allocate(1 + flags.length);
	}
	
	public void encode() {
		buf.put(PID);
		buf.put(flags);
	}
	
	public void decode() { }
	
	public ByteBuffer getBuffer(){
		return buf;
	}

}
