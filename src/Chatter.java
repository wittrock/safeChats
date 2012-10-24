/* 
 * John Wittrock, Greg Herpel, 2012 
 * Class to hold all of the data associated with the chatter.
 * The only real functionality here is to pass messages from the server to the ChatterWriter
 */
public class Chatter {
	protected String name;
	//add list of rooms here later.
	ChatterWriter writer;
	ChatterReader reader;
	Thread writerThread;
	Thread readerThread;
 
	public void addMessage(String str) {
		writer.addMessage(str);
	}
	
	public void stopAll() {
		writer.stopThread();
		reader.stopThread();
	}
	
	public String getName() {
		return name;
	}

	public Chatter(String name, ChatterReader reader, ChatterWriter writer) {
		this.name = name;
		this.reader = reader;
		this.writer = writer;
		writerThread = new Thread(this.writer);
		readerThread = new Thread(this.reader);
		writerThread.start();
		readerThread.start();
		System.out.println("Started a new Chatter " + name);
	}
}