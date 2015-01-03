package mcbridge.network;

import java.io.IOException;
import java.util.Arrays;

import mcbridge.PocketPlayer;

public class HandlePacketWorker extends Thread {
	
	private PocketPlayer player;
	private CustomPacket cp;
	
	public HandlePacketWorker(PocketPlayer player, CustomPacket cp){
		this.player = player;
		this.cp = cp;
	}
	
	@Override
	public void run(){
		cp.decode();
		try {
			//System.out.println("Handling custom packet: "+Arrays.toString(cp.getBuffer().array()));
			player.handlePacket(cp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
