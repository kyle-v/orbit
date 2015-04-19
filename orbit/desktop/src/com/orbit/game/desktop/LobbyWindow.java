
package com.orbit.game.desktop;

import orbit.User;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.util.ArrayList;

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
		
		add(mainContainer);
		setSize(1024,600);
		setVisible(true);
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
