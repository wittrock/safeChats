/* 
 * This is going to become more of a processing class than anything else. 
 * This, or a closely-related class will actually have to parse status messages and perform 
 * the necessary operations. Still more to do here. 
 */ 

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;


public class BufferPusher extends Thread {
	private List<Chatter> chatters;
	private LinkedBlockingQueue<String> writeBuffer;

	public BufferPusher(List<Chatter> chatters, LinkedBlockingQueue<String> writeBuffer) {
		this.chatters = chatters;
		this.writeBuffer = writeBuffer;
	}

	public String getMessage() {
		//		synchronized(writeBuffer) {
		try {
		    	return writeBuffer.take();
		} catch (InterruptedException e) {
		    	System.out.println("BufferPusher InterruptedException!");
		    	e.printStackTrace();
		    	return null;
		}
		//		}
	}


	public void run(){
		while(true) {
			String str = getMessage();
			System.out.println("Pushing a message to chatters: " + str);
			for(Chatter c : chatters) {
				c.addMessage(str);
			}
		}
	}

}