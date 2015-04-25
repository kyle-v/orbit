
package com.orbit.game.desktop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javafx.util.Pair;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import orbit.ServerRequest;
import orbit.User;

public class LobbyWindow extends Window{
	private static final long serialVersionUID = 3030799455712427080L;
	
	Vector<User> currentUsers;	//list of users that still needs to be set users from Orbit object
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
	private Vector<JAviPanel> aviPanels = new Vector<JAviPanel>();
	
	LobbyWindow(Orbit orbit){
		super(orbit);
		currentUsers = new Vector<User>();
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
			@SuppressWarnings("unchecked")
			public void run(){
				//System.out.println((Vector<User>)Orbit.sendRequest(new ServerRequest("Ping Count", null)));
				currentUsers = (Vector<User>)Orbit.sendRequest(new ServerRequest("Get User List", null));
				if(currentUsers != null){
					updateLobbyAvis();
				}
				for(JAviPanel jap : aviPanels){
					jap.revalidate();
					jap.repaint();
				}
			}
		}, 0, 3000);	
	}
	
	private void updateLobbyAvis(){
		System.out.println("Updating avis");
//		for(JAviPanel jap : aviPanels){
			userContainer.removeAll();
//		}
		aviPanels.removeAllElements();
		for(User u : currentUsers){
			JAviPanel temp = new JAviPanel(u);
			userContainer.add(temp);
			aviPanels.add(temp);
		}
		userContainer.revalidate();
		userContainer.repaint();
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
				//send server a quit signal
				Orbit.sendRequest(new ServerRequest("User Quit", orbit.currentUser.getUsername()));
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

//			for(int i = 0; i < 6; i++){						//temporary user panels. need to be pulled from user array
//				this.add(new JAviPanel());
//			}
//			for(int i = 0; i < 8; i++){					//must fill with empty labels
//				this.add(new JLabel());
//			}
			
		}
		
		protected void paintComponent(Graphics g){
			g.drawImage(backgroundImage.getImage() ,0,0,null );
		}
	}
	
	class JAviPanel extends JPanel{

		private static final long serialVersionUID = 3L;
		ImageIcon aviImage;
		JButton playGame;
		JButton trade;
		JPanel buttonContainer;
		JLabel statusLabel;
		User user;
		
		JButton acceptButton, declineButton;
		
		
		
		JAviPanel(User user){
			this.setLayout(new BorderLayout());
			this.user = user;
//			System.out.println(user.getPlanet().toString());
			
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
	
			aviImage = new ImageIcon("assets/worker.png");
			System.out.println(aviImage.toString());
			JLabel label = new JLabel();
			label.setIcon(aviImage);
			d = new Dimension(170, 140);
			label.setPreferredSize(d);//panel to hold image avi
			
			this.add(label, BorderLayout.CENTER);				//creates panel
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
					if(user!=null)statusLabel.setText("Waiting for " + user.getUsername());
					playGame.setEnabled(false);
				}
			});
			
			trade.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if(user!=null)statusLabel.setText("Waiting for " + user.getUsername());
					trade.setEnabled(true);
				}
			});
		}
	}
}
