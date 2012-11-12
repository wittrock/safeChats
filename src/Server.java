/*
 * John Wittrock and Greg Herpel, 2012
 * This is the main class for the server end of things.
 */

import java.util.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import javax.net.ServerSocketFactory;
import javax.net.ssl.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.security.*;

public class Server {
	private static final int PORT = 46754;
	private List<Chatter> chatters; // A list of all the chatters in this simplistic, one-room chat system.
	private LinkedBlockingQueue<Message> writeBuffer; // All of the messages to be sent out
	private Map<Integer, ChatRoom> rooms;
	private Map<String, String> authData;
	private List<byte[]> saltsUsed;
	private SecureRandom random;

	public Server() {
		System.out.println("Started the server...");
		chatters = Collections.synchronizedList(new ArrayList<Chatter>()); //Chatters is a synchronized list. 
		rooms = Collections.synchronizedMap(new HashMap<Integer, ChatRoom>());
		authData = Collections.synchronizedMap(new HashMap<String,String>());
		saltsUsed = new LinkedList<byte[]>();
		readAuthFile();
		writeBuffer = new LinkedBlockingQueue<Message>();
		
		try {
		random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Cannot instantiate SecureRandom with SHA1PRNG");
			e.printStackTrace();
		}	

		try{
			// Start accepting requests. 
			ServerSocketFactory f = SSLServerSocketFactory.getDefault();
			SSLServerSocket ss;
			String[] suites = {"TLS_DHE_RSA_WITH_AES_256_CBC_SHA",
					"TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
					"TLS_DHE_DSS_WITH_AES_128_CBC_SHA",
					"TLS_DHE_DSS_WITH_AES_256_CBC_SHA"};
			ss = (SSLServerSocket)f.createServerSocket(PORT);
			ss.setEnabledCipherSuites(suites);
	
			// For now, this will also be the name of the chatter. 
			int numChatters = 0;
	
			// The thread that will actively poll the queue and distribute to chatters. 
			BufferPusher bufferPusher = new BufferPusher(chatters, writeBuffer, this); 
			(new Thread(bufferPusher)).start();

			// Accept connections forever, stick them on the chatters list. 
			while(true) {
				Socket s = ss.accept();
				System.out.println("Got a new connection!");
				String name = Integer.toString(numChatters);

				Chatter chatter = new Chatter(name);
				ChatterWriter writer = new ChatterWriter(this, s);
				ChatterReader reader = new ChatterReader(this, s, chatter);
				chatter.setReader(reader);
				chatter.setWriter(writer);
				chatters.add(chatter);
				numChatters++;
		    
			}
		} catch (Exception e) { // We know this sucks, we just wanted to get something out the door. Will be fixed in next version. 
			e.printStackTrace();
			return;
		}
	}

