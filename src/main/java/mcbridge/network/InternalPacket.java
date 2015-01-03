package mcbridge.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import mcbridge.PocketPlayer;
import mcbridge.network.minecraft.MinecraftPacket;

/*
 * This code is originally from the BlockServerProject (https://github.com/BlockServerProject/BlockServer/blob/master/src/java/org/blockserver/network/raknet/InternalPacket.java)
 * 
 *  BlockServer
    Copyright (C) 2014  BlockServerProject

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

public class InternalPacket{
	public byte[] buffer;
	public byte reliability;
	public boolean hasSplit;
	public int messageIndex = -1;
	public int orderIndex = -1;
	public byte orderChannel = (byte)0xff;
	public int splitCount;
	public short splitID;
	public int splitIndex;
	
	private InternalPacket() { }
	
	public static InternalPacket fromBinary(ByteBuffer bb){
		 try{	
	            InternalPacket packet = new InternalPacket();

	            int flags = bb.get();
	            packet.reliability = (byte) ((flags & 0b11100000) >> 5);
	            packet.hasSplit = (flags & 0b00010000 >> 4) > 0;

	            int length = (bb.getShort() & 0xFFFF) / 8;
	            if (packet.reliability == 2 || packet.reliability == 3 || packet.reliability == 4 || packet.reliability == 6 || packet.reliability == 7) {
	                packet.messageIndex = NetworkUtils.getLTriad(bb);
	            }

	            if (packet.reliability == 1 || packet.reliability == 3 || packet.reliability == 4 || packet.reliability == 7) {
	                packet.orderIndex = NetworkUtils.getLTriad(bb);
	                packet.orderChannel = (byte) (bb.get() & 0xFF);
	            }

	            if (packet.hasSplit) {
	                packet.splitCount = bb.getInt();
	                packet.splitID = bb.getShort();
	                packet.splitIndex = bb.getInt();
	            }
	            packet.buffer = new byte[length];
	            bb.get(packet.buffer);

	            return packet;
	        } catch (Exception e) {
	            return null;
	        }
	}
	
	/*
	public static InternalPacket[] fromBinary(byte[] buffer){
		ByteBuffer bb = ByteBuffer.wrap(buffer);
		ArrayList<InternalPacket> list = new ArrayList<>();
		while(bb.position() < bb.capacity()) {
			InternalPacket pck = new InternalPacket();
			byte flag = bb.get();
			pck.reliability = (byte) (flag >> 5);
			pck.hasSplit = (flag & 0b00010000) == 16;
			int length = ((bb.getShort() + 7) >> 3); // The Length is in bits, so Bits to Bytes conversion
			if(pck.reliability == 2 || pck.reliability == 3 || pck.reliability == 4 || pck.reliability == 6 || pck.reliability == 7){
				pck.messageIndex = NetworkUtils.getLTriad(buffer, bb.position());
				bb.position(bb.position() + 3);
			}
			if(pck.reliability == 1 || pck.reliability == 3 || pck.reliability == 4 || pck.reliability == 7){
				pck.orderIndex = NetworkUtils.getLTriad(buffer, bb.position());
				bb.position(bb.position() + 3);
				pck.orderChannel = bb.get();
			}
			if(pck.hasSplit){
				pck.splitCount = bb.getInt();
				pck.splitID = bb.getShort();
				pck.splitIndex = bb.getInt();
			}
			pck.buffer = new byte[length];
			bb.get(pck.buffer);
		}
		InternalPacket[] result = new InternalPacket[list.size()];
		list.toArray(result);
		return result;
	}
	*/

	public int getLength(){
		return 3 + buffer.length + (messageIndex != -1 ? 3:0) + (orderIndex != -1 ? 4:0) +  (hasSplit ? 10:0);
	}

	public byte[] toBinary(){
		ByteBuffer bb = ByteBuffer.allocate(getLength());
		byte flag = 0;
		
		flag = (byte) (flag | reliability << 5);
		if(hasSplit){
			flag = (byte) ((flag & 0xFF) | 0x10);
		}
		bb.put(flag);
		bb.putShort((short) ((buffer.length << 3) & 0xFFFF));
		if(reliability == 2 || reliability == 3 || reliability == 4 || reliability == 6 || reliability == 7){
			bb.put(NetworkUtils.putLTriad(messageIndex));
		}
		if(reliability == 1 || reliability == 3 || reliability == 4 || reliability == 7){
			bb.put(NetworkUtils.putLTriad(orderIndex));
			bb.put((byte) (orderChannel & 0xFF));
		}
		if(hasSplit){
			bb.putInt(splitCount);
			bb.putShort((short) (splitID & 0xFFFF));
			bb.putInt(splitIndex);
		}
		bb.put(buffer);
		return bb.array();
	}
	
	public static InternalPacket[] fromMinecraftPacket(PocketPlayer player, MinecraftPacket packet, int reliability){
		byte[] data = packet.getBuffer().array();
		System.out.println("Length is: "+data.length+", mtu is: "+player.mtu);
		if(data.length + 34 < player.mtu){
			System.out.println("Not split!");
			InternalPacket ipk = new InternalPacket();
			ipk.reliability = (byte) reliability;
			ipk.hasSplit = false;
			ipk.messageIndex = player.getMessageIndex();
			ipk.buffer = data;
			player.setMessageIndex(player.getMessageIndex() + 1);
			return new InternalPacket[] {ipk};
			
		} else { //Need to split
			System.out.println("Split!");
			byte[][] multipleData = NetworkUtils.splitArray(data, player.mtu - 34);
			InternalPacket[] internalPackets = new InternalPacket[multipleData.length];
			
			int currentSplitID = player.getSplitID();
			int currentSplitCount = multipleData.length;
			
			int slice = 0;
			for(byte[] sliceData: multipleData){
				internalPackets[slice] = new InternalPacket();
				internalPackets[slice].reliability = (byte) reliability;
				internalPackets[slice].hasSplit = true;
				internalPackets[slice].splitID = (short) currentSplitID;
				internalPackets[slice].splitCount = currentSplitCount;
				internalPackets[slice].splitIndex = slice;
				internalPackets[slice].messageIndex = player.getMessageIndex();
				player.setMessageIndex(player.getMessageIndex() + 1);
				internalPackets[slice].buffer = sliceData;
				slice++;
			}
			
			player.setSplitID((player.getSplitID() + 1) % 65535);
			return internalPackets;
		}
	}
}
