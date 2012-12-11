import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;


public class GUI_CreateAccount extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7827731606231193671L;
	private JTextField textField;
	private JPasswordField passwordField;
	private JPasswordField passwordField_1;
	private JLabel lblError;
	private Client client;
	
	private int indexOf(char[] str, char c,int start){
		if(start>=str.length)
			return -1;
		for(int i=start;i<str.length;i++){
			if(str[i]==c){
				return i;
			}
		}
		return -1;
	}
	
	private boolean charEquals(char[] a, char[] b){
		if(a.length != b.length)
			return false;
		for(int i=0;i<a.length;i++){
			if(a[i] != b[i])
				return false;
		}
		return true;
	}
	
	private void NEW_ACC(){
		char[] pword = passwordField.getPassword();
		char[] pwordCheck = passwordField_1.getPassword();
		if(charEquals(pword,pwordCheck)){
			String name = textField.getText();
			if(name.contains("$")||name.contains(" ")||indexOf(pword,'$',0)!=-1||indexOf(pword,' ',0)!=-1){
				lblError.setText("Invalid Character Used ($, )");
			}else{
				client.newAcc(name,pword);
			}
		}
		else{
			lblError.setText("passwords did not match");
			passwordField.setText("");
			passwordField_1.setText("");
		}
	}
	
	public void accFailed(){
		lblError.setText("userName has already be used");
	}
	
	public void dispose() {
		client.leaveCreateAcc();
		super.dispose();
	}
	
	public void disposeCall(){
		super.dispose();
	}
	
	public GUI_CreateAccount(Client c) {
		
		this.client = c;
		
		setSize(340,225);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("SafeChats-New Account");
		getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 12));
		getContentPane().setLayout(null);
		
		JLabel lblEnterUsername = new JLabel("Enter Username:");
		lblEnterUsername.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblEnterUsername.setBounds(33, 57, 109, 23);
		getContentPane().add(lblEnterUsername);
		
		textField = new JTextField();
		textField.setBounds(152, 59, 141, 20);
		getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel lblEnterPassword = new JLabel("Enter Password:");
		lblEnterPassword.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblEnterPassword.setBounds(33, 86, 109, 23);
		getContentPane().add(lblEnterPassword);
		
		passwordField = new JPasswordField();
		passwordField.setToolTipText("");
		passwordField.setBounds(152, 87, 141, 20);
		getContentPane().add(passwordField);
		
		passwordField_1 = new JPasswordField();
		passwordField_1.setBounds(152, 118, 141, 20);
		getContentPane().add(passwordField_1);
		
		JLabel lblReenterPassword = new JLabel("Re-Enter Password:");
		lblReenterPassword.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblReenterPassword.setBounds(33, 116, 109, 23);
		getContentPane().add(lblReenterPassword);
		
		JButton btnCreateAccount = new JButton("Create Account");
		btnCreateAccount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				NEW_ACC();
			}
		});
		btnCreateAccount.setBounds(152, 149, 141, 23);
		getContentPane().add(btnCreateAccount);
		
		lblError = new JLabel("");
		lblError.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblError.setForeground(Color.RED);
		lblError.setBounds(33, 35, 260, 14);
		getContentPane().add(lblError);
	}
}
