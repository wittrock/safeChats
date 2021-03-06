/*
 * This is the 
 */

import java.util.*;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class ChatRoom {
	
	private List<Chatter> chatters; // A list of all the chatters in this simplistic, one-room chat system.
	private List<Chatter> invited;
	private List<Chatter> silenced;
	private int id;
	private Chatter owner;
	private Logger log;
	
	private String[] zs;
	private String[] xs;
	private int numZs; 
	private int numXs;
	
	public ChatRoom(int id, Chatter owner) {
		this.chatters = Collections.synchronizedList(new ArrayList<Chatter>());
		this.invited = Collections.synchronizedList(new ArrayList<Chatter>());
		this.silenced = Collections.synchronizedList(new ArrayList<Chatter>());
		this.owner = owner;
		this.id = id;
		this.zs = null;
		this.xs = null;
		this.numZs = 0;
		this.numXs = 0;
		chatters.add(owner);
		log = Logger.getLogger(ChatRoom.class);
		PropertyConfigurator.configure("log4j.properties");
	}
	
	/* Adds a chatter to this room, but only if they've been invited first. */
	public boolean addChatter(Chatter c) {
		if (!invited.contains(c)) { 
			log.info("Chatter tried to join room that they weren't invited to: " + c.getName());
			return false; 
		}

		log.trace("Found invited chatter: " + c.getName());

		// tell all the other chatters in the room that this user was added.
		this.distributeMessage("CHTR_ADDED " + c.getName() + " " + Integer.toString(this.id) + " $ ");

		chatters.add(c);
		invited.remove(c);
		c.addMessage("JOINED " + Integer.toString(this.id) + " $ ");

		for (Chatter chatter : chatters) {
			// makes the assumption that names are unique. 
			if (!chatter.getName().equals(c.getName())) { 
				c.addMessage("CHTR_ADDED " + chatter.getName() + " " + Integer.toString(this.id) + " $ ");
			}
		}
			
		return true;
	}

	// Tell all the users in the room to begin the conference keying broadcast rounds. 
	public void encryptRoom() {
		numZs = 0;
		zs = null;
		numXs = 0; 
		zs = null;
		distributeMessage("BEGIN_ENC " + this.id + " $ ");
	}
	

	/* This method is called when we receive a Z from a chatter.  */
	public void addZ(Chatter c, String z) {
		/* Dynamic re-encryption will be a problem. */

		if (numZs == 0 || zs == null) {
			System.out.println("initializing zs of size " + chatters.size());
			zs = new String[chatters.size()];
		}
		
		int index = chatters.indexOf(c);
		if (index == -1) return;
		zs[index] = z;
		numZs++;
		if (numZs == chatters.size()) {
			// distribute zs
			for (int i = 0 ; i < zs.length; i++) {
				Chatter chatter = chatters.get(i);
				int lIndex = (i - 1) % zs.length;
				int rIndex = (i + 1) % zs.length;
				if(lIndex < 0) {
					lIndex += zs.length;
				}

				chatter.addMessage("ZS " + this.id + " " + zs[lIndex] + " " + zs[rIndex] + " $ ");
				log.trace("Sent zs to " + c.getName());
			}
		}
	}

	/* This method is called when we receive an X from a chatter.  */
	public void addX(Chatter c, String x) {
		if (numXs == 0 || xs == null) {
			xs = new String[chatters.size()];
		}

		if (xs.length != zs.length) {
			log.error("Number of xs != number of zs");
			return;
		}

		int index = chatters.indexOf(c);
		if (index == -1) return;
		xs[index] = x;
		numXs++;
		if (numXs == chatters.size()) {
			
			String xStr = "";
			for (int i = 0; i < xs.length; i++) {
				xStr = xStr + " " + xs[i];
			}

			System.out.println("xs: " + xStr);

			for (int i = 0; i < xs.length; i++) {
				Chatter chatter = chatters.get(i);
				int leftZIndex = (i - 1) % (xs.length);
				if (leftZIndex < 0) leftZIndex += xs.length;
				String leftZ = zs[leftZIndex];
				String msg = "XS " + this.id + " " + i + " " + leftZ + xStr + " $ ";
				chatter.addMessage(msg);
			}
		}
		
	}

	public void silenceChatter(Chatter c) {
		silenced.add(c);
	}

	public void unsilenceChatter(Chatter c) { 
		silenced.remove(c);
	}

	public boolean isSilenced(Chatter c) {
		return silenced.contains(c);
	}

	public void inviteChatter(Chatter c) {
		invited.add(c);
	}
	
	public void removeInvite(Chatter c){
		invited.remove(c);
	}

	public void removeChatter(Chatter c) {
		chatters.remove(c);
	}

	public boolean containsChatter(Chatter c) {
		return chatters.contains(c);
	}

	public Chatter getOwner() {
		return owner;
	}

	public int size() {
		return chatters.size();
	}

	public int getID() {
		return this.id;
	}

	// Sends a given string to all users in this room. 
	public void distributeMessage(String message) {
		log.trace("Room"+id+": Pushing a message to chatters");
		for(Chatter c : this.chatters) {
			c.addMessage(message);
		}

	}

}