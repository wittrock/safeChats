import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;


public class ClientMessageListener implements Runnable {
	
	private BufferedReader messageReader;
	private GUI_ChatInterface gci;
	
	public ClientMessageListener(Socket s,GUI_ChatInterface gci){
		try{
			this.gci = gci;
			messageReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
		}catch (Exception e) {
		    e.printStackTrace();
		    return;
		}
	}
		
	public void run(){	
		try{
			String str = null;
			while((str=messageReader.readLine())!=null){
				gci.addChatText(str);
			}
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
	}
}