	/*
	 * The function that adds a message to the work queue of this server. 
	 */
	public void addMessage(Message msg) { 
		try {
			writeBuffer.put(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Chatter getChatterByName(String name) {
		for (Chatter c : chatters) {
			if (c.name.equals(name)) {
				return c;
			}
		}
		return null;
	}

	public boolean addChatterToRoom(Chatter c, int roomId) {
		ChatRoom room = rooms.get(roomId);
		if (room == null) {
			return false;
		}
		
		room.addChatter(c);
		return true;
	}

	public boolean removeChatterFromRoom(Chatter c, int roomID) {
		ChatRoom room = rooms.get(roomID);
		if (room == null) {
			return false;
		}
		
		room.removeChatter(c);
		return true;
	}
	
	public void removeChatterFromAllRooms(Chatter c){
		for(ChatRoom room:rooms.values()){
			if(room.containsChatter(c))
				room.removeChatter(c);
		}
	}
	
	public void removeChatter(Chatter c){
		chatters.remove(c);
	}

	public void createRoom(Chatter owner) {
		int roomId;
		while(true) {
			roomId = random.nextInt();
			if(!rooms.containsKey(roomId)) {
				break;
			}			
		}

		ChatRoom room = new ChatRoom(roomId, owner);
		rooms.put(roomId, room);
		owner.addMessage("CREATED " + roomId + "$ ");
	}

	public void removeRoom(int id) {
		rooms.remove(id);
	}

	public ChatRoom getRoomByID(String idString) {
		int id = Integer.valueOf(idString);
		return rooms.get(id);
	}

	// This should very much be done with a direct reference for the sake of security.
	// For now, this will suffice.
	public void removeChatter(String name) {
		for (int i = 0; i < chatters.size(); i++) {
			if (chatters.get(i).getName().equals(name)) {
				Chatter c = chatters.remove(i);
				c.stopAll();
				sendToAll("USR_LEFT " + c.getName() + " $ ");
				break;
			}
		}
		System.out.println("Removed a chatter. Num chatters: " + chatters.size());
	}
	
	public void authUser(String userName, String password, Chatter user){
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			byte[] b = password.getBytes();
			
			String sH = authData.get(userName);
			
			if(sH == null){
				user.addUnauthenticatedMessage("AUTH false$ ");
				return;
			}
			String[] div = sH.split("#");
			byte[] salt = util_StringToByteArr(div[0]);
			byte[] hash = util_StringToByteArr(div[1]);
			md.update(salt);
			byte[] h = md.digest(b);
			if(Arrays.equals(hash, h)){
				user.authUser();
				user.addMessage("AUTH true$ ");
				user.name = userName;
				sendAllNames(user);
				sendToAll("USR_ADDED " + user.getName()+"$ ");
			}
			else{
				user.addUnauthenticatedMessage("AUTH false$ ");
			}
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
	}
	public void newAcc(String userName, String password, Chatter user){
		String sh = authData.get(userName);
		
		try {
			if(sh == null){
				MessageDigest md = MessageDigest.getInstance("SHA-512");
				
				byte[] salt = new byte[4];
				random.nextBytes(salt);
				while(saltsUsed.contains(salt)){
					random.nextBytes(salt);
				}
				saltsUsed.add(salt);
				
				md.update(salt);
				byte[] hashP = md.digest(password.getBytes());
				authData.put(userName,Arrays.toString(salt)+"#"+Arrays.toString(hashP));
				writeAuthFile(userName,Arrays.toString(salt)+"#"+Arrays.toString(hashP));
				
				user.authUser();
				user.name = userName;
				user.addUnauthenticatedMessage("NEW_ACC true$ ");
				
				sendAllNames(user);
				sendToAll("USR_ADDED " + user.getName()+"$ ");
			}
			else{
				user.addUnauthenticatedMessage("NEW_ACC false$ ");
			}
		}catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	public void sendToAll(String str){
		for(Chatter c: chatters){
			c.addMessage(str);
		}
	}
	public void sendAllNames(Chatter c){
		for(Chatter chat: chatters){
			c.addMessage("USR_ADDED "+chat.getName()+"$ ");
		}
	}

	/* Main method. Not much else to say about it. */
	public static void main(String[] args) { 
		
		Server server = new Server();
	}
	
	private void readAuthFile(){
		try {
			BufferedReader in = new BufferedReader(new FileReader("auth.txt"));
			String line = "";
			while((line=in.readLine())!=null){
				System.out.println(line);
				String[] s =line.split("\t");
				authData.put(s[0], s[1]);
				String[] t = s[1].split("#");
				byte[] salt = util_StringToByteArr(t[0]);
				saltsUsed.add(salt);
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeAuthFile(String user, String pass){
		try {
			PrintWriter out = new PrintWriter(new FileWriter("auth.txt", true)); // append to the file, don't overwrite.
			String put = user+"\t"+pass;
			out.println(put);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public byte[] util_StringToByteArr(String str){
		str = str.replace("[","");
		str = str.replace("]","");
		String[] u = str.split(", ");
		byte[] ret = new byte[u.length];
		for(int i=0;i<u.length;i++){
			ret[i] = Byte.valueOf(u[i]);
		}
		return ret;
	}
	
}