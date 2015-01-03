package mcbridge;

import mcbridge.network.wrappers.WrapperPlayServerChat;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class GlobalListener implements Listener {
	private PluginCore plugin;
	
	public GlobalListener(PluginCore core){
		plugin = core;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onJoin(PlayerJoinEvent evt){
		plugin.peServer.chatManager.broadcastPE("[PC]: "+evt.getPlayer().getPlayerListName()+" joined the game.");
		
		WrapperPlayServerChat chatPkt = new WrapperPlayServerChat();
		chatPkt.setMessage(WrappedChatComponent.fromText("Hello welcome!"));
		
		chatPkt.sendPacket(evt.getPlayer());
	}

}
