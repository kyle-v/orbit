package com.orbit.game.desktop;

import java.awt.BorderLayout;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
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
import javax.swing.SwingConstants;

import orbit.ServerListenerThread;
import orbit.ServerRequest;
import orbit.User;

public class LoginWindow extends Window{
	ServerListenerThread  slt;
	
	private static final long serialVersionUID = 6765474456932332037L;
	private JTextField usernameTextField;
	private JPasswordField passwordTextField;
	private JOrbitButton userLoginButton;
	private JOrbitButton guestLoginButton;
	private JOrbitButton newUserButton;
	private JLabel titleLabel; 
	

	private JOrbitPanel mainPanel;
	private final ImageIcon backgroundImage = new ImageIcon("bin/LoginWallpaper.jpg");
	private final ImageIcon logo = new ImageIcon("bin/logo.png");
	private final JLabel usernameLabel = new JLabel("Username: ");
	private final JLabel passwordLabel = new JLabel("Password: ");
	JLabel ipLabel = new JLabel("", SwingConstants.CENTER);
	JLabel connectLabel = new JLabel("Not Connected", SwingConstants.CENTER);
	private final JLabel ipFieldLabel = new JLabel("IP to connect to: ");
	boolean connected = false;
	
	JPanel networkingPanel;
	JTextField ipField;
	JButton connectButton, refreshButton;
	
	//need to pass in an orbit ref. will temporarily use blank constructor
	LoginWindow(Orbit orbit){
		super(orbit);
		
		//initialize all the shit
		//titleLabel = new JLabel(logo);
		usernameTextField = new JTextField(25);
		passwordTextField = new JPasswordField(25);
		userLoginButton = new JOrbitButton("Login");
		guestLoginButton = new JOrbitButton("Play as Guest");
		newUserButton = new JOrbitButton("Create User");
		mainPanel = new JOrbitPanel();	
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));	//a container for all JComps
		mainPanel.setSize(1024, 600);

		//titleLabel.setSize(321, 315);
		//titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		//titleLabel.setFont(new Font("Helvetica", Font.BOLD, 30));
		//container.add(titleLabel);			//adds logo
		
		
		GridBagConstraints textFieldConstraints = new GridBagConstraints();		//constraints for inner panels
		textFieldConstraints.gridx = GridBagConstraints.RELATIVE;
		textFieldConstraints.gridy = 1;
		
		JPanel usernamePanel = new JPanel(new GridBagLayout());			//username info
		usernamePanel.add(usernameLabel, textFieldConstraints);
		usernamePanel.add(usernameTextField, textFieldConstraints);
		
		JPanel passwordPanel = new JPanel(new GridBagLayout());			//password info
		passwordPanel.add(passwordLabel, textFieldConstraints);
		passwordPanel.add(passwordTextField, textFieldConstraints);
		
		Color c = new Color(187, 127, 222);
		
		usernamePanel.setBackground(c);
		passwordPanel.setBackground(c);
		container.add(usernamePanel);
		container.add(passwordPanel);
		
		
		JPanel buttonContainer = new JPanel();						//panel for buttons
		buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.X_AXIS));
		buttonContainer.add(userLoginButton);
		buttonContainer.add(Box.createHorizontalGlue());
		buttonContainer.add(guestLoginButton);
		buttonContainer.add(Box.createHorizontalGlue());
		buttonContainer.add(newUserButton);

		buttonContainer.setBackground(c);
		container.add(buttonContainer);					//adds buttons, then adds panel to frame
		container.setBackground(c);
		container.setSize(600,600);
		
//		GridBagConstraints centerConstraints = new GridBagConstraints();
//		centerConstraints.gridx = 1;
//		centerConstraints.gridy = GridBagConstraints.RELATIVE;
//		centerConstraints.gridy = GridBagConstraints.SOUTH;
//		mainPanel.add(container, centerConstraints);
		JPanel southContainer = new JPanel();
		southContainer.add(container);
		southContainer.setOpaque(false);
		mainPanel.add(southContainer, BorderLayout.SOUTH);
		
		networkingPanel = new JPanel();
		ipField = new JTextField("localhost");
		connectButton = new JButton("Connect");
		refreshButton = new JButton("Refresh");
		
		JPanel ipFieldPanel = new JPanel();
		ipFieldPanel.setLayout(new BorderLayout());
		ipFieldPanel.add(ipFieldLabel, BorderLayout.WEST);
		ipFieldPanel.add(ipField, BorderLayout.CENTER);
		ipFieldPanel.setBackground(c);
		
		JPanel ipPanel = new JPanel();
		ipPanel.setLayout(new BorderLayout());
		ipLabel.setText("Local IP: ");
		ipPanel.add(ipLabel, BorderLayout.NORTH);
		ipPanel.setBackground(c);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(connectLabel);
		buttonPanel.add(connectButton);
		buttonPanel.add(refreshButton);
		buttonPanel.setBackground(c);
		
		networkingPanel.setLayout(new BorderLayout());
		networkingPanel.add(ipPanel, BorderLayout.NORTH);
		networkingPanel.add(ipFieldPanel, BorderLayout.CENTER);
		networkingPanel.add(buttonPanel, BorderLayout.SOUTH);
		networkingPanel.setBackground(c);
		container.add(networkingPanel);
		
		addActionListeners();
		add(mainPanel);
		setSize(1024,600);
		refreshButton.doClick();
	}

	//add action listeners to components
	private void addActionListeners(){
		//validate text fields and authenticate login
		userLoginButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(connected){
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
			}
		});
		
		//log in as guest
		guestLoginButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(connected){
					
				}
			}
		});
		
		//create new user
		newUserButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(connected){
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
			}
		});
		
		//connect to ip address
		connectButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String ip = ipField.getText();
				if(ip.equals("")){
					//report error
					System.out.println("Invalid ip");
				}
				else{
					System.out.println("Connecting to IP: " + ip);
					connected = true;
					orbit.ipAddress = ip;
					if(Orbit.initializeSocket()){
						connectButton.setEnabled(false);
						refreshButton.setEnabled(false);
						connectLabel.setText("Connected to ip: " + ip);
					}
				}
			}
		});
		
		//fetch ip address
		refreshButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				ipLabel.setText("Local IP: " + getIP());
			}
		});
		
		//login as user if enter key is pressed
		usernameTextField.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                if(connected){
                	if(e.getKeyChar() == KeyEvent.VK_ENTER){
                        userLoginButton.doClick();
                    }  
                }
            }
        });
		
		//login as user if enter key is pressed
		passwordTextField.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                if(connected){
                	if(e.getKeyChar() == KeyEvent.VK_ENTER){
                        userLoginButton.doClick();
                    }       
                }
            }
        });
		
		//login as user if enter key is pressed
		ipField.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                if(!connected){
                	if(e.getKeyChar() == KeyEvent.VK_ENTER){
                        connectButton.doClick();
                    }
                }
            }
        });
	}
	
	String getIP(){
		String ip = "";
		try {
			URL toCheckIp = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(toCheckIp.openStream()));
			ip = in.readLine();
			System.out.println(ip);
		} catch (MalformedURLException e) {
			System.out.println("MalformedURLException in NetworkWindow(): " + e.getMessage());
		} catch (IOException e) {
			ip = "Error";
		}
		return ip;
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
			//this.setLayout(new GridBagLayout());
			this.setLayout(new BorderLayout());

		}
		
		protected void paintComponent(Graphics g){
			g.drawImage(backgroundImage.getImage() ,0,0,null );
			//hard coded logo for right now
			g.drawImage(logo.getImage(), 350,50,null);
		}
	}
}
