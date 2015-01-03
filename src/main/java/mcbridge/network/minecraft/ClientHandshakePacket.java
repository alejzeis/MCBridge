package mcbridge.network.minecraft;

import java.nio.ByteBuffer;

import mcbridge.network.NetworkUtils;
import mcbridge.network.raknet.RakNetConstants;

public class ClientHandshakePacket implements MinecraftPacket {
	
	public static final byte PID = MinecraftConstants.CLIENT_HANDSHAKE;
	public int cookie;
	public byte security;
	public short port;
	public short timestamp;
	public long session;
	public long session2;
	
	private ByteBuffer buf;
	
	public ClientHandshakePacket(byte[] data){
		buf = ByteBuffer.wrap(data);
	}
	
	public void encode() { }
	
	public void decode() {
		buf.get(); //PID
		cookie = buf.getInt();
		security = buf.get();
		port = buf.getShort();
		buf.get(new byte[(int) buf.get()]);
		getDataArray();
		timestamp = buf.getShort();
		session2 = buf.getLong();
		session = buf.getLong();
	}
	
	public ByteBuffer getBuffer(){
		return buf;
	}
	
	private void getDataArray(){
		for(int i = 0; i < 9; i++){
			int l = NetworkUtils.getLTriad(buf);
			buf.get(new byte[l]);
		}
	}

}
