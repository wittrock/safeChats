/* 
 * This is going to become more of a processing class than anything else. 
 * This, or a closely-related class will actually have to parse status messages and perform 
 * the necessary operations. Still more to do here. 
 */ 

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;


public class BufferPusher extends Thread {
	private List<Chatter> chatters;
	private LinkedBlockingQueue<Message> writeBuffer;
	private Server server;

	public BufferPusher(List<Chatter> chatters, LinkedBlockingQueue<Message> writeBuffer, Server server) {
		this.chatters = chatters;
		this.writeBuffer = writeBuffer;
		this.server = server;
	}

	public Message getMessage() {
		try {
		    	return writeBuffer.take();
		} catch (InterruptedException e) {
		    	System.out.println("BufferPusher InterruptedException!");
		    	e.printStackTrace();
		    	return null;
		}
	}


	public void run(){
		while(true) {
			Message msg = getMessage();
			String str = msg.getData();
			System.out.println("Got message: " + str);			
			// Parse the protocol stuff out
			int protocolEnd = str.indexOf((int)'$');
			if (protocolEnd == -1) {
				// Toss this out. 
				continue;
			}
			
			String protocol = str.substring(0, protocolEnd);
			String userMessage = str.substring(protocolEnd + 1);
			
			String[] args = protocol.split(" ");
			String command = args[0];

			if (command.equals("CREATE")) {
				System.out.println("Sending create command to " + msg.getSender().getName());
				server.createRoom(msg.getSender());
				continue;
			} else {
				//toss out the whole thing.
				continue;
			}

			// MOVE THIS TO MSG COMMAND
			// System.out.println("Pushing a message to chatters: " + str);
			// for(Chatter c : chatters) {
			// 	c.addMessage(str);
			// }
		}
	}

}