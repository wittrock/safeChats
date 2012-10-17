import java.io.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import javax.net.ServerSocketFactory;
import javax.net.ssl.*;

public class Server {
    private static final int PORT = 78655;
    private synchronized Vector<Chatter> chatters;
    private synchronized LinkedBlockingQueue<String> writeBuffer();

    

    public static void main(String[] args) { 
	chatters = new Vector<Chatter>();
	ServerSocketFactory f = SSLServerSocketFactory.getDefault();
	ServerSocket ss = f.createServerSocket(PORT);
	int chatters = 0;

	Thread bufferPusher = new Thread(){
		public void run(){
		    while(true) {
			String str = writeBuffer.take();
			for(Chatter c : chatters) {
			    Chatter.addMessage(str);
			}
		    }
		}
	    }

	while(true) {
	    Socket s = ss.accept();
	    chatters.add(new Chatter(Integer.toString(chatters),
				     new ChatterReader(this, s),
				     new ChatterWriter(this, s)));

	    
	}
    }
}