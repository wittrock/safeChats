import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class GUI_Menu extends JFrame {
	
	String[] users;
	
	public GUI_Menu() {
		setResizable(false);
		getContentPane().setLayout(null);
		
		JButton btnCreateAChat = new JButton("Create a Chat Room");
		btnCreateAChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				GUI_UserList gul = new GUI_UserList(users);
				gul.setVisible(true);
			}
		});
		btnCreateAChat.setBounds(0, 0, 173, 79);
		getContentPane().add(btnCreateAChat);
	}

	private static final long serialVersionUID = 290374249345775044L;
}
