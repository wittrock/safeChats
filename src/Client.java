/*
 * Greg Herpel, John Wittrock, 2012
 */

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;


public class Client {
	private static final int PORT = 46754;
	private static final String HOST = "localhost";
	private Socket s;
	private BufferedWriter typedWriter;
	
	public Client(){
		try{
			SocketFactory sf = SSLSocketFactory.getDefault();
			s = sf.createSocket(HOST,PORT);
			typedWriter = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			
		}catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	/* This method spawns a thread to listen for messages */
	public void runChat(GUI_ChatInterface gci){
		try{
			ClientMessageListener cml = new ClientMessageListener(s,gci);
			(new Thread(cml)).start();
		}catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	public void sendMessage(String str){
		System.out.println("SendMessage called!");
		try{
			typedWriter.write(str+"\n");
			typedWriter.flush();
			System.out.println("Flushed socket: " + str);
		}catch (Exception e) {
			System.out.println("Caught exception in sendMessage");
			e.printStackTrace();
			return;
		}
	}


	/* 
	 * Pretty simple class, this. Makes a new GUI, spawns a couple of threads, listens to chats, writes chats. 
	 */	
	public static void main(String[] args){
		Client c = new Client();
		GUI_ChatInterface gci = new GUI_ChatInterface(c);
		gci.setVisible(true);
		c.runChat(gci);
	}
}
