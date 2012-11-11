import java.io.*;
import java.util.*;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatRoom {
	
	private List<Chatter> chatters; // A list of all the chatters in this simplistic, one-room chat system.
	private List<Chatter> invited;
	
	private LinkedBlockingQueue<String> writeBuffer; // All of the messages to be sent out. 
	private int id;
	private Chatter owner;
	
	public ChatRoom(int id, Chatter owner) {
		this.chatters = Collections.synchronizedList(new ArrayList<Chatter>());
		this.invited = Collections.synchronizedList(new ArrayList<Chatter>());
		this.owner = owner;
		this.id = id;
		chatters.add(owner);
	}
	
	public boolean addChatter(Chatter c) {
		if (invited.contains(c)) {
			System.out.println("Found invited chatter: " + c.getName());
			this.distributeMessage("CHTR_ADDED " + c.getName() + " " + Integer.toString(this.id) + " $ ");
			chatters.add(c);
			invited.remove(c);


			c.addMessage("JOINED " + Integer.toString(this.id) + " $ ");
			for (Chatter chatter : chatters) {
				// makes the assumption that names are unique. 
				if (!chatter.getName().equals(c.getName())) { 
					c.addMessage("CHTR_ADDED " + chatter.getName() + " " + Integer.toString(this.id) + " $ ");
				}
			}

			
			return true;
		}
		return false;
	}

	public void inviteChatter(Chatter c) {
		invited.add(c);
	}

	public void removeChatter(Chatter c) {
		chatters.remove(c);
	}

	public boolean containsChatter(Chatter c) {
		return chatters.contains(c);
	}

	public Chatter getOwner() {
		return owner;
	}

	public int size() {
		return chatters.size();

	}

	public void distributeMessage(String message) {
		System.out.println("Pushing a message to chatters: " + message);
		for(Chatter c : this.chatters) {
			c.addMessage(message);
		}

	}

}