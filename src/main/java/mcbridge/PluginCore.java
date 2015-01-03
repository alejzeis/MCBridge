package mcbridge;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

/**
 * The core of the plugin.
 * @author jython234
 *
 */
public class PluginCore extends JavaPlugin {
	public final static String version = "0.1.0-SNAPSHOT";
	
	public final Server server;
	public final Logger logger;
	
	public World defaultWorld;
	public FileConfiguration config;
	
	//Plugin Config Properties
	public String worldName;
	public boolean enableBridgeChat;
	public int maxPEPlayers;
	public boolean allowPCCommands;
	public String serverName;
	public String motd;
	public int gamemode;
	
	protected MCPEServer peServer;
	protected ProtocolManager protocolManager;
	
	
	public PluginCore(){
		server = getServer();
		logger = getLogger();
	}
	
	/**
	 * DO NOT CALL THIS!
	 * This is a method to be called once the plugin is enabled by bukkit.
	 */
	public void onEnable(){
		File dataFolder = getDataFolder();
		if(dataFolder.exists() && dataFolder.isDirectory()){ }
		else {
			dataFolder.mkdir();
		}
		this.saveDefaultConfig();
		loadConfig();
		logger.info("Config loaded.");
		
		if(!(server.getWorld(worldName).getMaxHeight() <= 128)){
			logger.warning("ATTENTION!");
			logger.warning("The maximum build height for world "+worldName+" is over 128 blocks");
			logger.warning("The current height is: "+server.getWorld(worldName).getMaxHeight());
			logger.warning("MCPE clients were designed for a max height of 128 blocks.");
			logger.warning("The software is not responsible for any problems related to this.");
		}
		
		protocolManager = ProtocolLibrary.getProtocolManager();
		
		server.getPluginManager().registerEvents(new GlobalListener(this), this);
		
		peServer = new MCPEServer(19132, this);
		peServer.Start();
		
		BukkitTask pingTask = new PingCheckerTask(peServer, this).runTaskTimer(this, 0, 40);
		
		logger.info("MCBridge enabled.");
	}
	
	/**
	 * DO NOT CALL THIS!
	 * This is a method to be called once the plugin is disabled by bukkit.
	 */
	public void onDisable(){
		//TODO: On disable
		try {
			peServer.Stop();
		} catch (InterruptedException e) {
			logger.severe("Failed to stop server thread.");
			e.printStackTrace();
		} finally {
			logger.info("MCBridge disabled.");
		}
	}
	
	private void loadConfig(){
		config = getConfig();
		
		worldName = config.getString("worldName");
		allowPCCommands = config.getBoolean("allowPCCommands");
		serverName = config.getString("serverName");
		maxPEPlayers = config.getInt("maxPEPlayers");
		enableBridgeChat = config.getBoolean("enableBridgeChat");
		motd = config.getString("motd");
		gamemode = config.getInt("defaultGamemode");
	}

}
