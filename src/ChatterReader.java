/* 
 * John Wittrock, Greg Herpel, 2012
 * A class to read from a socket and pass it up to a chat room. 
 * Will eventually be refactored to 
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;

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

			char[] c = new char[1];
			LinkedList<Character> list = new LinkedList<Character>();
			while ((w.read(c,0,1)) != -1) { // While the stream is still open
				if(c[0]=='\n'){
					log.trace(chatter.getName()+": Received a message from");
					char[] rec = new char[list.size()];
					for(int i=0;i<list.size();i++){
						rec[i]=list.get(i);
					}
					server.addMessage(new Message(rec, this.chatter));
					list.clear();
				}
				else{list.add(c[0]);}
			}
		} catch (Exception e) {
			log.trace("Chatter " + this.chatter.getName() + "  exited with exception.");			
		}

		server.removeChatter(this.chatter);
		log.trace("Chatter " + this.chatter.getName() + "  exited.");			

	}

}