/*
 * Greg Herpel, John Wittrock, 2012
 * This class reads incoming messages from the server and pushes to the buffer
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;


public class ClientMessageListener implements Runnable {
	
	private BufferedReader messageReader;
	private ClientBufferPusher cbp;
	
	public ClientMessageListener(Socket s, ClientBufferPusher cbp){
		try{
			this.cbp = cbp;
			messageReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
		}catch (Exception e) {
		    e.printStackTrace();
		    return;
		}
	}
		
	public void run(){	
		System.out.println("ClientMessageListener starting to run...");
		try{
			String str = null;
			while((str=messageReader.readLine())!=null){
				cbp.addMessage(str + "\n");
			}
			System.out.println("Finished loop!");
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
	}
}
