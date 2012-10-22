import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChatterReader extends ChatterHandler {

    public ChatterReader(Server server, Socket sock) {
    	super(server, sock);
    }
    
    public void run() {
		if(sock == null || server == null) {
		    return;
		}
		try{
		    BufferedReader w = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	
		    String str = null;
		    while ((str = w.readLine()) != null) {
				//	    w.write(str + '\n');
				server.addMessage(str);
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
    }

}