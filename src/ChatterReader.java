import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChatterReader extends ChatterHandler {
	String name;
	
	public ChatterReader(Server server, Socket sock, String name) {
		super(server, sock);
		this.name = name;
	}
    
	public void run() {
		if(sock == null || server == null) {
			return;
		}
		try{
			BufferedReader w = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			System.out.println("ChatterReader started...");
			String str = null;
			while ((str = w.readLine()) != null) {
				System.out.println("Got message: " + str);
				server.addMessage("" + this.name + ": " + str  +"\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Chatter exited.");
	}

}