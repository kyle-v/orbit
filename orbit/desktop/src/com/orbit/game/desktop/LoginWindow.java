package com.orbit.game.desktop;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import orbit.ServerListenerThread;
import orbit.ServerRequest;
import orbit.User;

public class LoginWindow extends Window{
	ServerListenerThread  slt;
	
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
	LoginWindow(Orbit orbit){
		super(orbit);
		
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
		
		addActionListeners();
		add(mainPanel);
		setSize(1024,600);
 
	}

	//add action listeners to components
	private void addActionListeners(){
		//validate text fields and authenticate login
		userLoginButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String username = usernameTextField.getText();
				String password = new String(passwordTextField.getPassword());
				if(username.equals("") || password.equals("")){
					//report error
					System.out.println("Invalid username and password combination.");
				}
				else{
					System.out.println("username: " + username + ", password: " + password);
					authenticate(username, password);
				}
			}
		});
		
		//log in as guest
		guestLoginButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		//create new user
		newUserButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String username = usernameTextField.getText();
				String password = new String(passwordTextField.getPassword());
				if(username.equals("") || password.equals("")){
					//report error
					System.out.println("Invalid username and password combination.");
				}
				else{
					System.out.println("Creating new user: username: " + username + ", password: " + password);
					createUser(username, password);
				}
			}
		});
		
		//login as user if enter key is pressed
		usernameTextField.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                if(e.getKeyChar() == KeyEvent.VK_ENTER){
                    userLoginButton.doClick();
                }       
            }
        });
		
		//login as user if enter key is pressed
		passwordTextField.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                if(e.getKeyChar() == KeyEvent.VK_ENTER){
                    userLoginButton.doClick();
                }       
            }
        });
	}
	
	//verify username/password combo with server and allows or denies login based on server response
	public void authenticate(String username, String password){ //checks if username and password are in database, and logins in
		Vector<String> strings = new Vector<String>();
		strings.add(username);
		strings.add(password);
		String response = (String)Orbit.sendRequest(new ServerRequest("Authenticate Login", strings));
		//DEBUG System.out.println("Received response: " + response);
		if(response.equalsIgnoreCase("Valid")){
			//Login as user
			//start new server listener thread
//			slt = new ServerListenerThread();
//			slt.start();
			orbit.currentUser = (User)Orbit.sendRequest(new ServerRequest("Get User", null));
			orbit.openLobby();
			
			this.setVisible(false);
			System.out.println("Valid login");
			
			
		}
		else{
			//Deny login
			System.out.println("Invalid login");
		}
		
		
	}
	
	//signals server to create a new user. indicates whether new user was created
	public void createUser(String username, String password){ //adds username and password to databaseb
		Vector<String> strings = new Vector<String>();
		strings.add(username);
		strings.add(password);
		String response = (String)Orbit.sendRequest(new ServerRequest("Create New User", strings));
		//DEBUG System.out.println("Received response: " + response);
		if(response.equalsIgnoreCase("Valid")){
			//Signal that new user has been created
			System.out.println("New user created! username: " + username + ", password: " + password);
		}
		else{
			//Signal that new user has not been created
			System.out.println("Username taken. Try again.");
		}
	}

	//sends object to server and returns for response received from server


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
