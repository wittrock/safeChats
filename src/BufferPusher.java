/* John Wittrock, Greg Herpel 2012 
 * This is going to become more of a
 * processing class than anything else.  This, or a closely-related
 * class will actually have to parse protocol messages and perform the
 * necessary operations. This class handles all messages coming into the server and demultiplexes 
 * them to the right chat room, and also processes protocol messages. 
 * 
 * Protocol messages are arranged as follows:
 * The first string will be a command. There will then be a series of 
 * arguments, delimited by spaces, and then a '$' character, and then a possible user-message, 
 * or an encrypted message. Note that the only command which takes a user message (in plaintext 
 * or ciphertext) is the MSG command. 
 */ 

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


public class BufferPusher extends Thread {
	private List<Chatter> chatters;
	private LinkedBlockingQueue<Message> writeBuffer;
	private Server server;
	private Logger log;

	public BufferPusher(List<Chatter> chatters, LinkedBlockingQueue<Message> writeBuffer, Server server) {
		this.chatters = chatters;
		this.writeBuffer = writeBuffer;
		this.server = server;
		log = Logger.getLogger(BufferPusher.class);
		PropertyConfigurator.configure("log4j.properties");
	}


	/* Returns a new message from the blocking queue. 
	 * This function blocks until a message is on the queue.
	 */
	private Message getMessage() {
		try {
		    	return writeBuffer.take();
		} catch (InterruptedException e) {
		    	log.error("BufferPusher InterruptedException for writeBuffer");
		    	e.printStackTrace();
		    	return null;
		}
	}

	/* Begin String-like char-array functions. 
	 * Used so we can zero out memory used for passwords. */
	private int countChar(char[] str, char c) {
		int count = 0;
		for (int i = 0; i < str.length ; i++) {
			if (c == str[i]) {
				count++;
			}
		}
		return count;
	}
	
	private int indexOf(char[] str, char c,int start){
		if(start>=str.length)
			return -1;
		for(int i=start;i<str.length;i++){
			if(str[i]==c){
				return i;
			}
		}
		return -1;
	}
	
	private char[] substring(char[] str,int begin,int end){
		if(end==-1)
			end = str.length;
		if(begin>=end)
			begin = end;
		char[] ret = new char[end-begin];
		for(int i=begin;i<end;i++){
			ret[i-begin] = str[i];
		}
		return ret;
	}
	
	private char[][] split(char[] str,char reg){
		int count = countChar(str,reg)+1;
		char[][] ret = new char[count][];
		int begin = 0;
		int end = indexOf(str,reg,0);
		for(int i=0;i<count;i++){
			char[] sub = substring(str,begin,end);
			ret[i] = sub;
			begin = end+1;
			end = indexOf(str,reg,begin);
		}
		
		return ret;
	}

	/* End of char-array parsing functions. */

	/* 
	 * Protocol messages are arranged as follows:
	 * The first string will be a command. There will then be a series of 
	 * arguments, delimited by spaces, and then a '$' character, and then a possible user-message, 
	 * or an encrypted message. Note that the only command which takes a user message (in plaintext 
	 * or ciphertext) is the MSG command. 
	 */

