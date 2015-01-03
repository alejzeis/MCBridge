package mcbridge.network.minecraft;

import java.nio.ByteBuffer;

import mcbridge.network.raknet.RakNetConstants;

public class LoginStatusPacket implements MinecraftPacket {
	
	public static final byte PID = MinecraftConstants.LOGIN_STATUS;
	public int status; //0 - OK, 1 - Server outdated., 2 - Client outdated.
	
	private ByteBuffer buf;
	
	public LoginStatusPacket(int status){
		buf = ByteBuffer.allocate(5);
		this.status = status;
	}
	
	public void encode(){
		buf.put(PID);
		buf.putInt(status);
	}
	
	public void decode() { }
	
	public ByteBuffer getBuffer(){
		return buf;
	}

}
