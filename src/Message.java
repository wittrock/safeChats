public class Message {
	private char[] data;
	private Chatter sender;

	public Message(char[] data, Chatter sender) {
		this.data = data;
		this.sender = sender;
	}

	public char[] getData() { return data; }
	public Chatter getSender() { return sender; }
}