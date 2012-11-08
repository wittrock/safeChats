/*
 * John Wittrock and Greg Herpel, 2012
 * This is the main class for the server end of things.
 */

import java.util.*;
import java.net.ServerSocket;
import java.net.Socket;
import javax.net.ServerSocketFactory;
import javax.net.ssl.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.security.*;

public class Server {
	private static final int PORT = 46754;
	private List<Chatter> chatters; // A list of all the chatters in this simplistic, one-room chat system.
	private LinkedBlockingQueue<Message> writeBuffer; // All of the messages to be sent out
	private Map<Integer, ChatRoom> rooms;
	private HashMap<String, byte[]> authData;
	private HashMap<String, byte[]> userSalt;
	private SecureRandom random;

	public Server() {
		System.out.println("Started the server...");
		chatters = Collections.synchronizedList(new ArrayList<Chatter>()); //Chatters is a synchronized list. 
		rooms = Collections.synchronizedMap(new HashMap<Integer, ChatRoom>());
		
		writeBuffer = new LinkedBlockingQueue<Message>();
		
		try {
		random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Cannot instantiate SecureRandom with SHA1PRNG");
			e.printStackTrace();
		}	

		try{
			// Start accepting requests. 
			ServerSocketFactory f = SSLServerSocketFactory.getDefault(); 
			ServerSocket ss = f.createServerSocket(PORT);
	
			// For now, this will also be the name of the chatter. 
			int numChatters = 0;
	
			// The thread that will actively poll the queue and distribute to chatters. 
			BufferPusher bufferPusher = new BufferPusher(chatters, writeBuffer, this
); 
			(new Thread(bufferPusher)).start();

			// Accept connections forever, stick them on the chatters list. 
			while(true) {
				Socket s = ss.accept();
				System.out.println("Got a new connection!");
				String name = Integer.toString(numChatters);

				Chatter chatter = new Chatter(name);
				ChatterWriter writer = new ChatterWriter(this, s);
				ChatterReader reader = new ChatterReader(this, s, chatter);
				chatter.setReader(reader);
				chatter.setWriter(writer);
				chatters.add(chatter);
				// chatters.add(new Chatter(name,
				// 			 new ChatterReader(this, s, name),
				// 			 new ChatterWriter(this, s)));
				numChatters++;
		    
			}
		} catch (Exception e) { // We know this sucks, we just wanted to get something out the door. Will be fixed in next version. 
			e.printStackTrace();
			return;
		}
	}

	/*
	 * The function that adds a message to the queue of this server. 
	 * This will be moved to the ChatRoom in the next version.
	 */
	public void addMessage(Message msg) { 
		try {
			writeBuffer.put(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Chatter getChatterByName(String name) {
		for (Chatter c : chatters) {
			if (c.name.equals(name)) {
				return c;
			}
		}
		return null;
	}

	// MAJOR SECURITY FLAW HERE. MAKE SURE THAT THE CHATTER WAS INVITED FIRST
	public boolean addChatterToRoom(Chatter c, int roomId) {
		ChatRoom room = rooms.get(roomId);
		if (room == null) {
			return false;
		}
		
		room.addChatter(c);
		return true;
	}

	public boolean removeChatterFromRoom(Chatter c, int roomID) {
		ChatRoom room = rooms.get(roomID);
		if (room == null) {
			return false;
		}
		
		room.removeChatter(c);
		return true;
	}

	public void createRoom(Chatter owner) {
		int roomId;
		while(true) {
			roomId = random.nextInt();
			if(!rooms.containsKey(roomId)) {
				break;
			}			
		}

		ChatRoom room = new ChatRoom(roomId, owner);
		rooms.put(roomId, room);
		owner.addMessage("CREATED " + roomId + "$ ");
	}

	public void removeRoom(int id) {
		rooms.remove(id);
	}

	// This should very much be done with a direct reference for the sake of security.
	// For now, this will suffice.
	public void removeChatter(String name) {
		for (int i = 0; i < chatters.size(); i++) {
			if (chatters.get(i).getName().equals(name)) {
				Chatter c = chatters.remove(i);
				c.stopAll();
				break;
			}
		}
		System.out.println("Removed a chatter. Num chatters: " + chatters.size());
	}
	
	public void authUser(String userName, String password){
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			byte[] b = password.getBytes();
			
			byte[] salt = userSalt.get(userName);
			byte[] hash = authData.get(userName);
			
			if(hash == null || salt == null);
				//call auth failed
			
			md.update(salt);
			byte[] h = md.digest(b);
			
			if(h.equals(hash));
				//call auth accepted
			else;
				//call auth failed
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
	}

	/* Main method. Not much else to say about it. */
	public static void main(String[] args) { 
		Server server = new Server();
	}
	
}