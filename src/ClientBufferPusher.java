import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

/*
 * Greg Herpel, John Wittrock, 2012
 * This class is a buffer for handling messages from the server
 */

public class ClientBufferPusher {
	
	private LinkedBlockingQueue<String> writeBuffer;
	private HashMap<Integer,GUI_ChatInterface> chats;
	private GUI_SignIn gsi;
	private Client client;
	private final char delim = (char)254;
	
	public ClientBufferPusher(Client c, GUI_SignIn gsi){
		this.writeBuffer = new LinkedBlockingQueue<String>();
		this.chats = new HashMap<Integer,GUI_ChatInterface>();
		this.client = c;
		this.gsi = gsi;
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
		Boolean authed = Boolean.valueOf(bool);
		//add if authed, open main menu
		//otherwise display message on gsi
	}
	
	private void USR_ADDED(String chat, String user){
		
	}
	private void USR_LEFT(String chat, String user){
		
	}
	
	private void CHTR_ADDED(String chat, String user){
		
	}
	private void CHTR_LEFT(String chat, String user){
		
	}
	
	private void handleMessage(String message){
		try{
			
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
				newChat(id);
			} else {
				// Toss this.
				return;
			}
			
		}catch(IndexOutOfBoundsException e){
			System.err.println("The client received an illegal statement");
			return;
		}
	}
	
	public void run(){
		while(true) {
			String str = getMessage();
			//String[] message =  str.split(String.valueOf(delim));
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
		//		synchronized(writeBuffer) {
		try {
		    	return writeBuffer.take();
		} catch (InterruptedException e) {
		    	System.out.println("BufferPusher InterruptedException!");
		    	e.printStackTrace();
		    	return null;
		}
		//		}
	}
}
