import java.io.*;
import java.util.*;

public class Message {
	private String data;
	private Chatter sender;

	public Message(String data, Chatter sender) {
		this.data = data;
		this.sender = sender;
	}

	public String getData() { return data; }
	public Chatter getSender() { return sender; }
}