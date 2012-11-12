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

	private static int countChar(String str, char c) {
		int count = 0;
		for (int i = 0; i < str.length() ; i++) {
			if (c == str.charAt(i)) {
				count++;
			}
		}
		return count;
	}


	public void run(){
		while(true) {
			Message msg = getMessage();
			String str = msg.getData();			
			// Parse the protocol stuff out
			int protocolEnd = str.indexOf((int)'$');
			if (protocolEnd == -1) {
				// Toss this out. 
				continue;
			}
			
			String protocol = str.substring(0, protocolEnd);
			String userMessage = str.substring(protocolEnd + 1);
			
			String[] args = protocol.split(" ");
			int numArgs = args.length;
			String command = args[0];
			
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
				String roomID = args[1];
				Chatter sender = msg.getSender();
				ChatRoom room = server.getRoomByID(roomID);
				if (room == null) {
					//invalid room. Log here?
					continue;
				}
				if (room.containsChatter(sender)) {
					String annotatedMsg = "MSG " + roomID + " $ "  + sender.getName() + ": " + userMessage;
					room.distributeMessage(annotatedMsg);
				}

			} else if (command.equals("INVITE")) {
				if(numArgs < 3 || countChar(str, '$') > 1) {
					continue; // invalid invite message. Log here?
				}

				String invitedChatter = args[1];
				String roomId = args[2];
				Chatter c = server.getChatterByName(invitedChatter);
				if (c == null) {
					// send a failure message here. 
					String inviteFailure = "INVITE_FAILED " + args[1] + " $ ";
					msg.getSender().addMessage(inviteFailure);
					continue;
				}
				
				// check that the sender is actually in the room. 
				ChatRoom room = server.getRoomByID(roomId);
				if (room == null || !room.containsChatter(msg.getSender()) || room.containsChatter(c)) {
					// invalid room. will consider sending a different failure message here.
					continue;
				}

				room.inviteChatter(c);
				// forward on the invite message.
				c.addMessage("" + protocol + " $ ");


			} else if (command.equals("JOIN")) {
				if (numArgs < 2) {
					continue;
				}
				
				String roomId = args[1];
				//				Chatter c = server.getChatterByName(invitedChatter);
				Chatter c = msg.getSender();
				ChatRoom room = server.getRoomByID(roomId);
				if (c == null || room == null || !room.addChatter(c)) {
					log.error("No invited chatter by that name: " + msg.getSender().getName());
					continue;
				}

			} else if(command.equals("AUTH")){
				// We should do some error checking here.
				if (numArgs !=3){continue;}
				server.authUser(args[1],args[2],msg.getSender());

			} else if (command.equals("NEW_ACC")){
				if (numArgs !=3){continue;}
				server.newAcc(args[1], args[2], msg.getSender());

			} else if (command.equals("CHTR_LEFT")) {
				if (numArgs < 2) { continue; }
				String roomId = args[1];
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

			} else if(command.equals("USR_LEFT")){
				Chatter c = msg.getSender();
				server.removeChatterFromAllRooms(c);
				server.removeChatter(c);
				chatters.remove(c);
				c.stopAll();
			}else {
				//toss out the whole thing. we should add logging here.
				continue;
			}
		}
	}
}