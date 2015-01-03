package mcbridge.network.minecraft;

import java.nio.ByteBuffer;

import org.bukkit.World;

import mcbridge.PocketPlayer;

public class StartGamePacket implements MinecraftPacket {
	
	public static final byte PID = MinecraftConstants.START_GAME;
	public int seed;
	public int unknown = 1;
	public int gamemode;
	public int entityID;
	public int spawnX;
	public int spawnZ;
	public int spawnY;
	
	public float x;
	public float y;
	public float z;
	
	private ByteBuffer buf;
	
	public StartGamePacket(PocketPlayer player, World world){
		buf = ByteBuffer.allocate(53);
		//seed = (int) world.getSeed();
		seed = 1406827239;
		gamemode = player.gamemode;
		entityID = (int) player.entityID;
		x = (float) player.x;
		y = (float) player.y;
		z = (float) player.z;
		
		spawnX = (int) x;
		spawnZ = (int) z;
		spawnY = (int) y;
	}
	
	public void encode(){
		buf.put(PID);
		buf.putInt(seed);
		buf.putInt(unknown);
		buf.putInt(gamemode);
		buf.putInt(entityID);
		buf.putInt(spawnX);
		buf.putInt(spawnZ);
		buf.putInt(spawnY);
		buf.putFloat(x);
		buf.putFloat(y);
		buf.putFloat(z);
	}
	
	public void decode(){ }
	
	public ByteBuffer getBuffer(){
		return buf;
	}

}
