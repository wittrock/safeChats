import java.io.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import javax.net.ServerSocketFactory;
import javax.net.ssl.*;
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
				w.write(str + '\n');
		    }
	
		} catch(Exception e) {
		    e.printStackTrace();
		    return;
		}
    }
}