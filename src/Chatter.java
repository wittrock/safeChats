import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/* 
 * John Wittrock, Greg Herpel, 2012 
 * Class to hold all of the data associated with the chatter.
 * The only real functionality here is to pass messages from the server to the ChatterWriter
 */
public class Chatter {
	protected String name;
	private boolean authenticated;
	//add list of rooms here later.
	ChatterWriter writer = null;
	ChatterReader reader = null;
	Thread writerThread;
	Thread readerThread;
	private Logger log;
 
	public void addMessage(String str) {
		if (authenticated == false) return;
		writer.addMessage(str);
	}

	public void addUnauthenticatedMessage(String str) {
		writer.addMessage(str);
	}
	
	public void stopAll() {
		writer.stopThread();
		reader.stopThread();
	}
	
	public String getName() {
		return name;
	}

	public void setReader(ChatterReader reader) {
		if (this.reader == null) { 
			this.reader = reader;
			readerThread = new Thread(this.reader);

			readerThread.start();
		}
	}

	public void setWriter(ChatterWriter writer) {
		if (this.writer == null) { 
			this.writer = writer;
			writerThread = new Thread(this.writer);
			writerThread.start();
		} 
	}
	
	public boolean isAuthenticated(){
		return authenticated;
	}
	
	public void authUser(){
		authenticated = true;
	}

	public Chatter(String name) {
		this.name = name;
		this.authenticated = false;
		log = Logger.getLogger(Chatter.class);
		PropertyConfigurator.configure("log4j.properties");
		log.trace("Started a new Chatter " + name);
	}
}