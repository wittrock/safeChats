import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;


public class ClientMessageListener implements Runnable {
	
	private BufferedReader messageReader;
	
	public ClientMessageListener(Socket s){
		try{
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
				System.out.println(str);
				System.out.flush();
			}
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
	}
}
