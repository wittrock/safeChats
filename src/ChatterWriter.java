/*
 * John Wittrock, Greg Herpel, 2012
 * Simple class to take a message from a global queue and put it on a local queue to be sent out. 
 */

import java.io.*;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatterWriter extends ChatterHandler {
	
	/* A private blocking queue to add global messages to. */
	private LinkedBlockingQueue<String> writeBuffer;

	public ChatterWriter(Server server, Socket sock) {
		super(server, sock);
		writeBuffer = new LinkedBlockingQueue<String>();
	}

	/*
	 * This method will be called by other threads 
	 * that wish to add messages.
	 */
	public void addMessage(String message) {
		try {
			writeBuffer.put(message);
		} catch (InterruptedException e) {
			System.out.println("ChatterWriter was interrupted.");
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
				System.out.println("ChatterWriter: got a message -- " + str);
				w.write(str + '\n');
				w.flush();
				System.out.println("ChatterWriter: sent a message -- " + str);
			}
	
		} catch(Exception e) { /* Again, I know this sucks, but we wanted to get something out the door */
			e.printStackTrace();
			return;
		}
	}
}