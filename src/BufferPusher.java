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
		synchronized(writeBuffer) {
		    try {
		    	return writeBuffer.take();
		    } catch (InterruptedException e) {
		    	System.out.println("BufferPusher InterruptedException!");
		    	e.printStackTrace();
		    	return null;
		    }
		}
    }


    public void run(){
		while(true) {
		    String str = getMessage();
		    for(Chatter c : chatters) {
		    	c.addMessage(str);
		    }
		}
    }

}