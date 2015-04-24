
package com.orbit.game.desktop;

import orbit.ServerRequest;
import orbit.User;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javafx.util.Pair;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class LobbyWindow extends Window{
	private static final long serialVersionUID = 3030799455712427080L;
	
	private ArrayList<User> currentUsers;	//list of users that still needs to be set users from Orbit object
	private JPanel mainContainer;			//container for management. names should specify use
	private JPanel buttonContainer;
	private JPanel userContainer;
	private JPanel chatContainer;
	private JPanel leftSideContainer;
	
	
	private JButton findGameButton;				//private gui elements
	private JButton profileButton;
	private JButton quitButton;
	private JButton sendMessageButton;
	private JTextField messageTextField;
	private JTextArea chatArea;
	private final ImageIcon backgroundImage = new ImageIcon("assets/SpaceBackground.jpg");
	
	private ChatClient chatClient;
	
	LobbyWindow(Orbit orbit){
		super(orbit);
		currentUsers = new ArrayList<User>();
		mainContainer = new JPanel(new BorderLayout());
		buttonContainer = createButtonContainer();
		userContainer = new JLobbyPanel();
		chatContainer = createChatContainer();
		leftSideContainer = new JPanel(new BorderLayout());

		
		leftSideContainer.add(userContainer, BorderLayout.CENTER);		//left side container holds users and buttons
		leftSideContainer.add(buttonContainer, BorderLayout.SOUTH);
		
		mainContainer.add(leftSideContainer, BorderLayout.CENTER);		//main container adds chat to east
		mainContainer.add(chatContainer, BorderLayout.EAST);
		
		
		startUpdateThread();
		
		addActionListeners();
		add(mainContainer);
		setSize(1024,600);
	}
	
	
	private void startUpdateThread(){
		Timer updateTimer = new Timer("Ping for lobby updates");
		updateTimer.schedule(new TimerTask(){
			public void run(){
				Object response = Orbit.sendRequest(new ServerRequest("Get User List", null));
				if(response != null){
					@SuppressWarnings("unchecked")
					ArrayList<User> users = (ArrayList<User>) response;
					for(User u: users){
						System.out.println(u.getUsername());
					}
					
				}
			}
		}, 0, 3000);
		
		
	}
	
	//adds action listeners to components
	private void addActionListeners(){
		//opens profile window
		profileButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(orbit.profile == null){
					orbit.profile = new ProfileWindow(orbit);
					orbit.lobby.setVisible(false);
					orbit.profile.setVisible(true);
				}
			}
		});
		
		//start's matchmaking
		findGameButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Orbit.sendRequest(new ServerRequest("Find Game", null));
				Timer checkForGame = new Timer();
				checkForGame.schedule(new TimerTask(){
					public void run() {
						Object response = Orbit.sendRequest(new ServerRequest("Get Opponents", null));
						if(response != null){
							@SuppressWarnings("unchecked")
							Pair<ArrayList<User>, ArrayList<String>> opponents = (Pair<ArrayList<User>, ArrayList<String>>)response;

							System.out.println("Client has opponents!" + opponents.getValue().toString());
							this.cancel();
							
						}
					}
					
				}, 0, 1000);
			}
		});
		
		//quits back to login window
		quitButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				orbit.currentUser = null;
				orbit.login.setVisible(true);
				orbit.lobby.setVisible(false);
				orbit.lobby.dispose();
			}
		});
	}
	
	private JPanel createButtonContainer(){
		
		JPanel tempPanel = new JPanel();			//creates bottom button panel
		
		findGameButton = new JButton("Find Game");
		profileButton = new JButton("Profile");
		quitButton = new JButton("Quit");
		
		tempPanel.add(profileButton);
		tempPanel.add(findGameButton);
		tempPanel.add(quitButton);
		return tempPanel;
	}

	private JPanel createChatContainer(){
		
		JPanel tempPanel = new JPanel(new BorderLayout());
		
		JPanel innerButtonContainer = new JPanel();		//holds text field and send button
		BoxLayout innerButtonContainerLayout = new BoxLayout(innerButtonContainer, BoxLayout.X_AXIS);
		innerButtonContainer.setLayout(innerButtonContainerLayout);
		
		
		sendMessageButton = new JButton("Send");			//Initialize and add glue
		messageTextField = new JTextField(17);
		innerButtonContainer.add(messageTextField);
		innerButtonContainer.add(Box.createHorizontalGlue());
		innerButtonContainer.add(sendMessageButton);
		
		
		chatArea = new JTextArea();						//add chat area
		chatArea.setEditable(false);
		
		tempPanel.add(chatArea, BorderLayout.CENTER);
		tempPanel.add(innerButtonContainer, BorderLayout.SOUTH);
		
		System.out.println("Starting chat client");
		chatClient = new ChatClient(chatArea);
		chatClient.start();
		sendMessageButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String message = messageTextField.getText();
				if(message.equals("")){
					return;
				}
				messageTextField.setText("");
				//send message to server
				chatClient.sendMessage(orbit.currentUser.getUsername() + ": " + message);
			}
		});
		
		//send message to server if enter is pressed
		messageTextField.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                if(e.getKeyChar() == KeyEvent.VK_ENTER){
                	sendMessageButton.doClick();
                }       
            }
        });
		
		
		return tempPanel;
	}
	
	class JLobbyPanel extends JPanel{				//custom Lobby panel with overridden paint component

		
		private static final long serialVersionUID = 2L;
		
		JLobbyPanel(){					//constructor needs to populate users from array list
			this.setLayout(new GridLayout(4,3,20,20)); 

			for(int i = 0; i < 6; i++){						//temporary user panels. need to be pulled from user array
				this.add(new JAviPanel());
			}
			for(int i = 0; i < 8; i++){					//must fill with empty labels
				this.add(new JLabel());
			}
			
		}
		
		protected void paintComponent(Graphics g){
			g.drawImage(backgroundImage.getImage() ,0,0,null );
		}
	}
	
	class JAviPanel extends JPanel{

		private static final long serialVersionUID = 3L;
		
		JButton playGame;
		JButton trade;
		JPanel buttonContainer;
		JPanel avi;
		JLabel statusLabel;
		User user;
		
		JButton acceptButton, declineButton;
		
		
		
		JAviPanel(){
			this.setLayout(new BorderLayout());
			
			buttonContainer = new JPanel();			//container for bottom button panel
			playGame = new JButton("Play");
			trade = new JButton("Trade");
			acceptButton = new JButton("Accept");
			declineButton = new JButton("Decline");
			buttonContainer.add(playGame);
			buttonContainer.add(trade);
			statusLabel = new JLabel();
			if(user != null){
				statusLabel.setText(user.getUsername());
			}
			Dimension d = new Dimension(170, 32);
			buttonContainer.setPreferredSize(d);
	
			avi = new JPanel();							//panel to hold image avi
			avi.setBackground(Color.BLUE);
			d = new Dimension(170, 140);
			avi.setPreferredSize(d);
			
			this.add(avi, BorderLayout.CENTER);				//creates panel
			this.add(buttonContainer, BorderLayout.SOUTH);
			this.add(statusLabel, BorderLayout.NORTH);
			d = new Dimension(170, 172);
			this.setPreferredSize(d);
			addActionListeners();
			
		}
		
		public void newChallenge(User challenger){
			statusLabel.setText("Challenge from " + challenger.getUsername() + "!");
			buttonContainer.removeAll();
			buttonContainer.add(acceptButton);
			buttonContainer.add(declineButton);
			while(acceptButton.getActionListeners().length > 0) acceptButton.removeActionListener(acceptButton.getActionListeners()[0]);
			while(declineButton.getActionListeners().length > 0) declineButton.removeActionListener(declineButton.getActionListeners()[0]);
			
			acceptButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					buttonContainer.removeAll();
					buttonContainer.add(playGame);
					buttonContainer.add(trade);
				}
			});
			
			declineButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					buttonContainer.removeAll();
					buttonContainer.add(playGame);
					buttonContainer.add(trade);
				}
			});
		}
		
		
		
		private void addActionListeners(){
			playGame.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					statusLabel.setText("Waiting for " + user.getUsername());
					playGame.setEnabled(false);
				}
			});
			
			trade.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					statusLabel.setText("Waiting for " + user.getUsername());
					trade.setEnabled(true);

				}
			});
			
		}
		
		
	}
}
