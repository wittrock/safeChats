import java.io.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import javax.net.ServerSocketFactory;
import javax.net.ssl.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {
    private static final int PORT = 46754;
    private List<Chatter> chatters;
    private LinkedBlockingQueue<String> writeBuffer;

    public Server() {
	chatters = Collections.synchronizedList(new ArrayList());
	writeBuffer = new LinkedBlockingQueue<String>();

	try{
	    ServerSocketFactory f = SSLServerSocketFactory.getDefault();
	    ServerSocket ss = f.createServerSocket(PORT);

	    int numChatters = 0;

	    BufferPusher bufferPusher = new BufferPusher(chatters, writeBuffer);
	
	    bufferPusher.run();
	
	    while(true) {
		Socket s = ss.accept();
		chatters.add(new Chatter(Integer.toString(numChatters),
					 new ChatterReader(this, s),
					 new ChatterWriter(this, s)));
	    
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    return;
	}
    }

    public void addMessage(String str) { 
	synchronized(writeBuffer) {
	    try {
		writeBuffer.put(str);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
    }


    public static void main(String[] args) { 
	Server server = new Server();
    }
}