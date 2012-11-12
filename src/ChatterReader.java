/* 
 * John Wittrock, Greg Herpel, 2012
 * A class to read from a socket and pass it up to a chat room. 
 * Will eventually be refactored to 
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class ChatterReader extends ChatterHandler {
	Chatter chatter;
	private Logger log;
	
	public ChatterReader(Server server, Socket sock, Chatter chatter) {
		super(server, sock);
		this.chatter = chatter;
		log = Logger.getLogger(ChatterReader.class);
		PropertyConfigurator.configure("log4j.properties");
	}

	public void run() {
		if(sock == null || server == null) {
			log.error("Error initializing ChatterReader");
			return;
		}
		try{
			BufferedReader w = new BufferedReader(new InputStreamReader(sock.getInputStream()));

			String str = null;
			while ((str = w.readLine()) != null) { // While the stream is still open
				log.trace(chatter.getName()+": Received a message from");
				//				server.addMessage(new Message("" + this.chatter.getName() + ": " + str  +"\n", this.chatter)); // Send the message on up to the server.
				server.addMessage(new Message(str, this.chatter));
			}
		} catch (Exception e) {
			log.trace("Chatter " + this.chatter.getName() + "  exited with exception.");			
		}

		server.removeChatter(this.chatter.getName());
		log.trace("Chatter " + this.chatter.getName() + "  exited.");			

	}

}