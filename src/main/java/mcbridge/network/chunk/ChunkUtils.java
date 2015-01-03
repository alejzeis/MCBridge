package mcbridge.network.chunk;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class ChunkUtils {
	
	public final static byte[] compressByte(byte[]... compress) throws Exception{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Deflater deflater = new Deflater(7);
		DeflaterOutputStream dos = new DeflaterOutputStream(bos, deflater);
		for(byte[] ba: compress){
			dos.write(ba);
		}
		dos.close();
		byte[] buf = bos.toByteArray();
		bos.close();
		return buf;
	}
	
	public static void setNibble(byte x, byte y, byte z, byte nibble, byte[] buffer){
		x &= 0x0F;
		z &= 0x0F;
		y &= 0x7F;
		nibble &= 0x0F;
		int offset = (x << 10) + (z << 6) + (y >> 1);
		byte b = buffer[offset];
		if((y & 0x01) == 1){
			b &= 0x0F;
			b |= (nibble << 4);
		}
		else{
			b &= 0xF0;
			b |= nibble;
		}
		buffer[offset] = b;
	}
	public static byte getNibble(byte x, byte y, byte z, byte[] buffer){
		x &= 0x0F;
		z &= 0x0F;
		y &= 0x7F;
		int offset = (x << 10) + (z << 6) + (y >> 1);
		byte b = buffer[offset];
		if((y & 0x01) == 1){
			b >>= 4;
		}
		return b;
	}

}
