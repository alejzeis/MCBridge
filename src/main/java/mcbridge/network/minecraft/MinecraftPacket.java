package mcbridge.network.minecraft;

import java.nio.ByteBuffer;

public interface MinecraftPacket {
	
	void encode();
	void decode();
	
	ByteBuffer getBuffer();

}
