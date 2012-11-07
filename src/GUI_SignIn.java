import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.JPasswordField;


public class GUI_SignIn extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3481153769518892625L;
	private JTextField userNameField;
	private JPasswordField passwordField;
	private Client client;
	
	private void attemptAuth(){
		new GUI_Menu(this.client);
		this.setVisible(false);
	}
	
	private void createNew(){
		
	}
	
	public GUI_SignIn(Client client) {
		this.client = client;
		setResizable(true);
		setSize(400, 310);
		setTitle("SafeChats-Sign In");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setFont(new Font("Tunga", Font.PLAIN, 12));
		getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("User Name:");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel.setBounds(29, 75, 73, 24);
		getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Password:");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1.setBounds(29, 110, 73, 24);
		getContentPane().add(lblNewLabel_1);
		
		userNameField = new JTextField();
		userNameField.setBounds(112, 78, 151, 20);
		getContentPane().add(userNameField);
		userNameField.setColumns(10);
		
		JLabel lblNew = new JLabel("New?");
		lblNew.setBounds(69, 29, 32, 14);
		getContentPane().add(lblNew);
		
		JButton btnClickHere = new JButton("Click Here");
		btnClickHere.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				createNew();
			}
		});
		btnClickHere.setBounds(102, 27, 79, 19);
		getContentPane().add(btnClickHere);
		
		JButton btnSignIn = new JButton("Sign In");
		btnSignIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				attemptAuth();
			}
		});
		btnSignIn.setBounds(112, 144, 89, 23);
		getContentPane().add(btnSignIn);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(112, 110, 151, 23);
		getContentPane().add(passwordField);
	}
}
