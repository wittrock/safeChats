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
	
	public void runChat(GUI_ChatInterface gci){
		try{
			System.out.println("Running chat");
			ClientMessageListener cml = new ClientMessageListener(s,gci);
			(new Thread(cml)).start();
			System.out.println("Spun off  chat");
		}catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	public void sendMessage(String str){
		System.out.println("SendMessage called!");
		try{
			typedWriter.write(str+"\n");
			System.out.println("Wrote to socket");
			typedWriter.flush();
			System.out.println("Flushed socket");
		}catch (Exception e) {
			System.out.println("Caught exception in sendMessage");
			e.printStackTrace();
			return;
		}
	}
	
	public static void main(String[] args){
		Client c = new Client();
		GUI_ChatInterface gci = new GUI_ChatInterface(c);
		gci.setVisible(true);
		System.out.println("Calling runChat()...");
		c.runChat(gci);
		System.out.println("Called runChat()...");
	}
}
