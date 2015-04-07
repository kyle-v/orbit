package orbit;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LoginWindow extends Window{
	
	private JTextField usernameTextField;
	private JTextField passwordTextField;
	private JButton userLoginButton;
	private JButton guestLoginButton;
	private JButton newUserButton;
	private JLabel backgroundLabel;
	private JLabel titleLabel;
	private JPanel mainPanel;
	
	LoginWindow(Orbit parent){
		super(parent);
		//initialize all the shit
	}
	
	public void authenticate(String username, String password){ //checks if username and password are in database, and logins in
		
	}
	
	public void createUser(String username, String password){ //adds username and password to databaseb
		
	}
	
	public void guestLogin(){ //logins without requiring a username or password
		
	}
	

}
