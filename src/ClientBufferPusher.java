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
	
	private void printMessage(String message, String chat){
		Integer chatID = Integer.valueOf(chat);
		GUI_ChatInterface gci = chats.get(chatID);
		gci.addChatText(message);
	}
	
	private void newChat(String chat){
		Integer chatID = Integer.valueOf(chat);
		GUI_ChatInterface gci = new GUI_ChatInterface(client);
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
	
	private void HandleMessage(String[] message){
		String type = "",vars ="",body = "";
		try{
			type = message[0];
			vars = message[1];
			body = message[2];
			
			
			
		}catch(IndexOutOfBoundsException e){
			System.err.println("The client received an illegal statement");
			return;
		}
	}
	
	public void run(){
		while(true) {
			String str = getMessage();
			String[] message =  str.split(String.valueOf(delim));
			HandleMessage(message);
		}
	}
}
