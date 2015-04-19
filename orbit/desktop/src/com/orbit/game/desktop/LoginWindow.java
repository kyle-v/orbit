package com.orbit.game.desktop;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

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
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));	//a container for all JComps
		mainPanel.setSize(1024, 600);

		titleLabel.setSize(200, 200);
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		titleLabel.setFont(new Font("Serif", Font.BOLD, 30));
		container.add(titleLabel);			//adds logo
		
		
		GridBagConstraints textFieldConstraints = new GridBagConstraints();		//constraints for inner panels
		textFieldConstraints.gridx = GridBagConstraints.RELATIVE;
		textFieldConstraints.gridy = 1;
		
		JPanel usernamePanel = new JPanel(new GridBagLayout());			//username info
		usernamePanel.add(usernameLabel, textFieldConstraints);
		usernamePanel.add(usernameTextField, textFieldConstraints);
		
		JPanel passwordPanel = new JPanel(new GridBagLayout());			//password info
		passwordPanel.add(passwordLabel, textFieldConstraints);
		passwordPanel.add(passwordTextField, textFieldConstraints);
		
		
		usernamePanel.setBackground(Color.LIGHT_GRAY);
		passwordPanel.setBackground(Color.LIGHT_GRAY);
		container.add(usernamePanel);
		container.add(passwordPanel);
		
		
		JPanel buttonContainer = new JPanel();						//panel for buttons
		buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.X_AXIS));
		buttonContainer.add(userLoginButton);
		buttonContainer.add(Box.createHorizontalGlue());
		buttonContainer.add(guestLoginButton);
		buttonContainer.add(Box.createHorizontalGlue());
		buttonContainer.add(newUserButton);
		
		buttonContainer.setBackground(Color.LIGHT_GRAY);
		container.add(buttonContainer);					//adds buttons, then adds panel to frame
		container.setBackground(Color.LIGHT_GRAY);
		container.setSize(600,600);
		
		GridBagConstraints centerConstraints = new GridBagConstraints();
		centerConstraints.gridx = 1;
		centerConstraints.gridy = GridBagConstraints.RELATIVE;
		mainPanel.add(container, centerConstraints);
		
		add(mainPanel);
		setSize(1024,600);
		setVisible(true);

	}
	
	public void authenticate(String username, String password){ //checks if username and password are in database, and logins in
		
	}
	
	public void createUser(String username, String password){ //adds username and password to database
		
	}
	
	public void guestLogin(){ //logins without requiring a username or password
		
	}
	
	class JOrbitPanel extends JPanel{				//custom JPanel with overridden paint component

		
		private static final long serialVersionUID = 1L;
		
		JOrbitPanel(){
			this.setLayout(new GridBagLayout());

		}
		
		protected void paintComponent(Graphics g){
			g.drawImage(backgroundImage.getImage() ,0,0,null );
		}
	}

}
