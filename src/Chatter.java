/* 
 * John Wittrock, Greg Herpel, 2012 
 * Class to hold all of the data associated with the chatter.
 * The only real functionality here is to pass messages from the server to the ChatterWriter
 */
public class Chatter {
	protected String name;
	//add list of rooms here later.
	ChatterWriter writer = null;
	ChatterReader reader = null;
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

	public void setReader(ChatterReader reader) {
		if (this.reader == null) { 
			System.out.println("Really starting a new Reader");
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

	public Chatter(String name) {
		this.name = name;
		System.out.println("Started a new Chatter " + name);
	}
}