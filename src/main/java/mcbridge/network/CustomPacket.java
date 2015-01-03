package mcbridge.network;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mcbridge.network.raknet.RakNetConstants;
import mcbridge.network.raknet.RakNetPacket;

/*
 * This code is originally from the BlockServerProject (https://github.com/BlockServerProject/BlockServer/blob/master/src/java/org/blockserver/network/raknet/CustomPacket.java)
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

public class CustomPacket implements RakNetPacket{
	public byte packetID;
	public int sequenceNumber;
	public List<InternalPacket> packets;
	
	private ByteBuffer bb;

	public CustomPacket(byte[] data){
		bb = ByteBuffer.wrap(data);
		packets = new ArrayList<InternalPacket>();
	}
	public CustomPacket(){
		packetID = RakNetConstants.DATA_PACKET_4;
		packets = new ArrayList<InternalPacket>();
	}

	public int getLength(){
		int length = 4; // PacketID + sequence number
		for(InternalPacket pck: this.packets){
			length += pck.getLength();
		}
		return length;
	}
	
	@Override
	public void decode(){
		packetID = bb.get();
		sequenceNumber = NetworkUtils.getLTriad(bb.array(), bb.position());
		
		bb.position(bb.position() + 3);
		
		packets = new ArrayList<InternalPacket>();
		while(bb.remaining() > 0){
			packets.add(InternalPacket.fromBinary(bb));
		}

		//byte[] data = new byte[bb.capacity() - 4];
		//bb.get(data);
		//packets = Arrays.asList(InternalPacket.fromBinary(data));
	}
	@Override
	public void encode(){
		bb= ByteBuffer.allocate(getLength());
		bb.put(this.packetID);
		bb.put(NetworkUtils.putLTriad(sequenceNumber));
		for(InternalPacket pck: packets){
			bb.put(pck.toBinary());
		}
	}
	
	public ByteBuffer getBuffer(){
		return bb;
	}
}
