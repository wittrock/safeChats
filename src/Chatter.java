
public class Chatter {
    String name;
    //add list of rooms here later.
    ChatterWriter reader;
    ChatterReader writer;
    
    public void addMessage(String str) {
	writer.addMessage(str);
    }

    public Chatter(String name, ChatterReader reader, ChatterWriter writer){
	this.name = name;
	this.reader = reader;
	this.writer = writer;
	
    }
}