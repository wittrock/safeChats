import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

/*
 * Greg Herpel, John Wittrock, 2012
 * This class is a buffer for handling messages from the server
 */

public class ClientBufferPusher implements Runnable {
	
	private LinkedBlockingQueue<String> writeBuffer;
	private HashMap<Integer,GUI_ChatInterface> chats;
	private GUI_Menu menu;
	private Client client;
	private final char delim = (char)254;
	
	public ClientBufferPusher(Client c){
		this.writeBuffer = new LinkedBlockingQueue<String>();
		this.chats = new HashMap<Integer,GUI_ChatInterface>();
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
	
	private void INVITE(String from){
		//add a GUI to ask if user wants to accept an invite from "from"
	}
	
	private void AUTH(String bool){
		boolean authed = Boolean.valueOf(bool);
		if(authed){
			client.authed();
			menu = new GUI_Menu(client);
			menu.setVisible(true);
		}else{
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
	
	private void USR_ADDED(String chat, String user){
		
	}
	private void USR_LEFT(String chat, String user){
		
	}
	
	private void CHTR_ADDED(String chat, String user){
		
	}
	private void CHTR_LEFT(String chat, String user){
		
	}
	
	private void joinChat(String chat) {
		client.sendMessage("JOIN " + chat + " $ ");
	}

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
				String chatId = args[2];

				// maybe throw up an accept dialog here?
				joinChat(args[2]);
			} else if (command.equals("JOINED")) {
				newChat(args[1]);
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
		while(true) {
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
}
