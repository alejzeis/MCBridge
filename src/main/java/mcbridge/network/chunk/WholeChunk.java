package mcbridge.network.chunk;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.block.Block;

public class WholeChunk {
	
	public byte[] blocks = new byte[32768];
	public byte[] metaData = new byte[16384]; //Nibble Array
	public byte[] blocklight = new byte[16384]; //Nibble Array
	public byte[] skylight = new byte[16384]; //Nibble Array
	
	public byte[] biomeIds = new byte[256];
	public byte[] biomeColors = new byte[1024];
	
	@SuppressWarnings("deprecation")
	public void createFromChunk(Chunk chunk, World world){
		//System.out.println("WholeChunk Start: "+chunk.getX()+", "+chunk.getZ());
		
		ChunkSnapshot snap = chunk.getChunkSnapshot();
		int aoffset = 0;
		int biomeOffset = 0;
		boolean go = true;
		//Block Ids
		for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 128; y++) {
                	if(!go){ break; }
                	//System.out.println("WholeChunk, X: "+x+", Y: "+y+", Z: "+z);
                	//Block block = chunk.getBlock(x, y, z);
                	try{
                		blocks[aoffset] = (byte) snap.getBlockTypeId(x, y, z);
                	} catch(ArrayIndexOutOfBoundsException e){
                		go = false;
                		break;
                	}
                	
                	/*
                	ChunkUtils.setNibble((byte) x, (byte) y, (byte) z, (byte) block.getData(), metaData);
                	ChunkUtils.setNibble((byte) x, (byte) y, (byte) z, (byte) block.getLightLevel(), blocklight);
                	ChunkUtils.setNibble((byte) x, (byte) y, (byte) z, (byte) block.getLightFromSky(), skylight);
                	*/
                	aoffset++;
                }
                if(!go){ break; }
                //biomeIds[biomeOffset] = 1;
                biomeOffset++;
            }
            if(!go){ break; }
        }
		
		//Metadata
		aoffset = 0;
		for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 128; y += 2) {
                    byte data = 0;
                    data = (byte) ((snap.getBlockData(x, y, z) & 0xF) << 4);
                    data |= snap.getBlockData(x, y + 1, z) & 0xF;
                    metaData[aoffset] = data;
                    aoffset++;
                }
            }
        }
		//Block Light
		aoffset = 0;
		for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 128; y += 2) {
                    byte data = 0;
                    data = (byte) ((snap.getBlockEmittedLight(x, y, z) & 0xF) << 4);
                    data |= snap.getBlockEmittedLight(x, y + 1, z) & 0xF;
                    blocklight[aoffset] = data;
                    aoffset++;
                }
            }
        }
		//SkyLight
		aoffset = 0;
		for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 128; y += 2) {
                    byte data = 0;
                    data = (byte) ((snap.getBlockSkyLight(x, y, z) & 0xF) << 4);
                    data |= snap.getBlockSkyLight(x, y + 1, z) & 0xF;
                    skylight[aoffset] = data;
                    aoffset++;
                }
            }
        }
		//Biome Ids
		for(int i = 0; i < 256; i++){
			biomeIds[i] = (byte) 0xFF;
		}
		//Biome Colors
		for(int i = 0; i < 1024; i = i + 4){
			biomeColors[i] = 0x00;
			biomeColors[i + 1] = (byte) 0x85;
			biomeColors[i + 2] = (byte) 0xB2;
			biomeColors[i + 3] = 0x4A;
		}
		
		//System.out.println("WholeChunk OUT.");
		
	}

}
