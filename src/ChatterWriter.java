/*
 * John Wittrock, Greg Herpel, 2012
 * Simple class to take a message from a global queue and put it on a local queue to be sent out, 
 * in this case those of the Chatters.
 */

import java.io.*;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class ChatterWriter extends ChatterHandler {
	
	/* A private blocking queue to add global messages to. */
	private LinkedBlockingQueue<String> writeBuffer;
	private Logger log;
	private Chatter chatter;

	public ChatterWriter(Server server, Socket sock, Chatter chatter) {
		super(server, sock);
		writeBuffer = new LinkedBlockingQueue<String>();
		log = Logger.getLogger(ChatterWriter.class);
		PropertyConfigurator.configure("log4j.properties");
		this.chatter=chatter;
	}

	/*
	 * This method will be called by other threads 
	 * that wish to add messages.
	 */
	public void addMessage(String message) {
		try {
			writeBuffer.put(message);
		} catch (InterruptedException e) {
			log.error("ChatterWriter was interrupted. "+chatter.getName());
			e.printStackTrace();
			return;
		}
	}
    
	public void run() {
		if(sock == null || server == null) {
			return;
		}
		BufferedWriter w;
		try {    
			w = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			String str = null;
			
			/* Get a message from the queue, write it to the socket. */
			while(true && this.running) {
				str = writeBuffer.take();
				log.trace(chatter.getName()+": sending a "+getCommand(str)+ " message to");
				w.write(str + '\n');
				w.flush();
			}
	
		} catch(Exception e) { /* Again, I know this sucks, but we wanted to get something out the door */
			e.printStackTrace();
			return;
		}
	}
	
	public String getCommand(String message){
		String[] args = message.split(" ");
		return args[0];
	}
}