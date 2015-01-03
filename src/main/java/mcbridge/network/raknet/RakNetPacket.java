package mcbridge.network.raknet;

import java.nio.ByteBuffer;

public interface RakNetPacket {
	
	void decode();
	void encode();
	
	ByteBuffer getBuffer();

}
