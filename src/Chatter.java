
public class Chatter {
	protected String name;
	//add list of rooms here later.
	ChatterWriter writer;
	ChatterReader reader;
    
	public void addMessage(String str) {
		writer.addMessage(str);
	}

	public Chatter(String name, ChatterReader reader, ChatterWriter writer){
		this.name = name;
		this.reader = reader;
		this.writer = writer;
		(new Thread(this.writer)).start();
		(new Thread(this.reader)).start();
		System.out.println("Started a new Chatter " + name);
	}
}