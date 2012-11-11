/* 
 * This is going to become more of a processing class than anything else. 
 * This, or a closely-related class will actually have to parse status messages and perform 
 * the necessary operations. Still more to do here. 
 */ 

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;


public class BufferPusher extends Thread {
	private List<Chatter> chatters;
	private LinkedBlockingQueue<Message> writeBuffer;
	private Server server;

	public BufferPusher(List<Chatter> chatters, LinkedBlockingQueue<Message> writeBuffer, Server server) {
		this.chatters = chatters;
		this.writeBuffer = writeBuffer;
		this.server = server;
	}

	public Message getMessage() {
		try {
		    	return writeBuffer.take();
		} catch (InterruptedException e) {
		    	System.out.println("BufferPusher InterruptedException!");
		    	e.printStackTrace();
		    	return null;
		}
	}


	public void run(){
		while(true) {
			Message msg = getMessage();
			String str = msg.getData();
			System.out.println("Got message: " + str);			
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

			if (command.equals("CREATE")) {
				System.out.println("Sending create command to " + msg.getSender().getName());
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
				if(numArgs < 3) {
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
					System.out.println("No invited chatter by that name: " + msg.getSender().getName());
					continue;
				}
				
				// to all except the new user we send a "USR_ADDED"
				// to the new user we send a series of USR_ADDED to denote the contents of the room.
				

			} else if(command.equals("AUTH")){
				// We should do some error checking here. 
				server.authUser(args[1],args[2],msg.getSender());
				continue;
			} else if (command.equals("NEW_ACC")){
				server.newAcc(args[1], args[2], msg.getSender());
			} else if (command.equals("USR_LEFT")) {
				if (numArgs < 2) { continue; }
				String roomId = args[1];
				ChatRoom room = server.getRoomByID(roomId);
				Chatter c = msg.getSender();
				if (room == null || !room.containsChatter(c)) continue;

				
				room.removeChatter(c);
				if (room.getOwner() == msg.getSender() || room.size() == 0) {
					room.distributeMessage("RM_DESTROYED " + roomId + " $ ");
					// remove the room. 
				} else {
					room.distributeMessage("USR_LEFT " + c.getName() + " " + roomId + " $ ");
				}

			} else {
				//toss out the whole thing.
				continue;
			}

		}
	}

}