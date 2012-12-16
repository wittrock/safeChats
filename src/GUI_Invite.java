import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class GUI_Invite extends JFrame {
	
	Client client;
	ClientBufferPusher cbp;
	String chat;
	
	public void acceptInvite(){
		client.sendMessage(("JOIN " + chat + " true$ ").toCharArray());
		cbp.removeInvite(chat);
		dispose();
	}
	
	public void rejectInvite(){
		client.sendMessage(("JOIN " + chat + " false$ ").toCharArray());
		cbp.removeInvite(chat);
		dispose();
	}
	
	public GUI_Invite(String userName, String chat, Client client, ClientBufferPusher cbp) {
		setSize(new Dimension(247, 155));
		setResizable(false);
		getContentPane().setLayout(null);
		
		this.client = client;
		this.cbp = cbp;
		this.chat = chat;
		
		JLabel lblYouHaveReceived = new JLabel("You have received an Invitation from:");
		lblYouHaveReceived.setBounds(10, 24, 192, 14);
		getContentPane().add(lblYouHaveReceived);
		
		JLabel label = new JLabel(userName);
		label.setBounds(10, 41, 188, 14);
		getContentPane().add(label);
		
		JButton btnAccept = new JButton("Accept");
		btnAccept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				acceptInvite();
			}
		});
		btnAccept.setBounds(10, 66, 89, 23);
		getContentPane().add(btnAccept);
		
		JButton btnReject = new JButton("Reject");
		btnReject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rejectInvite();
			}
		});
		btnReject.setBounds(109, 66, 89, 23);
		getContentPane().add(btnReject);
	}

	private static final long serialVersionUID = -2430392815964714237L;
}
