import java.util.*;
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
		System.out.println("Started the server...");
		chatters = Collections.synchronizedList(new ArrayList<Chatter>());
		writeBuffer = new LinkedBlockingQueue<String>();
	
		try{
			ServerSocketFactory f = SSLServerSocketFactory.getDefault();
			ServerSocket ss = f.createServerSocket(PORT);
	
			int numChatters = 0;
	
			BufferPusher bufferPusher = new BufferPusher(chatters, writeBuffer);
		
			(new Thread(bufferPusher)).start();
		
			while(true) {
				Socket s = ss.accept();
				System.out.println("Got a new connection!");
				chatters.add(new Chatter(Integer.toString(numChatters),
							 new ChatterReader(this, s),
							 new ChatterWriter(this, s)));
				numChatters++;
		    
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public void addMessage(String str) { 
				synchronized(writeBuffer) {
			try {
				System.out.println("Got a message!");
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