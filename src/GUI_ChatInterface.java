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


public class GUI_ChatInterface extends JFrame {

	private static final long serialVersionUID = 3115636304040322146L;
	
	
	private JTextArea chatText;
	private JTextArea userText;
	private JTextField inviteField;
	private JButton inviteButton;
	private JButton btnSend;
	private Client client;
	private String chatID;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private JTextArea chatterList;
	private ArrayList<String> chatters;
	
	public void addChatText(String txt){
		chatText.append(txt);
	}
	
	public void sendUserText(){
		client.sendMessage("MSG " + chatID + " $ " + userText.getText());
		userText.setText("");
	}
	
	public void inviteUser() {
		String invite = inviteField.getText();
		if (invite.length() <= 0) return;
		client.sendMessage("INVITE " + invite + " " + chatID + " $ ");
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
		this.chatID = chatID;
		client = c;
		Dimension dim = new Dimension(375, 285);
		this.setSize(dim);
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
		
		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, "4, 1, 1, 3, fill, fill");
		panel_1.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("20px:grow"),
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("23px"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(11dlu;default)"),}));
		
		scrollPane_1 = new JScrollPane();
		panel_1.add(scrollPane_1, "1, 2, 1, 9, fill, fill");
		
		chatterList = new JTextArea();
		chatterList.setEditable(false);
		chatterList.setWrapStyleWord(true);
		scrollPane_1.setViewportView(chatterList);
		
		inviteField = new JTextField(20);
		panel_1.add(inviteField, "1, 12, left, top");
		
		inviteButton = new JButton("Invite...");
		panel_1.add(inviteButton, "1, 14, center, top");
		inviteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0){
					inviteUser();
				}
			});
		
		scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, "2, 2, fill, fill");
		
		chatText = new JTextArea();
		scrollPane.setViewportView(chatText);
		chatText.setWrapStyleWord(true);
		chatText.setEditable(false);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		getContentPane().add(panel, "1, 3, 2, 2, fill, fill");
		panel.setLayout(new FormLayout(new ColumnSpec[] {
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
		panel.add(userText, "1, 1, 2, 2, fill, fill");

		btnSend = new JButton("SEND");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sendUserText();
			}
		});
		getContentPane().add(btnSend, "4, 4");
	}

}
