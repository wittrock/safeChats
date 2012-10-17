public class ChatterReader extends ChatterHandler {

    public ChatterWriter(Server server, Socket sock) {
	super(server, sock);
    }
    
    public void run() {
	if(sock == null || server == null) {
	    return;
	}
	    
	BufferedReader w = new BufferedReader(new InputStreamReader(s.getInputStream()));

	String str = null;
	while ((str = r.readLine()) != null) {
	    w.write(str + '\n');
	    w.flush();
	    server.addMessage(str);
	}
    }

}