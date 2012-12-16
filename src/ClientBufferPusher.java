import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Arrays;

/*
 * Greg Herpel, John Wittrock, 2012
 * This class is a processing and control class for all messages to the client. 
 * It reads messages sent to the server by clients and takes appropriate action. 
 * The name of this class is taken from the initial version of this program, because 
 * the server side implementation of this pushed
 * messages to client buffers. Now, it does something similar, but takes appropriate action
 * based on the content of the command section of messages. 
 */

public class ClientBufferPusher implements Runnable {
	
	private LinkedBlockingQueue<String> writeBuffer;
	private HashMap<Integer,GUI_ChatInterface> chats;
	private HashMap<Integer,GUI_Invite> invites;
	private GUI_Menu menu;
	private Client client;
	private boolean kill = false;
	
	public ClientBufferPusher(Client c){
		this.writeBuffer = new LinkedBlockingQueue<String>();
		this.chats = new HashMap<Integer,GUI_ChatInterface>();
		this.invites = new HashMap<Integer, GUI_Invite>();
		this.client = c;
	}
	
	
	private void printMessage(String message, String chat){
		Integer chatID = Integer.valueOf(chat);
		GUI_ChatInterface gci = chats.get(chatID);
		gci.addChatText(message);
	}
	
	private void newChat(String chat){
		Integer chatID = Integer.valueOf(chat);
		GUI_ChatInterface gci = new GUI_ChatInterface(client, chat);
		gci.setVisible(true);
		chats.put(chatID, gci);
	}
	
	public void leaveChat(String chat){
		Integer chatID = Integer.valueOf(chat);
		chats.remove(chatID);
	}
	
	public void removeInvite(String chat){
		invites.remove(Integer.valueOf(chat));
	}
	
	private void INVITE(String from){
		//add a GUI to ask if user wants to accept an invite from "from"
	}
	
	private void AUTH(String bool){
		boolean authed = Boolean.valueOf(bool);
		if(authed){
			client.authed();
			menu = new GUI_Menu(client);
			menu.setVisible(true);
		} else{
			client.authFailed();
		}
	}
	
	private void NEW_ACC(String bool){
		boolean authed = Boolean.valueOf(bool);
		if(authed){
			client.authed();
			menu = new GUI_Menu(client);
			menu.setVisible(true);
		}else{
			client.authFailed();
		}
	}
	
	private void USR_ADDED(String user){
		if (menu == null) return;
		menu.addUser(user);
	}
	private void USR_LEFT( String user){
		if (menu == null) return;
		menu.removeUser(user);
	}
	
	private void CHTR_ADDED(String chat, String user){
		
	}
	private void CHTR_LEFT(String chat, String user){
		
	}

	/* Protocol messages are arranged as follows:
	 * The first string will be a command. There will then be a series of 
	 * arguments, delimited by spaces, and then a '$' character, and then a possible user-message, 
	 * or an encrypted message. Note that the only command which takes a user message (in plaintext 
	 * or ciphertext) is the MSG command. 
	 */

