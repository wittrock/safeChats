/*
 * John Wittrock, Greg Herpel, 2012
 * Simple abstract class to denote the idea of a thing that operates on a Socket and is part of a chatter.
 * The real important bit is that it implements runnable, so it's going to be a Thread. 
 */

import java.net.Socket;


public abstract class ChatterHandler implements Runnable {
	protected Socket sock;
	protected Server server;
	protected volatile boolean running = true;

	public ChatterHandler(Server server, Socket sock){
		this.server = server;
		this.sock = sock;
	}

	public void stopThread() {
		this.running = false;
	}

	public abstract void run();
}