package mcbridge.network.minecraft;

import java.nio.ByteBuffer;

import mcbridge.MCPEServer;
import mcbridge.network.NetworkUtils;
import mcbridge.network.raknet.RakNetConstants;

public class ServerHandshake implements MinecraftPacket {
	
	public final static byte PID = MinecraftConstants.SERVER_HANDSHAKE;
	public final byte[] cookie = new byte[] {0x04, 0x3f, 0x57, (byte) 0xfe};
	public final byte securityFlags = (byte) 0xcd;
	public short serverPort;
	public final byte[] unknown1 = new byte[] {0x00, 0x00};
	public long session;
	public final byte[] unknown2 = new byte[] {0x00, 0x00, 0x00, 0x00, 0x04, 0x44, 0x0b, (byte) 0xa9};
	
	private ByteBuffer buf;
	
	public ServerHandshake(MCPEServer server, long session){
		buf = ByteBuffer.allocate(96);
		this.session = session;
		
		serverPort = (short) server.port;
	}
	
	public void encode(){
		buf.put(PID);
		buf.put(cookie);
		buf.put(securityFlags);
		buf.putShort(serverPort);
		
		putDataArray();
		
		buf.put(unknown1);
		buf.putLong(session);
		buf.put(unknown2);
	}
	
	public void decode() { }
	
	public byte getEncapPID(){
		return RakNetConstants.DATA_PACKET_4;
	}
	
	public byte getEncapID(){
		return 0x60;
	}
	
	public ByteBuffer getBuffer(){
		return buf;
	}
	
	private void putDataArray(){
		byte[] array1 = new byte[] {(byte) 0xf5, (byte) 0xff, (byte) 0xff, (byte) 0xf5};
		byte[] array2 = new byte[] {(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
		
		buf.put(NetworkUtils.putLTriad(array1.length));
		buf.put(array1);
		
		for(int i=0; i < 9; i++){
			buf.put(NetworkUtils.putLTriad(array2.length));
			buf.put(array2);
		}
		
	}

}
