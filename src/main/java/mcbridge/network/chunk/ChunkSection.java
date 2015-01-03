package mcbridge.network.chunk;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;

/**
 * Represents a chunk section.
 * @author jython234
 *
 */
public class ChunkSection {
	
	public byte[] blocks;
	public byte[] metaData; //Nibble Array
	public byte[] blocklight; //Nibble Array
	public byte[] skylight; //Nibble Array
	
	@SuppressWarnings("deprecation")
	public void createFromChunk(Chunk chunk, World world, int offset){
		blocks = new byte[4096];
		metaData = new byte[2048];
		blocklight = new byte[2048];
		skylight = new byte[2048];
		
		ChunkSnapshot snap = chunk.getChunkSnapshot();
		offset = offset * 16;
		
		int aoffset = 0;
		for (int x = chunk.getX(); x < 16; x++) {
            for (int z = chunk.getZ(); z < 16; z++) {
                for (int y = offset; y < offset + 16; y++) {
                	System.out.println("ChunkSection offset "+offset+", X: "+x+", Y: "+y+", Z: "+z);
                	blocks[aoffset] = (byte) snap.getBlockTypeId(x, y, z);
                	ChunkUtils.setNibble((byte) x, (byte) y, (byte) z, (byte) snap.getBlockData(x, y, z), metaData);
                	ChunkUtils.setNibble((byte) x, (byte) y, (byte) z, (byte) snap.getBlockEmittedLight(x, y, z), blocklight);
                	ChunkUtils.setNibble((byte) x, (byte) y, (byte) z, (byte) snap.getBlockSkyLight(x, y, z), skylight);
                	aoffset++;
                }
            }
        }
	}

}
