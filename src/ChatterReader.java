/* 
 * John Wittrock, Greg Herpel, 2012
 * A class to read from a socket and pass it up to a chat room. 
 * Will eventually be refactored to 
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChatterReader extends ChatterHandler {
	String name;
	
	public ChatterReader(Server server, Socket sock, String name) {
		super(server, sock);
		this.name = name;
	}
    
	public void run() {
		if(sock == null || server == null) {
			return;
		}
		try{
			BufferedReader w = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			System.out.println("ChatterReader started...");
			String str = null;
			while ((str = w.readLine()) != null) { // While the stream is still open...
				System.out.println("Got message: " + str);
				server.addMessage("" + this.name + ": " + str  +"\n"); // Send the message on up to the server.
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Do something here and make sure the server removes this particular chatter. 
		server.removeChatter(this.name);
		
		System.out.println("Chatter " + this.name + "  exited.");
	}

}