package mcbridge.network.minecraft;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import mcbridge.network.PacketRuntimeException;
import mcbridge.network.chunk.ChunkSection;
import mcbridge.network.chunk.ChunkUtils;
import mcbridge.network.chunk.WholeChunk;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;

/**
 * Represents a FullChunkData Packet.
 * @author jython234
 *
 */
public class FullChunkDataPacket implements MinecraftPacket {
	
	public final static int UNCOMPRESSED_CHUNK_LENGTH = (0x04 * 2) + 0x8000 + (0x4000*3) + 0x100 + 0x400;
	public static final byte[] BIOME_COLOR = new byte[]{0x00 ,(byte) 0x85 ,(byte) 0xb2 ,0x4a};
	
	public static final byte PID = MinecraftConstants.FULL_CHUNK_DATA_PACKET;
	private Chunk chunk;
	private ChunkSnapshot snapshot;
	private World world;
	
	private ByteBuffer buf;
	
	private Deflater deflater = new Deflater(7);
	
	public FullChunkDataPacket(Chunk chunk, World world){
		this.chunk = chunk;
		this.snapshot = chunk.getChunkSnapshot();
		this.world = world;
		
	}
	
	public void encode(){
		/*
		ByteBuffer afterBuffer = ByteBuffer.allocate(0x100 + 0x400);
		int last = 0x100;
		while( afterBuffer.position() != last ) {
			afterBuffer.put((byte) 0xff);
		}
		last += 0x400;
		while( afterBuffer.position() != last ) {
			afterBuffer.put(BIOME_COLOR);
		}
		*/
		byte[] compressed;
		try{
			
			//16384
			ByteBuffer bb = ByteBuffer.allocate(83208).order(ByteOrder.LITTLE_ENDIAN);
			//ByteBuffer bb = ByteBuffer.allocate(UNCOMPRESSED_CHUNK_LENGTH).order(ByteOrder.LITTLE_ENDIAN); //About 81mb :D
			//ByteBuffer bb = ByteBuffer.allocate(8 + 0x1000 + 0x800 * 3 + 0x100 + 0x500).order(ByteOrder.LITTLE_ENDIAN);
			
			bb.putInt(chunk.getX());
			bb.putInt(chunk.getZ());
			
			//bb.order(ByteOrder.BIG_ENDIAN);
			
			WholeChunk whole = new WholeChunk();
			whole.createFromChunk(chunk, world);
			
			bb.put(whole.blocks);
			bb.put(whole.metaData);
			bb.put(whole.skylight);
			bb.put(whole.blocklight);
			
			/*
			for(int i = 0; i < 8; i++){
				ChunkSection section = new ChunkSection();
				section.createFromChunk(chunk, world, i);
				bb.put(section.blocks); //Block IDs
				bb.put(section.metaData); //Block Damages
				bb.put(section.blocklight); //Block Light
				bb.put(section.skylight); //Block SkyLight
			}
			*/
			bb.put(whole.biomeIds); //Biome ids
			bb.put(whole.biomeColors);
			//bb.put(new byte[256]);
			
			/*
			for(int i: new byte[256]){ //Biome colors
				bb.put((byte) (i & 0x0F));
				bb.put((byte) ((i << 8) & 0x0F));
				bb.put((byte) ((i << 16) & 0x0F));
				bb.put((byte) ((i << 24) & 0x0F));
			}
			*/
			
			compressed = compressData(bb.array());
		}
		catch(Exception e){
			e.printStackTrace();
			throw new PacketRuntimeException("Failed to encode packet! "+e.getMessage());
		}
		buf = ByteBuffer.allocate(5 + compressed.length);
		buf.put(PID);
		//buf.putInt(chunk.getX());
		//buf.putInt(chunk.getZ());
		//buf.put((byte) 0x78);
		//buf.put((byte) 0x01);
		buf.put(compressed);
		buf.putInt(deflater.getAdler());
	}
	
	public void decode(){ }
	
	public ByteBuffer getBuffer(){
		return buf;
	}
	
	private byte[] compressData(byte[] data) throws IOException{
		deflater.reset();
		deflater.setInput(data);
		deflater.finish();
		byte[] compressed = new byte[65536];
		int length = deflater.deflate(compressed);
		return ArrayUtils.subarray(compressed, 0, length);
	}

}
