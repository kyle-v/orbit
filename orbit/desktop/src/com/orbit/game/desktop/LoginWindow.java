package com.orbit.game.desktop;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginWindow extends Window{

	private static final long serialVersionUID = 6765474456932332037L;
	private JTextField usernameTextField;
	private JPasswordField passwordTextField;
	private JButton userLoginButton;
	private JButton guestLoginButton;
	private JButton newUserButton;
	private JLabel titleLabel;
	private JOrbitPanel mainPanel;
	private final ImageIcon backgroundImage = new ImageIcon("assets/LoginWallpaper.jpg");
	private final JLabel usernameLabel = new JLabel("Username: ");
	private final JLabel passwordLabel = new JLabel("Password: ");
	
	
	//need to pass in an orbit ref. will temporarily use blank constructor
	LoginWindow(){
		
		//initialize all the shit
		titleLabel = new JLabel("Orbit");
		usernameTextField = new JTextField(25);
		passwordTextField = new JPasswordField(25);
		userLoginButton = new JButton("Login");
		guestLoginButton = new JButton("Play as Guest");
		newUserButton = new JButton("Create User");
		mainPanel = new JOrbitPanel();	
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		mainPanel.setSize(1024, 600);

		titleLabel.setSize(200, 200);
		titleLabel.setFont(new Font("Serif", Font.BOLD, 30));
		container.add(titleLabel);			//adds logo
		
		
		JPanel usernamePanel = new JPanel(new GridLayout(1,2));			//username info
		usernamePanel.add(usernameLabel);
		usernamePanel.add(usernameTextField);
		
		JPanel passwordPanel = new JPanel(new GridLayout(1,2));			//password info
		passwordPanel.add(passwordLabel);
		passwordPanel.add(passwordTextField);
		
		
		container.add(usernamePanel);
		container.add(passwordPanel);
		
		
		JPanel buttonContainer = new JPanel();						//panel for buttons
		buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.X_AXIS));
		buttonContainer.add(userLoginButton);
		buttonContainer.add(Box.createHorizontalGlue());
		buttonContainer.add(guestLoginButton);
		buttonContainer.add(Box.createHorizontalGlue());
		buttonContainer.add(newUserButton);
		
		container.add(buttonContainer);					//adds buttons, then adds panel to frame
		
		mainPanel.add(container, BorderLayout.CENTER);
		
		add(mainPanel);
		setSize(1024,600);
		setVisible(true);

	}
	
	public void authenticate(String username, String password){ //checks if username and password are in database, and logins in
		
	}
	
	public void createUser(String username, String password){ //adds username and password to databaseb
		
	}
	
	public void guestLogin(){ //logins without requiring a username or password
		
	}
	
	class JOrbitPanel extends JPanel{				//custom JPanel with overridden paint component

		
		private static final long serialVersionUID = 1L;
		
		JOrbitPanel(){
			//this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

		}
		
		protected void paintComponent(Graphics g){
			g.drawImage(backgroundImage.getImage() ,0,0,null );
		}
	}

}
