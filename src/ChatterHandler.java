import java.net.Socket;


public abstract class ChatterHandler implements Runnable {
    Socket sock;
    Server server;

    public ChatterHandler(Server server, Socket sock){
    	this.server = server;
    	this.sock = sock;
    }

    public abstract void run();
}