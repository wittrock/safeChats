/*
 * Greg Herpel, John Wittrock, 2012
 * This is the main class for the client
 */

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.util.LinkedList;

import javax.net.ssl.SSLSocket;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;


public class Client {
	private static final int PORT = 46754;
	private static String HOST;
	private SSLSocket s;
	private BufferedWriter typedWriter;
	private GUI_SignIn gsi;
	private GUI_CreateAccount gca;
	private ClientBufferPusher cbp;
	private ClientMessageListener cml;
	
	public Client(){
		try{
			SocketFactory sf = SSLSocketFactory.getDefault();
			LinkedList<String> su = new LinkedList<String>(); //list of cipherSuites we will use
			su.add("TLS_DHE_RSA_WITH_AES_256_CBC_SHA");
			su.add("TLS_DHE_RSA_WITH_AES_128_CBC_SHA");
			su.add("TLS_DHE_DSS_WITH_AES_256_CBC_SHA");
			su.add("TLS_DHE_DSS_WITH_AES_128_CBC_SHA");
			s = (SSLSocket)sf.createSocket();
			String[] suites = s.getSupportedCipherSuites();
			for(int i=0;i<su.size();i++){	//make sure cipherSuites are supported
				boolean counter = true;
				for(String str: suites){
					if(str.equals(su.get(i)))
						counter = false;
				}
				if(counter){
					su.remove(su.get(i));
					i--;
				}
			}
			
			suites = listToArray(su);
			s.setEnabledCipherSuites(suites);
			s.connect(new InetSocketAddress(HOST,PORT));
			typedWriter = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		}catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	public String[] listToArray(LinkedList<String> list){
		String[] ret = new String[list.size()];
		for(int i=0;i<list.size();i++){
			ret[i] = list.get(i);
		}
		return ret;
	}
	
	public void authed(){
		gsi.disposeCall();
		gca.disposeCall();
	}
	
	public void authFailed(){
		gsi.authFailed();
	}
	
	public void newAccFailed(){
		gca.accFailed();
	}
	
	public void newAccSwitch(){
		gsi.setVisible(false);
		gca.setVisible(true);
	}

	/* This method spawns a thread to listen for messages */
	public void runChat(ClientBufferPusher cbp){
		try{
			cml = new ClientMessageListener(s,cbp);
			(new Thread(cml)).start();
			(new Thread(cbp)).start();
		}catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	public void hideName(){
		sendMessage(("HIDE true$ ").toCharArray());
	}
	
	public void showName(){
		sendMessage(("HIDE false$ ").toCharArray());
	}

	public void createChat() {
		System.out.println("Client: creating message.");
		sendMessage(("CREATE$ ").toCharArray());
	}
	
	//concatinates the elements of the auth or newAcc message and zeros out the string buffer
	public void authUser(String userName, char[] password) {
		StringBuffer sb = new StringBuffer();
		sb.append("AUTH "+userName+" ");
		sb.append(password);
		sb.append("$ ");
		char[] ch = new char[sb.length()];
		sb.getChars(0, sb.length(), ch, 0);
		sendMessage(ch);
		sb.delete(0, sb.length());
	}
	
	public void newAcc(String userName, char[] password){
		StringBuffer sb = new StringBuffer();
		sb.append("NEW_ACC "+userName+" ");
		sb.append(password);
		sb.append("$ ");
		char[] ch = new char[sb.length()];
		sb.getChars(0, sb.length(), ch, 0);
		sendMessage(ch);
		sb.delete(0, sb.length());
	}
	

	public void leaveRoom(GUI_ChatInterface chat) {
		sendMessage(("CHTR_LEFT " + chat.getChatID() + "$ ").toCharArray());
		cbp.leaveChat(chat.getChatID());
	}
	
	public void leaveSignIn(){
		gca.disposeCall();
		leaveUser();
	}
	public void leaveCreateAcc(){
		gsi.disposeCall();
		leaveUser();
	}
	public void leaveMenu(){
		cbp.killChats();
		leaveUser();
	}
	public void leaveUser(){
		sendMessage(("USR_LEFT$ ").toCharArray());
		cbp.killProc();
		cml.killProc();
	}
	
	//method used to send all message from the client
	public synchronized void sendMessage(char[] str){
		try{
			typedWriter.write(str, 0, str.length);
			typedWriter.newLine();
			typedWriter.flush();
		}catch (Exception e) {
			System.out.println("Caught exception in sendMessage");
			e.printStackTrace();
			return;
		}
	}

	/* 
	 * Pretty simple class, this. Makes a new GUI, spawns a couple of threads, listens to chats, writes chats. 
	 */	
	public static void main(String[] args){
		if (args.length < 1) {
			System.out.println("No HOST specified... defaulting to localhost.");
			HOST = "localhost";
		} else {
			HOST = args[0];
		}
		Client c = new Client();
		c.gsi = new GUI_SignIn(c);
		c.gsi.setVisible(true);
		c.gca = new GUI_CreateAccount(c);
		c.gca.setVisible(false);
		c.cbp = new ClientBufferPusher(c);
		c.runChat(c.cbp);
	}
}
