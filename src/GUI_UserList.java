import javax.swing.JFrame;
import javax.swing.JList;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Arrays;


public class GUI_UserList extends JFrame{
	
	private static final long serialVersionUID = 7381580261961643887L;
	private JList list;
	
	public GUI_UserList(String[] users) {
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		list = new JList(users);
		scrollPane.setViewportView(list);
		
		JButton btnInvite = new JButton("Invite");
		btnInvite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object[] obj = list.getSelectedValues();
				String[] invitees =  Arrays.copyOf(obj,obj.length,String[].class);
				
			}
		});
		getContentPane().add(btnInvite, BorderLayout.SOUTH);
	}

}
