package mcbridge.network.minecraft;

import java.nio.ByteBuffer;

import mcbridge.network.raknet.RakNetConstants;

/**
 * Represents a ClientConnect packet (0x09). Client to Server.
 * Data Packet.
 * @author jython234
 *
 */
public class ClientConnect implements MinecraftPacket {
	
	public final static byte PID = MinecraftConstants.CLIENT_CONNECT;
	public long clientID;
	public long session;
	public byte unknown;
	
	private ByteBuffer buf;
	
	public ClientConnect(byte[] data){
		buf = ByteBuffer.wrap(data);
	}
	
	public void encode() { }
	
	public void decode() {
		buf.get();
		clientID = buf.getLong();
		session = buf.getLong();
		unknown = buf.get();
	}
	
	public ByteBuffer getBuffer(){
		return buf;
	}

}
