import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;



public class GUI_KickNotice extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3445489637854080L;

	public GUI_KickNotice(String chat) {
		setSize(new Dimension(234, 127));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("229px"),},
			new RowSpec[] {
				RowSpec.decode("32px"),
				RowSpec.decode("32px"),
				RowSpec.decode("32px"),}));
		
		JLabel lblNewLabel = new JLabel("You have been kicked");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 13));
		getContentPane().add(lblNewLabel, "1, 1, center, fill");
		
		JLabel lblNewLabel_1 = new JLabel("Chat: " + chat);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		getContentPane().add(lblNewLabel_1, "1, 2, center, fill");
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		getContentPane().add(btnClose, "1, 3, center, center");
	}

}
