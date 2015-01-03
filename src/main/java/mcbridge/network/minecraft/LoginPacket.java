package mcbridge.network.minecraft;

import java.nio.ByteBuffer;

import mcbridge.network.NetworkUtils;
import mcbridge.network.raknet.RakNetConstants;

public class LoginPacket implements MinecraftPacket {
	
	public static final byte PID = MinecraftConstants.LOGIN;
	public String username;
	public int protocol;
	public int protocol2;
	public int mojangClientID;
	public byte[] realmsData = new byte[1];
	
	private ByteBuffer buf;
	
	public LoginPacket(byte[] data){
		buf = ByteBuffer.wrap(data);
	}
	
	public void encode() { }
	
	public void decode() {
		buf.get(); //PID
		username = NetworkUtils.getString(buf);
		protocol = buf.getInt();
		protocol2 = buf.getInt();
		mojangClientID = buf.getInt();
		buf.get(realmsData);
	}
	
	public ByteBuffer getBuffer(){
		return buf;
	}

}
