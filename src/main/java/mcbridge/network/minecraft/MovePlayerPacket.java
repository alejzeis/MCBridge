package mcbridge.network.minecraft;

import java.nio.ByteBuffer;

import org.bukkit.Location;

public class MovePlayerPacket implements MinecraftPacket {
	
	public final static byte PID = MinecraftConstants.MOVE_PLAYER;
	public int entityID;
	public float x;
	public float y;
	public float z;
	public float yaw;
	public float pitch;
	public float bodyYaw;
	public byte teleport = 0x00;
	
	private ByteBuffer buf;
	
	public MovePlayerPacket(Location loc, byte teleport){
		x = (float) loc.getX();
		y = (float) loc.getY();
		z = (float) loc.getZ();
		
		yaw = loc.getYaw();
		pitch = loc.getPitch();
		bodyYaw = yaw;
		
		this.teleport = teleport;
		
		buf = ByteBuffer.allocate(30);
	}
	
	public MovePlayerPacket(byte[] buffer){
		buf = ByteBuffer.wrap(buffer);
	}
	
	public void encode() {
		buf.put(PID);
		buf.putInt(entityID);
		buf.putFloat(x);
		buf.putFloat(y);
		buf.putFloat(z);
		buf.putFloat(pitch);
		buf.putFloat(yaw);
		buf.putFloat(bodyYaw);
		buf.put(teleport);
	}
	
	public void decode() { 
		buf.get(); //PID
		entityID = buf.getInt();
		x = buf.getFloat();
		y = buf.getFloat();
		z = buf.getFloat();
		yaw = buf.getFloat();
		pitch = buf.getFloat();
		bodyYaw = buf.getFloat();
		teleport = buf.get();
	}
	
	public ByteBuffer getBuffer(){
		return buf;
	}

}
