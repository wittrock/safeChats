import java.io.*;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatterWriter extends ChatterHandler {
    private LinkedBlockingQueue<String> writeBuffer;

    public ChatterWriter(Server server, Socket sock) {
		super(server, sock);
		writeBuffer = new LinkedBlockingQueue<String>();
    }

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
	
		    while(true) {
				str = writeBuffer.take();
				System.out.println("ChatterWriter: got a message -- " + str);
				w.write(str + '\n');
				w.flush();
		    }
	
		} catch(Exception e) {
		    e.printStackTrace();
		    return;
		}
    }
}