/* 
 * John Wittrock, Greg Herpel, 2012
 * A class to read from a socket and pass it up to a chat room. 
 * Will eventually be refactored to 
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChatterReader extends ChatterHandler {
	Chatter chatter;
	
	public ChatterReader(Server server, Socket sock, Chatter chatter) {
		super(server, sock);
		this.chatter = chatter;
	}

	public void run() {
		if(sock == null || server == null) {
			System.out.println("Error initializing ChatterReader");
			return;
		}
		try{
			System.out.println("ChatterReader started...");
			BufferedReader w = new BufferedReader(new InputStreamReader(sock.getInputStream()));

			String str = null;
			while ((str = w.readLine()) != null) { // While the stream is still open
				System.out.println("Got message: " + str);
				//				server.addMessage(new Message("" + this.chatter.getName() + ": " + str  +"\n", this.chatter)); // Send the message on up to the server.
				server.addMessage(new Message(str, this.chatter));
			}
		} catch (Exception e) {
			System.out.println("Chatter Exited");
		}

		server.removeChatter(this.chatter.getName());
		
		System.out.println("Chatter " + this.chatter.getName() + "  exited.");
	}

}