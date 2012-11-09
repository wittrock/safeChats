import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.sun.media.sound.Toolkit;

import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Dimension;


public class GUI_ChatInterface extends JFrame {

	private static final long serialVersionUID = 3115636304040322146L;
	
	
	private JTextArea chatText;
	private JTextArea userText;
	private JButton btnSend;
	private Client client;
	private String chatID;
	
	public void addChatText(String txt){
		chatText.append(txt);
	}
	
	public void sendUserText(){
		client.sendMessage("MSG " + chatID + " $ " + userText.getText());
		userText.setText("");
	}
	
	
	public GUI_ChatInterface(Client c, String chatID) {
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
		getContentPane().add(panel_1, "4, 1, 1, 2, fill, fill");
		panel_1.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(88dlu;default)"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		getContentPane().add(panel_2, "1, 1, 2, 2, fill, fill");
		panel_2.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		
		chatText = new JTextArea();
		chatText.setWrapStyleWord(true);
		chatText.setEditable(false);
		panel_2.add(chatText, "1, 1, 2, 2, fill, fill");
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		getContentPane().add(panel, "1, 3, 2, 2, fill, fill");
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(28dlu;default):grow"),}));
		
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
