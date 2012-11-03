import java.io.*;
import java.util.*;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatRoom {
	
	private List<Chatter> chatters; // A list of all the chatters in this simplistic, one-room chat system.
	
	private LinkedBlockingQueue<String> writeBuffer; // All of the messages to be sent out. 
	private int id;
	private Chatter owner;
	
	public ChatRoom(int id, Chatter owner) {
		this.chatters = Collections.synchronizedList(new ArrayList<Chatter>());
		this.owner = owner;
		chatters.add(owner);
	}
	
	public void addChatter(Chatter c){
		chatters.add(c);
	}

	public void removeChatter(Chatter c){
		chatters.remove(c);
	}

	

}