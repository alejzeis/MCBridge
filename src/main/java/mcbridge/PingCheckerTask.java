package mcbridge;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class PingCheckerTask extends BukkitRunnable {
	
	private PluginCore plugin;
	private MCPEServer server;
	
	public PingCheckerTask(MCPEServer server, PluginCore plugin){
		this.server = server;
		this.plugin = plugin;
	}
	
	@Override
	public void run(){
		ArrayList<PocketPlayer> players = server.getConnectedPlayers();
		
		for(int i=0; i < players.size(); i++){
			PocketPlayer player = players.get(i);
			if(player.hasStartedPing){
				if(player.pingTimer <= 0){
					//plugin.logger.info(player.name+"["+player.address+", EID: "+player.entityID+"] has logged out due to timeout.");
					plugin.peServer.chatManager.broadcastPC(ChatColor.AQUA+"[MCB]: "+ChatColor.YELLOW+player.name+" has left the game.");
					plugin.peServer.chatManager.broadcastPE("[MCB]: "+player.name+" has left the game.");
					player.close("ping timeout.");
				}
				player.pingTimer = player.pingTimer - 1;
			}
		}
	}

}
