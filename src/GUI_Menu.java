import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.LinkedList;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;


public class GUI_Menu extends JFrame {
	
	Client client;
	JTextArea userList;
	LinkedList<String> users;
	JCheckBox chckbxHideYourName;

	public void newChat() {
		client.createChat();
	}
	
	public void dispose() {
		client.leaveMenu();
		super.dispose();
	}
	
	public void addUser(String name){
		if(!users.contains(name)){
			users.add(name);
			updateUserDisplay();
		}
	}
	
	public void hideName(){
		client.hideName();
	}
	
	public void showName(){
		client.showName();
	}
	
	public void removeUser(String name){
		users.remove(name);
		updateUserDisplay();
	}
	
	public void updateUserDisplay(){
		userList.setText("");
		for(String un:users){
			userList.append(un+"\n");
		}
	}

	public GUI_Menu(Client client) {
		this.client = client;
		setResizable(true);
		setSize(193,336);
		users = new LinkedList<String>();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("173px:grow"),},
			new RowSpec[] {
				RowSpec.decode("28px"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("24px"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(153dlu;default):grow"),}));
				
						JButton btnCreateAChat = new JButton("Create a Chat Room");
						btnCreateAChat.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								newChat();
								
							}
						});
						getContentPane().add(btnCreateAChat, "1, 1, fill, fill");
						
						JPanel panel = new JPanel();
						getContentPane().add(panel, "1, 3, fill, fill");
						
						chckbxHideYourName = new JCheckBox("Hide My Name");
						chckbxHideYourName.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								if(chckbxHideYourName.isSelected())
									hideName();
								else
									showName();
							}
						});
						panel.add(chckbxHideYourName);
						
						JScrollPane scrollPane = new JScrollPane();
						getContentPane().add(scrollPane, "1, 5, fill, fill");
						
						userList = new JTextArea();
						userList.setEditable(false);
						scrollPane.setViewportView(userList);
		setVisible(true);
	}

	private static final long serialVersionUID = 290374249345775044L;
}
