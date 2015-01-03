package mcbridge.network;

import java.nio.ByteBuffer;

import mcbridge.network.minecraft.MinecraftConstants;
import mcbridge.network.minecraft.MinecraftPacket;

public class DisconnectPacket implements MinecraftPacket{
	
	public final static byte PID = MinecraftConstants.DISCONNECT;
	
	private ByteBuffer buf;
	
	public DisconnectPacket(byte[] data){
		buf = ByteBuffer.wrap(data);
	}
	
	public DisconnectPacket(String reason){
		buf = ByteBuffer.allocate(1); //Disconnect reasons are currently not supported.
	}
	
	public void encode(){
		buf.put(PID); //Disconnect reasons are currently not supported. 
	}
	
	public void decode(){
		buf.get(); //Disconnect reasons are currently not supported.
	}
	
	public ByteBuffer getBuffer(){
		return buf;
	}

}
