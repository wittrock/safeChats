/*
 * Greg Herpel, John Wittrock, 2012
 * This is the main class for the client
 */

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.JFrame;


public class Client {
	private static final int PORT = 46754;
	private static final String HOST = "localhost";
	private Socket s;
	private BufferedWriter typedWriter;
	private JFrame gsi;
	private JFrame gca;
	
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
	
	public void authed(){
		gsi.dispose();
		gca.dispose();
	}
	
	public void newAccSwitch(){
		gsi.setVisible(false);
		gca.setVisible(true);
	}

	/* This method spawns a thread to listen for messages */
	public void runChat(ClientBufferPusher cbp){
		try{
			ClientMessageListener cml = new ClientMessageListener(s,cbp);
			(new Thread(cml)).start();
		}catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	public synchronized void sendMessage(String str){
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
		c.gsi = new GUI_SignIn(c);
		c.gsi.setVisible(true);
		c.gca = new GUI_CreateAccount(c);
		c.gca.setVisible(false);
		ClientBufferPusher cbp = new ClientBufferPusher(c);
		c.runChat(cbp);
	}
}
