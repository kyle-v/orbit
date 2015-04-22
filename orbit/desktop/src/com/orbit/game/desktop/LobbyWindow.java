
package com.orbit.game.desktop;

import orbit.ServerRequest;
import orbit.User;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagLayout;
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
	
	LobbyWindow(){
		super();
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
		
		addActionListeners();
		add(mainContainer);
		setSize(1024,600);
	}
	
	//adds action listeners to components
	private void addActionListeners(){
		//opens profile window
		profileButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
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
							Pair<ArrayList<User>, ArrayList<String>> opponents = (Pair<ArrayList<User>, ArrayList<String>>)response;

							System.out.println("Client has opponents!" + opponents.getValue().toString());
							this.cancel();
							
						}
					}
					
				}, 0, 100);
			}
		});
		
		//quits back to login window
		quitButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
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
		
		this.chatClient = new ChatClient(chatArea);
		
		sendMessageButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String message = messageTextField.getText();
				if(message.equals("")){
					return;
				}
				messageTextField.setText("");
				//send message to server
				chatClient.sendMessage(message);
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

		}
		
		protected void paintComponent(Graphics g){
			g.drawImage(backgroundImage.getImage() ,0,0,null );
		}
	}
}