	public void run(){
		while(true) {
			try {
				// try and get a new message from the blocking queue. 
				Message msg = getMessage();
				
				/* Get the data out of the message */
				char[] str = msg.getData();	
		
				// Parse the protocol stuff out
				int protocolEnd = indexOf(str,'$',0);
				int msgStart = protocolEnd +1;
				if (protocolEnd == -1) {
					log.warn("Invalid message from " + msg.getSender().getName());
					// Toss this out. 
					continue;

				}
			
				/* Make sure we parsed this correctly and we don't have an extra space at the end */
				if (str[protocolEnd - 1] == ' ') {
					protocolEnd--;
				}

				char[] protocol = substring(str, 0, protocolEnd);
				char[] userMessage = substring(str, msgStart, str.length);

				char[][] args = split(protocol,' ');
				int numArgs = args.length;
				String command = String.valueOf(args[0]);
			
				log.trace(msg.getSender().getName()+": Got " + command +" message");


				/* Create chat room */
				if (command.equals("CREATE")) {
					log.trace("Sending create command to " + msg.getSender().getName());
					server.createRoom(msg.getSender());
					continue;


				} else if (command.equals("MSG")) {

					/* Message command. This is the one that's going to take the most processing. */
					/* This is organized as follows:
					 * If the message isn't encrypted,
					 * args[1] = the roomID to be sent to
					 *
					 * If the message is encrypted, the format is:
					 * args[1] = the room id
					 * args[2] = the mac length, in bytes
					 * args[3] = the iv length, in bytes
					 * The user message is then a concatenation of MAC, IV, and encrypted message, 
					 * in base 64, which then has to be converted back to bytes and stripped apart. 
					 */
					
					log.trace(msg.getSender().getName()+": Sent a message to room " + String.valueOf(args[1]));
					
					if (numArgs < 2) {
						//incorrectly formatted message. Log here?
						log.warn("Incorrectly formatted MSG command from " + msg.getSender().getName());
						continue;//toss.
					}

					String roomID = String.valueOf(args[1]);
					Chatter sender = msg.getSender();
					ChatRoom room = server.getRoomByID(roomID);
					if (room == null || !room.containsChatter(sender)) {
						//invalid room. Log here?
						continue;
					}
					
					if (room.isSilenced(sender)) {
						sender.addMessage("MSG " + roomID + " $" + "Server: you have been silenced in this room. Whoops.\n");
						log.trace("Message from a silenced sender in room: " + roomID + ": " + msg.getSender().getName());
						continue;
					}

					// We know now that the sender is authorized to send to this room.

					if (numArgs == 2) {
						// non-encrypted message
						String annotatedMsg = "MSG " + roomID + " $ "  + sender.getName() + ": " + String.valueOf(userMessage);

						System.out.println("User message: " + new String(userMessage));
						room.distributeMessage(annotatedMsg);

					} else if (numArgs == 4) {
						/* The format for an
						 * encrypted message back to a client is as follows: 
						 * args[0] is the MSG command as usual.
						 * args[1] is the randomly generated roomID, as with unencrypted messages
						 * args[2] is the iv length, in bytes
						 * args[3] is the MAC length, in bytes
						 * args[4] will be the sender's id name, since we can't prepend it to the 
						 *         message anymore. 
						 */
						String annotatedMsg = "MSG " + 
							roomID + " " + 
							String.valueOf(args[2]) + " " +
							String.valueOf(args[3]) + " " + 
							sender.getName() + // this will be the iv. 
							" $ " + String.valueOf(userMessage);
						room.distributeMessage(annotatedMsg);

					} else {
						log.warn("Incorrectly formatted MSG command from " + msg.getSender().getName());
						continue;
					}


				} else if (command.equals("INVITE")) {
					/* Invite command from a client. 
					 * Format:
					 * args[1] = the invited chatter, by name.
					 * args[2] = the roomId
					 */
					
					log.trace(msg.getSender().getName()+": Invited "+ String.valueOf(args[1])+ "to room" + String.valueOf(args[2]));
					
					if(numArgs < 3 || countChar(str, '$') > 1) {
						log.warn("Incorrectly formatted INVITE command from " + msg.getSender().getName());
						continue; // invalid invite message. Log here?
					}

					String invitedChatter = String.valueOf(args[1]);
					String roomId = String.valueOf(args[2]);
					Chatter c = server.getChatterByName(invitedChatter);
					if (c == null) {
						// send a failure message here. 
						String inviteFailure = "INVITE_FAILED " + String.valueOf(args[1]) + " $ ";
						msg.getSender().addMessage(inviteFailure);
						log.warn("INVITE command to non logged-in chatter " + invitedChatter + " from " + msg.getSender().getName());
						continue;
					}
				
					// check that the sender is actually in the room. 
					ChatRoom room = server.getRoomByID(roomId);
					if (room == null || !room.getOwner().getName().equals(msg.getSender().getName()) || room.containsChatter(c)) {
						// invalid room. will consider sending a different failure message here.
						log.warn("INVITE command from " + msg.getSender().getName() + " to invalid room " + roomId);
						continue;
					}

					room.inviteChatter(c);
					// forward on the invite message.
					c.addMessage("" + String.valueOf(protocol) + " " + msg.getSender().getName() + " $ ");

				} else if (command.equals("KICK")){
					
					/* Kick command from a client. 
					 * Format:
					 * args[1] = the kicked chatter, by name.
					 * args[2] = the roomId
					 */
					
					if(numArgs < 3 || countChar(str, '$') > 1) {
						log.warn("Invalid KICK command from " + msg.getSender().getName());
						continue; // invalid invite message. Log here?
					}
					
					log.error(msg.getSender().getName()+": Kick message had errors");
					
					String kickedChatter = String.valueOf(args[1]);
					String roomId = String.valueOf(args[2]);
					ChatRoom room = server.getRoomByID(roomId);
					Chatter c = server.getChatterByName(kickedChatter);

					if (c == null || room == null || !room.getOwner().getName().equals(msg.getSender().getName()) || !room.containsChatter(c)) {
						log.warn("Invalid KICK command from " + msg.getSender().getName());
						continue;
					}
					room.removeChatter(c);
					room.distributeMessage("CHTR_LEFT " + c.getName() + " " + roomId + " $ ");
					c.addMessage("" + String.valueOf(protocol) + " $ ");
					
				} else if (command.equals("JOIN")) {
					/* Join command from a client to the server. Format:
					 * args[1] = the roomID.
					 * This also checks that the user has actually been invited and is authorized to join. 
					 */

					if (numArgs < 3) {
						log.warn("Invalid JOIN command from " + msg.getSender().getName());
						continue;
					}
				
					String roomId = String.valueOf(args[1]);
					boolean accept = Boolean.valueOf(String.valueOf(args[2]));
					//				Chatter c = server.getChatterByName(invitedChatter);
					Chatter c = msg.getSender();
					ChatRoom room = server.getRoomByID(roomId);
					if(!accept){
						continue;
					}
					if (c == null || room == null || !room.addChatter(c)) {
						log.error("No invited chatter by that name: " + msg.getSender().getName());
						continue;
					}

				} else if(command.equals("AUTH")){
					// We should do some error checking here.
					if (numArgs !=3 || countChar(str, '$') > 1){continue;}
					server.authUser(String.valueOf(args[1]),args[2],msg.getSender());

				} else if (command.equals("NEW_ACC")){
					if (numArgs !=3 || countChar(str, '$') > 1){continue;}
					server.newAcc(String.valueOf(args[1]), args[2], msg.getSender());

				} else if (command.equals("CHTR_LEFT")) {
					/* What is sent by the client when they exit a chat room. */
					/* The only argument is the roomID. */
					
					if (numArgs < 2) { continue; }
					String roomId = String.valueOf(args[1]);
					ChatRoom room = server.getRoomByID(roomId);
					Chatter c = msg.getSender();
					if (room == null || !room.containsChatter(c)) continue;

					room.removeChatter(c);

					if (room.size() == 0 || c.equals(room.getOwner())) {
						room.distributeMessage("RM_DESTROYED " + roomId + " $ ");									       // actually remove the room. 
						server.removeRoom(room.getID());
						log.trace("Room destroyed: " + room.getID());
					} else {
						room.distributeMessage("CHTR_LEFT " + c.getName() + " " + roomId + " $ ");
					}

				} else if (command.equals("USR_LEFT")){
					/* When a user signs off, they send this message */
					/* No arguments since we know the chatter from their specific socket connection */
					Chatter c = msg.getSender();
					server.removeChatterFromAllRooms(c);
					server.removeChatterFromList(c);
					chatters.remove(c);
					c.stopAll();

				} else if (command.equals("ENCRYPT")) {
					/* When a user wants to encrypt their chat room */
					String roomId = String.valueOf(args[1]);
					ChatRoom room = server.getRoomByID(roomId);
					Chatter c = msg.getSender();
					if (room == null || !room.containsChatter(c)) {
						log.warn("Invalid ENCRYPT message from " + msg.getSender().getName());
						continue;
					}
					if (!c.equals(room.getOwner())) {
						log.warn("ENCRYPT message from non-owner " + msg.getSender().getName());
						continue;
					}
					room.encryptRoom();

				} else if (command.equals("Z")) {
					/* Command sent after querying for keys from chatters in a room */
					/* See ConferenceKey.java for more information */
					String roomId = String.valueOf(args[1]);
					ChatRoom room = server.getRoomByID(roomId);
					Chatter c = msg.getSender();
					if (room == null || !room.containsChatter(c)) {
						log.warn("Invalid Z command from " + msg.getSender().getName());
						continue;
					}

					room.addZ(c, String.valueOf(args[2]));
					
				} else if (command.equals("X_KEY")) {
					/* The second broadcast round of Conference keying */
					String roomId = String.valueOf(args[1]);
					ChatRoom room = server.getRoomByID(roomId);
					Chatter c = msg.getSender();
					if (room == null || !room.containsChatter(c)) {
						log.warn("Invalid X command from " + msg.getSender().getName());
						continue;
					}
					
					room.addX(c, String.valueOf(args[2]));

				} else if (command.equals("HIDE")){
					/* When a user wants to hide themselves from user lists */
					boolean bool = Boolean.valueOf(String.valueOf(args[1]));
					Chatter c = msg.getSender();
					if(bool)
						server.sendToAll("USR_LEFT " + c.getName() + "$ ");
					else
						server.sendToAll("USR_ADDED " + c.getName() + "$ ");
				} else if (command.equals("SILENCE")) {
					String roomId = String.valueOf(args[1]);
					ChatRoom room = server.getRoomByID(roomId);
					Chatter c = msg.getSender();
					Chatter silencedChatter = server.getChatterByName(new String(args[2]));
					if (room == null 
					    || !room.containsChatter(c) 
					    || !room.getOwner().equals(c)
					    || silencedChatter == null
					    || !room.containsChatter(silencedChatter)) {
						log.warn("Invalid SILENCE command in room " + roomId + " from " + msg.getSender().getName());
						continue;
					}
					
					if (!room.isSilenced(silencedChatter)) {
						room.silenceChatter(silencedChatter);
					}
					
				} else if (command.equals("UNSILENCE")) {
					String roomId = String.valueOf(args[1]);
					ChatRoom room = server.getRoomByID(roomId);
					Chatter c = msg.getSender();
					Chatter silencedChatter = server.getChatterByName(new String(args[2]));
					if (room == null 
					    || !room.containsChatter(c) 
					    || !room.getOwner().equals(c)
					    || silencedChatter == null
					    || !room.containsChatter(silencedChatter)) {
						log.warn("Invalid UNSILENCE command in room " + roomId + " from " + msg.getSender().getName());
						continue;
					}
					
					if (room.isSilenced(silencedChatter)) {
						room.unsilenceChatter(silencedChatter);
					}

				} else {
					//toss the whole thing.
					log.warn("Non-recognized command from " + msg.getSender().getName());
					continue;
				}
			} catch (Exception e) {
				log.error ("Exception in runloop: " + e.getMessage());
				e.printStackTrace();
				continue;
			}
		} 
	
	}
}