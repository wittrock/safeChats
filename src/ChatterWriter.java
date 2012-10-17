public class ChatterWriter extends ChatterHandler {
    private LinkedBlockingQueue<String> writeBuffer;

    public ChatterWriter(Server server, Socket sock) {
	super(server, sock);
	writeBuffer = new LinkedBlockingQueue<String>();
    }

    public addMessage(String message) {
	writeBuffer.put(message);
    }
    
    public void run() {
	if(sock == null || server == null) {
	    return;
	}
	    
	BufferedWriter w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

	String str = null;
	while(true) {
	    str = writeBuffer.take();
	    w.write(str + '\n');
	}
    }

}