	private void handleMessage(String message){
		try{
			//System.out.println("Got message: " + message);
			String str = message;
			
			// Parse the protocol stuff out
			int protocolEnd = str.indexOf((int)'$');
			if (protocolEnd == -1) {
				// Toss this out. 
				return;
			}
			
			String protocol = str.substring(0, protocolEnd);
			String userMessage = str.substring(protocolEnd + 1);
			
			String[] args = protocol.split(" ");
			String command = args[0];
			
			if(command.equals("CREATED")) {
				String id = args[1];
				System.out.println("Got a created command: " + id);
				newChat(id);
			} else if (command.equals("MSG")) {
				if (args.length < 2) {
					return;
				}
				
				int roomId = Integer.valueOf(args[1]);
				GUI_ChatInterface ci = chats.get(roomId);
				if (ci == null) {
					System.out.println("Could not find the room id: " + protocol);
					return;
				}
				
				if (args.length > 2) { 
					/* As stated in BufferPusher.java, the format is:
					 * args[0] is the MSG command
					 * args[1] is the roomID
					 * args[2] is the IV length in bytes
					 * args[3] is the MAC length in bytes
					 * args[4] is the sender's name.
					 */
					ci.decryptMessageAndDisplay(args[2], args[3], args[4], userMessage);
					return;
				}

				ci.addChatText(userMessage);

			} else if (command.equals("AUTH")){
				String bool = args[1];
				AUTH(bool);
			} else if (command.equals("NEW_ACC")){
				String bool = args[1];
				NEW_ACC(bool);
			} else if (command.equals("INVITE")) {
				if (args.length < 3) {
					return;
				}
				// maybe throw up an accept dialog here?
				if (!chats.containsKey(Integer.valueOf(args[2])) && !invites.containsKey(Integer.valueOf(args[2]))) {
					invites.put(Integer.valueOf(args[2]), new GUI_Invite(args[1],args[2],client,this));
					invites.get(Integer.valueOf(args[2])).setVisible(true);
					System.out.println("here");
				}
			} else if (command.equals("JOINED")) {
				newChat(args[1]);
				GUI_ChatInterface gci = chats.get(Integer.valueOf(args[1]));
				gci.notOwner();
				invites.remove(Integer.valueOf(args[1]));
			} else if  (command.equals("KICK")){
				if (args.length < 3) {
					return;
				}
				GUI_ChatInterface gci = chats.get(Integer.valueOf(args[2]));
				gci.dispose();
				GUI_KickNotice gkn = new GUI_KickNotice(args[2]);
				gkn.setVisible(true);
			}else if (command.equals("CHTR_ADDED")) {
				String chatter = args[1];
				int roomId = Integer.valueOf(args[2]);
				GUI_ChatInterface ci = chats.get(roomId);
				if (ci == null) { return; }
				ci.addChatter(chatter);

			} else if (command.equals("CHTR_LEFT")) {
				String chatter = args[1];
				int roomId = Integer.valueOf(args[2]);
				GUI_ChatInterface ci = chats.get(roomId);
				if (ci == null) { return; }
				ci.removeChatter(chatter);
			} else if (command.equals("USR_ADDED")){
				USR_ADDED(args[1]);
			} else if (command.equals("USR_LEFT")){
				USR_LEFT(args[1]);

			} else if (command.equals("BEGIN_ENC")) {
				int roomId = Integer.valueOf(args[1]);
				GUI_ChatInterface ci = chats.get(roomId);
				if (ci == null) return;
				
				ci.genKeys();

			} else if (command.equals("ZS")) {
				int roomId = Integer.valueOf(args[1]);
				GUI_ChatInterface ci = chats.get(roomId);
				if (ci == null) return;

				ci.broadcastX(args[2], args[3]);

			} else if (command.equals("XS")) {
				int roomId = Integer.valueOf(args[1]);
				GUI_ChatInterface ci = chats.get(roomId);
				if (ci == null) return;
				
				ci.receiveXs(args[2], args[3], Arrays.copyOfRange(args, 4, args.length));
				
			} else if (command.equals("RM_DESTROYED")) {
				int roomId = Integer.valueOf(args[1]);
				GUI_ChatInterface ci = chats.get(roomId);
				if (ci == null) { return; }
				ci.addChatText("Server: The room has been destroyed because the owner left. \nNo more messages will be accepted.\n");

			} else {
				// Toss this.
				System.out.println("Command not recognized");
				return;
			}
			
		} catch(IndexOutOfBoundsException e){
			System.err.println("The client received an illegal statement");
			return;
		}
	}
	
	public void run(){
		while(!kill) {
			String str = getMessage();
			System.out.println("Got message: " + str);
			handleMessage(str);
		}
	}
	
	public void addMessage(String str){
		try {
			writeBuffer.put(str);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
	}
	
	private String getMessage() {
		try {
		    	return writeBuffer.take();
		} catch (InterruptedException e) {
		    	System.out.println("BufferPusher InterruptedException!");
		    	e.printStackTrace();
		    	return null;
		}
	}
	
	public void killChats(){
		for(GUI_ChatInterface chat: chats.values()){
			client.sendMessage(("CHTR_LEFT " + chat.getChatID() + "$ ").toCharArray());
			chat.dispose();
		}
	}
	
	public void killProc(){
		kill = true;
	}
}
