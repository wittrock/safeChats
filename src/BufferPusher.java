/* John Wittrock, Greg Herpel 2012 This is going to become more of a
 * processing class than anything else.  This, or a closely-related
 * class will actually have to parse protocol messages and perform the
 * necessary operations. Still more to do here.
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

	private Message getMessage() {
		try {
		    	return writeBuffer.take();
		} catch (InterruptedException e) {
		    	log.error("BufferPusher InterruptedException for writeBuffer");
		    	e.printStackTrace();
		    	return null;
		}
	}

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


	public void run(){
		while(true) {
			try {
				Message msg = getMessage();
				char[] str = msg.getData();			
				// Parse the protocol stuff out
				int protocolEnd = indexOf(str,'$',0);
				int msgStart = protocolEnd +1;
				if (protocolEnd == -1) {
					// Toss this out. 
					continue;
				}
			
				if (str[protocolEnd - 1] == ' ') {
					protocolEnd--;
				}

				char[] protocol = substring(str, 0, protocolEnd);
				char[] userMessage = substring(str, msgStart, str.length);

				
				char[][] args = split(protocol,' ');
				int numArgs = args.length;
				String command = String.valueOf(args[0]);
				
			
				log.trace(msg.getSender().getName()+": Got " + command +" message");

				if (command.equals("CREATE")) {
					log.trace("Sending create command to " + msg.getSender().getName());
					server.createRoom(msg.getSender());
					continue;

				} else if (command.equals("MSG")) {
					if (numArgs < 2) {
						//incorrectly formatted message. Log here?
						continue;//toss.
					}
					String roomID = String.valueOf(args[1]);
					Chatter sender = msg.getSender();
					ChatRoom room = server.getRoomByID(roomID);
					if (room == null || !room.containsChatter(sender)) {
						//invalid room. Log here?
						continue;
					}

					// We know now that the sender is authorized to send to this room.
					System.out.print("Args: ");
					for (int i = 0; i < args.length ; i ++ ) {
						System.out.print("" + i + ": "  + new String(args[i]) + " ");
					}
					System.out.println();
					if (numArgs == 2) {
						// non-encrypted message
						System.out.println("Got non-encrypted message");
						String annotatedMsg = "MSG " + roomID + " $ "  + sender.getName() + ": " + String.valueOf(userMessage);
						System.out.println("User message: " + new String(userMessage));
						room.distributeMessage(annotatedMsg);
					} else {
						System.out.println("Got encrypted message");
						String annotatedMsg = "MSG " + 
							roomID + " " + 
							String.valueOf(args[2]) + " " + // this will be the iv. 
							" $ " + String.valueOf(userMessage);

						room.distributeMessage(annotatedMsg);
					}


				} else if (command.equals("INVITE")) {
					if(numArgs < 3 || countChar(str, '$') > 1) {
						continue; // invalid invite message. Log here?
					}

					String invitedChatter = String.valueOf(args[1]);
					String roomId = String.valueOf(args[2]);
					Chatter c = server.getChatterByName(invitedChatter);
					if (c == null) {
						// send a failure message here. 
						String inviteFailure = "INVITE_FAILED " + String.valueOf(args[1]) + " $ ";
						msg.getSender().addMessage(inviteFailure);
						continue;
					}
				
					// check that the sender is actually in the room. 
					ChatRoom room = server.getRoomByID(roomId);
					if (room == null || !room.getOwner().getName().equals(msg.getSender().getName()) || room.containsChatter(c)) {
						// invalid room. will consider sending a different failure message here.
						continue;
					}

					room.inviteChatter(c);
					// forward on the invite message.
					c.addMessage("" + String.valueOf(protocol) + " $ ");

				} else if (command.equals("KICK")){
					if(numArgs < 3 || countChar(str, '$') > 1) {
						continue; // invalid invite message. Log here?
					}
					
					String kickedChatter = String.valueOf(args[1]);
					String roomId = String.valueOf(args[2]);
					ChatRoom room = server.getRoomByID(roomId);
					Chatter c = server.getChatterByName(kickedChatter);
					if (c == null || room == null || !room.getOwner().getName().equals(msg.getSender().getName()) || !room.containsChatter(c))
						continue;
					
					room.removeChatter(c);
					room.distributeMessage("CHTR_LEFT " + c.getName() + " " + roomId + " $ ");
					c.addMessage("" + String.valueOf(protocol) + " $ ");
					
				} else if (command.equals("JOIN")) {
					if (numArgs < 2) {
						continue;
					}
				
					String roomId = String.valueOf(args[1]);
					//				Chatter c = server.getChatterByName(invitedChatter);
					Chatter c = msg.getSender();
					ChatRoom room = server.getRoomByID(roomId);
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
					if (numArgs < 2) { continue; }
					String roomId = String.valueOf(args[1]);
					ChatRoom room = server.getRoomByID(roomId);
					Chatter c = msg.getSender();
					if (room == null || !room.containsChatter(c)) continue;

					room.removeChatter(c);

					if (room.size() == 0) {
						// commenting this out for
						// later when we may want to
						// give more priveleges to
						// owners

						// room.distributeMessage("RM_DESTROYED " + roomId + " $ ");

						// actually remove the room. 
						server.removeRoom(room.getID());
						log.trace("Room destroyed: " + room.getID());
					} else {
						room.distributeMessage("CHTR_LEFT " + c.getName() + " " + roomId + " $ ");
					}

				} else if (command.equals("USR_LEFT")){
					Chatter c = msg.getSender();
					server.removeChatterFromAllRooms(c);
					server.removeChatterFromList(c);
					chatters.remove(c);
					c.stopAll();

				} else if (command.equals("ENCRYPT")) {
					String roomId = String.valueOf(args[1]);
					ChatRoom room = server.getRoomByID(roomId);
					Chatter c = msg.getSender();
					if (room == null || !room.containsChatter(c)) continue;
					
					room.encryptRoom();

				} else if (command.equals("Z")) {
					String roomId = String.valueOf(args[1]);
					ChatRoom room = server.getRoomByID(roomId);
					Chatter c = msg.getSender();
					if (room == null || !room.containsChatter(c)) continue;

					room.addZ(c, String.valueOf(args[2]));
					
				} else if (command.equals("X_KEY")) {
					String roomId = String.valueOf(args[1]);
					ChatRoom room = server.getRoomByID(roomId);
					Chatter c = msg.getSender();
					if (room == null || !room.containsChatter(c)) continue;
					
					room.addX(c, String.valueOf(args[2]));

				} else if (command.equals("HIDE")){
					boolean bool = Boolean.valueOf(String.valueOf(args[1]));
					Chatter c = msg.getSender();
					if(bool)
						server.sendToAll("USR_LEFT " + c.getName() + "$ ");
					else
						server.sendToAll("USR_ADDED " + c.getName() + "$ ");
				}
				else {
					//toss out the whole thing. we should add logging here.
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