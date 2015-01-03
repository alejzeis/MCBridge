package mcbridge.network;

import mcbridge.MCPEServer;

public class PEChatManager {
	private MCPEServer server;
	
	public PEChatManager(MCPEServer server){
		this.server = server;
	}
	
	public void broadcastPE(String message){
		//TODO
	}
	
	public void broadcastPC(String message){
		server.getPlugin().server.broadcastMessage(message);
	}
	
	public void broadcast(String message){
		broadcastPE(message);
		broadcastPC(message);
	}

}
