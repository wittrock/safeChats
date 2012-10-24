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

public class Server {
	private static final int PORT = 46754;
	private List<Chatter> chatters; // A list of all the chatters in this simplistic, one-room chat system.
	private LinkedBlockingQueue<String> writeBuffer; // All of the messages to be sent out. 

	public Server() {
		System.out.println("Started the server...");
		//Should probably make the writebuffer synchronized too. Would solve a lot of potential threading problems.
		chatters = Collections.synchronizedList(new ArrayList<Chatter>()); //Chatters is a synchronized list. 
		writeBuffer = new LinkedBlockingQueue<String>();
	
		try{
			// Start accepting requests. 
			ServerSocketFactory f = SSLServerSocketFactory.getDefault(); 
			ServerSocket ss = f.createServerSocket(PORT);
	
			// For now, this will also be the name of the chatter. 
			int numChatters = 0;
	
			// The thread that will actively poll the queue and distribute to chatters. 
			BufferPusher bufferPusher = new BufferPusher(chatters, writeBuffer); 
			(new Thread(bufferPusher)).start();

			// Accept connections forever, stick them on the chatters list. 
			while(true) {
				Socket s = ss.accept();
				System.out.println("Got a new connection!");
				String name = Integer.toString(numChatters);
				chatters.add(new Chatter(name,
							 new ChatterReader(this, s, name),
							 new ChatterWriter(this, s)));
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
	public void addMessage(String str) { 
		synchronized(writeBuffer) { // Make sure we aren't going to screw anything. 
			try {
				writeBuffer.put(str);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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

	/* Main method. Not much else to say about it. */
	public static void main(String[] args) { 
		Server server = new Server();
	}
}