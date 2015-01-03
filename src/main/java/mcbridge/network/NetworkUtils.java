package mcbridge.network;

import java.nio.ByteBuffer;

import org.apache.commons.lang.ArrayUtils;

public class NetworkUtils {
	
	public static byte[] putLTriad(int i){
		byte[] triad = new byte[3];
		
		byte b1,b2,b3;
		b3 = (byte)(i & 0xFF);
		b2 = (byte)((i >> 8) & 0xFF);
		b1 = (byte)((i >> 16) & 0xFF);
		
		triad = new byte[] {b3, b2, b1};
		
		return triad;
	}
	
	public static int getLTriad(ByteBuffer bb){
		return (int) (bb.get() << 16 | bb.get() << 8 | bb.get());
	}
	
	public static int getLTriad(byte[] data, int offset){
		return (data[offset] & 0xff) | (data[offset+1] & 0xff) << 8 | (data[offset+2] & 0xff) << 16;
	}
	
	public static String getString(ByteBuffer bb){
		int len = bb.getShort();
		byte[] bytes = new byte[len];
		bb.get(bytes);
		
		return new String(bytes);
	}
	
	public static byte[][] splitArray(byte[] array, int singleSlice){
        if(array.length <= singleSlice){
            byte[][] singleRet = new byte[1][];
            singleRet[0] = array;
            return singleRet;
        }
        byte[][] ret = new byte[(array.length / singleSlice + (array.length % singleSlice == 0 ? 0 : 1))][];
        int pos = 0;
        int slice = 0;
        while(slice < ret.length){
            if(pos + singleSlice < array.length){
                ret[slice] = ArrayUtils.subarray(array, pos, singleSlice);
                pos += singleSlice;
                slice++;
            }else{
                ret[slice] = ArrayUtils.subarray(array, pos, array.length);
                pos += array.length - pos;
                slice++;
            }
        }
        return ret;
    }

}
