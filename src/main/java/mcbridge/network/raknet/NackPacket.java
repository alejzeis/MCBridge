package mcbridge.network.raknet;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mcbridge.network.NetworkUtils;

public class NackPacket implements RakNetPacket {
	
	public final static byte PID = RakNetConstants.RAKNET_NACK;
	public short unknown = 1;
	public byte additionalPacket;
	//public int packetNumber;
	//public int packetNumber2;
	public int[] packetNumbers;
	
	private ByteBuffer buf;
	
	public NackPacket(int[] counts){
		packetNumbers = counts;
	}
	
	public NackPacket(byte[] data){
		buf = ByteBuffer.wrap(data, 1, data.length - 1);
	}
	
	public void encode() {
		ByteBuffer bb = ByteBuffer.allocate(512);
		bb.position(3);
		Arrays.sort(packetNumbers);
		int count = packetNumbers.length;
		int records = 0;

		if(count > 0){
			int pointer = 1;
			int start = packetNumbers[0];
			int last = packetNumbers[0];
			while(pointer < count){
				int current = this.packetNumbers[pointer++];
				int diff = current - last;
				if(diff == 1){
					last = current;
				}
				else if(diff > 1){
					if(start == last){
						bb.put((byte) 0x01);
						bb.put(NetworkUtils.putLTriad(start));
						start = last = current;
					}
					else{
						bb.put((byte) 0x00);
						bb.put(NetworkUtils.putLTriad(start));
						bb.put(NetworkUtils.putLTriad(last));
						start = last = current;
					}
					++records;
				}
			}
			if(start == last){
				bb.put((byte) 0x01);
				bb.put(NetworkUtils.putLTriad(start));
			}
			else{
				bb.put((byte) 0x00);
				bb.put(NetworkUtils.putLTriad(start));
				bb.put(NetworkUtils.putLTriad(last));
			}
			++records;
		}
		int length = bb.position();
		bb.position(0);
		bb.put(PID);
		bb.putShort((short) records);
		byte[] buffer = Arrays.copyOf(bb.array(), length);
		buf = ByteBuffer.wrap(buffer);
		
	}
	
	public void decode() {
		int count = buf.getShort();
		List<Integer> packets = new ArrayList<Integer>();
		for(int i = 0; i < count && buf.position() < buf.capacity(); ++i){
			byte[] tmp = new byte[6];
			if(buf.get() == 0x00){
				buf.get(tmp);
				int start = NetworkUtils.getLTriad(tmp, 0);
				int end = NetworkUtils.getLTriad(tmp, 0);
				if((end - start) > 4096){
					end = start + 4096;
				}
				for(int c = start; c <= end; ++c){
					packets.add(c);
				}
			}
			else{
				buf.get(tmp, 0, 3);
				packets.add(NetworkUtils.getLTriad(tmp, 0));
			}
		}

		packetNumbers = new int[packets.size()];
		for(int i = 0; i < this.packetNumbers.length; i++){
			packetNumbers[i] = packets.get(i);
		}
	}
	
	public ByteBuffer getBuffer(){
		return buf;
	}

}
