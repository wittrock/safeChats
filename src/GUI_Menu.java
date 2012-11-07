import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class GUI_Menu extends JFrame {
	
	String[] users;
	Client client;

	public void newChat() {
		client.createChat();
	}

	public GUI_Menu(Client client) {
		this.client = client;
		setResizable(true);
		setSize(200,200);
		getContentPane().setLayout(null);

		JButton btnCreateAChat = new JButton("Create a Chat Room");
		btnCreateAChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// GUI_UserList gul = new GUI_UserList(users);
				// gul.setVisible(true);
				//				GUI_ChatInterface chat = new GUI_ChatInterface(
				newChat();
				
			}
		});
		btnCreateAChat.setBounds(0, 0, 173, 79);
		getContentPane().add(btnCreateAChat);
		setVisible(true);
	}

	private static final long serialVersionUID = 290374249345775044L;
}
