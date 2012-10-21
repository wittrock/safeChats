import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;


public class Client {
	private static final int PORT = 46754;
	public static final String HOST = "localhost";
	
	public Client(){
		try{
			SocketFactory sf = SSLSocketFactory.getDefault();
			Socket s = sf.createSocket(HOST,PORT);
			
			BufferedReader typedReader = new BufferedReader(new InputStreamReader(System.in));
			BufferedWriter typedWriter = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			
			ClientMessageListener cml = new ClientMessageListener(s);
			cml.run();
			
			String str = null;
			while((str=typedReader.readLine()) != null){
				typedWriter.write(str+"\n");
				typedWriter.flush();
			}
			
		}catch (Exception e) {
		    e.printStackTrace();
		    return;
		}
	}
}
