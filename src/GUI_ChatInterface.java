import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.*;
import java.util.*;
import java.util.LinkedList;
import javax.swing.JScrollPane;
import java.math.*;
import java.awt.SystemColor;

public class GUI_ChatInterface extends JFrame {

	private static final long serialVersionUID = 3115636304040322146L;
	
	
	private JTextArea chatText;
	private JTextArea userText;
	private JTextField inviteField;
	private JButton inviteButton;
	private JButton btnSend;
	private Client client;
	private String chatID;
	private JScrollPane outputPane;
	private JScrollPane chatterPane;
	private JTextArea chatterList;
	private ArrayList<String> chatters;
	private ConferenceKey ckey;
	private final JPanel sendPanel = new JPanel();
	private JButton btnEncrypt;
	private JPanel ownerPanel;
	private JButton btnKick;
	
	public void addChatText(String txt){
		chatText.append(txt);
	}
	
	public void notOwner(){
		inviteButton.setEnabled(false);
		btnKick.setEnabled(false);
		btnEncrypt.setEnabled(false);
	}
	
	public void sendUserText(){
		client.sendMessage(("MSG " + chatID + " $ " + userText.getText()).toCharArray());
		userText.setText("");
	}
	
	public void inviteUser() {
		String invite = inviteField.getText();
		if (invite.length() <= 0) return;
		client.sendMessage(("INVITE " + invite + " " + chatID + "$ ").toCharArray());
		inviteField.setText("");
	}
	public void kickChatter(){
		String kick = inviteField.getText();
		if(kick.length()<= 0) return;
		client.sendMessage(("KICK " + kick + " " + chatID + "$ ").toCharArray());
		inviteField.setText("");
	}
	
	public void addChatter(String name){
		if(!chatters.contains(name) && name != null){
			chatters.add(name);
			updateChatterDisplay();
		}
	}
	
	public void removeChatter(String name){
		if (!chatters.contains(name)) return;
		chatters.remove(name);
		updateChatterDisplay();
	}
	
	public void updateChatterDisplay(){
		chatterList.setText("");
		for(String un:chatters){
			chatterList.append(un +"\n");
		}
	}

	/* Begin conference-keying interface functions */
	public void startEncryption() {
		client.sendMessage(("ENCRYPT " + chatID + " $ ").toCharArray());
	}
	
	public void genKeys() {
		ckey = new ConferenceKey();
		client.sendMessage(("Z " + chatID + " " + ckey.getZ() + " $ ").toCharArray());
	}

	public void broadcastX(String l, String r) {
		if (this.ckey == null) return;
		BigInteger left = new BigInteger(l);
		BigInteger right = new BigInteger(r);
		client.sendMessage(("X_KEY " + chatID + " " + ckey.generateX(left, right).toString() + " $ ").toCharArray());
	}

	public void receiveXs(String indexStr, String leftZ, String[] xs) {
		if (this.ckey == null) return;
		int index = Integer.parseInt(indexStr);
		BigInteger z = new BigInteger(leftZ);
		BigInteger[] xs_ints = new BigInteger[xs.length];
		for (int i = 0; i < xs.length; i++) {
			xs_ints[i] = new BigInteger(xs[i]);
		}
		ckey.calculateSharedKey(xs_ints, index, z);
	}

	/* End conference-keying interface functions */

	public void dispose() {
		client.leaveRoom(this);
		super.dispose();
	}
	
	public void disposeCall(){
		super.dispose();
	}

	public String getChatID() { return chatID; }
	

	public GUI_ChatInterface(Client c, String chatID) {
		this.chatters = new ArrayList<String>();
		this.ckey = null;
		this.chatID = chatID;
		client = c;
		Dimension dim = new Dimension(375, 285);
		this.setSize(new Dimension(385, 312));
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		DisplayMode screen = gd.getDisplayMode();
		this.setLocation((screen.getWidth()-dim.width)/2, (screen.getHeight()-dim.height)/2);
		this.setTitle(chatID); // may want to remove this if/when we want to not display debug

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//		setTitle("SafeChats");
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("258px:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("center:90px:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(113dlu;default):grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(39dlu;default):grow"),}));
		
		JPanel chatterPanel = new JPanel();
		getContentPane().add(chatterPanel, "4, 1, 1, 3, fill, fill");
		Dimension d = chatterPanel.getSize();
		chatterPanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("75px:grow"),},
			new RowSpec[] {
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("98px:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("10dlu:grow"),}));
		
		chatterPane = new JScrollPane();
		chatterPanel.add(chatterPane, "1, 2, fill, fill");
		
		chatterList = new JTextArea();
		chatterList.setEditable(false);
		chatterList.setWrapStyleWord(true);
		chatterPane.setViewportView(chatterList);
		
		inviteField = new JTextField(20);
		chatterPanel.add(inviteField, "1, 4, fill, top");
		
		ownerPanel = new JPanel();
		chatterPanel.add(ownerPanel, "1, 5, 1, 2, fill, fill");
		ownerPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		inviteButton = new JButton("Invite");
		inviteButton.setBackground(SystemColor.inactiveCaption);
		ownerPanel.add(inviteButton);
		
		btnKick = new JButton("Kick");
		btnKick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				kickChatter();
			}
		});
		btnKick.setBackground(SystemColor.inactiveCaption);
		ownerPanel.add(btnKick);
		inviteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0){
					inviteUser();
				}
			});
		
		outputPane = new JScrollPane();
		getContentPane().add(outputPane, "2, 2, fill, fill");
		
		chatText = new JTextArea();
		outputPane.setViewportView(chatText);
		chatText.setWrapStyleWord(true);
		chatText.setEditable(false);
		
		JPanel inputPanel = new JPanel();
		inputPanel.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		getContentPane().add(inputPanel, "1, 3, 2, 2, fill, fill");
		inputPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		
		userText = new JTextArea();
		userText.setWrapStyleWord(true);
		userText.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent arg0) {
				if(arg0.getKeyChar()=='\n'){
					userText.setText(userText.getText().replace("\n","" ));
					sendUserText();
				}
			}
		});
		inputPanel.add(userText, "1, 1, 2, 2, fill, fill");
				getContentPane().add(sendPanel, "4, 4, fill, fill");
						
						btnEncrypt = new JButton("Encrypt");
						btnEncrypt.setBackground(SystemColor.inactiveCaption);
						btnEncrypt.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								startEncryption();
							}
						});
						sendPanel.setLayout(new GridLayout(0, 1, 0, 0));
						sendPanel.add(btnEncrypt);
				
						btnSend = new JButton("SEND");
						sendPanel.add(btnSend);
				btnSend.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						sendUserText();
					}
				});
	}

}
