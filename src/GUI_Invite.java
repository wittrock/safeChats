import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;


public class GUI_Invite extends JFrame {
	public GUI_Invite(String userName) {
		setResizable(false);
		getContentPane().setLayout(null);
		
		JLabel lblYouHaveReceived = new JLabel("You have received an Invitation from:");
		lblYouHaveReceived.setBounds(10, 24, 192, 14);
		getContentPane().add(lblYouHaveReceived);
		
		JLabel label = new JLabel(userName);
		label.setBounds(10, 41, 188, 14);
		getContentPane().add(label);
		
		JButton btnAccept = new JButton("Accept");
		btnAccept.setBounds(10, 66, 89, 23);
		getContentPane().add(btnAccept);
		
		JButton btnReject = new JButton("Reject");
		btnReject.setBounds(109, 66, 89, 23);
		getContentPane().add(btnReject);
	}

	private static final long serialVersionUID = -2430392815964714237L;
}
