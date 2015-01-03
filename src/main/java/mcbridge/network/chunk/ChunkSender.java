package mcbridge.network.chunk;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import mcbridge.MCPEServer;
import mcbridge.PocketPlayer;
import mcbridge.network.minecraft.FullChunkDataPacket;

public class ChunkSender extends BukkitRunnable {
	private int x;
	private int z; 
	
	private int centerX;
	private int centerZ;
	
	private PocketPlayer player;
	private MCPEServer server;
	
	public ChunkSender(PocketPlayer player, MCPEServer server){
		this.player = player;
		this.server = server;
	}
	
	@Override
	public final void run() {
		System.out.println("ChunkSender start.");
		x = (int) player.x;
		z = (int) player.z;
		
		centerX = server.world.getChunkAt(x, z).getX();
		centerZ = server.world.getChunkAt(x, z).getZ();
		
		//FullChunkDataPacket dp = new FullChunkDataPacket(server.world.getChunkAt((int) x, (int) z), server.world);
		//player.addToQueue(dp);
		int cornerX = centerX - 48;
		int cornerZ = centerZ + 48;
		
		x = cornerX;
		z = cornerZ;
		
		int chunkNum = 0;
		try{
			while(chunkNum < 49){
				System.out.println("ChunkSender chunk "+x+", "+z);
				FullChunkDataPacket dp = new FullChunkDataPacket(server.world.getChunkAt(x, z), server.world);
				player.addToQueue(dp);
				
				if(x < cornerX + 112){
					x = x + 16;
				} else {
					x = cornerX;
					z = z - 16;
				}
				chunkNum++;
				Thread.sleep(100);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("ChunkSender end.");
	}

}
