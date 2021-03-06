
package orbit.desktop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.SwingConstants;

import orbit.ServerRequest;
import orbit.User;
import orbit.GameData;

public class LobbyWindow extends Window{
	private static final long serialVersionUID = 3030799455712427080L;
	
	Vector<User> currentUsers;	//list of users that still needs to be set users from Orbit object
	private JPanel mainContainer;			//container for management. names should specify use
	private JPanel buttonContainer;
	private JPanel userContainer;
	private JPanel chatContainer;
	private JPanel leftSideContainer;
	
	
	JButton findGameButton;				//private gui elements
	JButton profileButton;
	private JButton quitButton;
	private JButton sendMessageButton;
	private JTextField messageTextField;
	private JTextArea chatArea;
	private final ImageIcon backgroundImage = new ImageIcon("bin/SpaceBackground.jpg");
	Timer updateTimer;
	Timer checkForGame;
	public WaitingWindow ww = null;
	
	ChatClient chatClient;
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
	
	public void endMatchmaking(){
		Orbit.sendRequest(new ServerRequest("End Matchmaking", orbit.currentUser.getUsername()));
		enableButtons(true);
	}
	
	public void enableButtons(boolean bool){
		profileButton.setEnabled(bool);
		findGameButton.setEnabled(bool);
		quitButton.setEnabled(bool);
	}
	
	public void startUpdateThread(){
		updateTimer = new Timer("Ping for lobby updates");
		updateTimer.schedule(new TimerTask(){
			@SuppressWarnings("unchecked")
			public void run(){
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
		//System.out.println("Updating avis");
		userContainer.removeAll();
		aviPanels.removeAllElements();
		for(User u : currentUsers){
			JAviPanel temp = new JAviPanel(u);
			userContainer.add(temp);
			aviPanels.add(temp);
		}
		for(int i = 12 - currentUsers.size(); i >= 0 ;i--){
			userContainer.add(new JLabel());
		}
		userContainer.revalidate();
		userContainer.repaint();
	}
	
	//adds action listeners to components
	private void addActionListeners(){
		//opens profile window
		profileButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(ww != null){
					dispatchEvent(new WindowEvent(ww, WindowEvent.WINDOW_CLOSING));
				}
				updateTimer.cancel();
				Orbit.sendRequest(new ServerRequest("User to Profile Screen", null));
				if(orbit.profile == null){
					orbit.profile = new ProfileWindow(orbit);
				}
				orbit.currentUser = (User)Orbit.sendRequest(new ServerRequest("Get User", null));

				orbit.profile.setVisible(true);
				orbit.lobby.dispose();
			}
		});
		
		//start's matchmaking
		findGameButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				enableButtons(false);
				ww = new WaitingWindow(LobbyWindow.this);
				Orbit.sendRequest(new ServerRequest("Find Game", null));
				checkForGame = new Timer();
				checkForGame.schedule(new TimerTask(){
					public void run() {
						ww.time--;
						ww.waitMessage.setText("Waiting for another player...  " + ww.time + "s until timeout.");
						
						Object response = Orbit.sendRequest(new ServerRequest("Get Game Data", null));
						if(response != null){
							@SuppressWarnings("unchecked")
							GameData gameData = (GameData)response;
							checkForGame = null;
							ww.dispose();
							ww.gameStarted = true;
							//dispatchEvent(new WindowEvent(ww, WindowEvent.WINDOW_CLOSING));
							this.cancel();
							enableButtons(false);
							orbit.launchGame(gameData);
						}else{
							if(ww.time == 0){
								checkForGame = null;
								ww.dispose();
								this.cancel();
							}
						}
					}
					
				}, 0, 1000);
			}
		});
		
		//quits back to login window
		quitButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				//send server a quit signal
				Orbit.sendRequest(new ServerRequest("User Quit", orbit.currentUser));
				orbit.currentUser = null;
				orbit.login.setVisible(true);
				orbit.lobby.setVisible(false);
				chatClient.endThread();
				updateTimer.cancel();
				orbit.lobby.dispose();
			}
		});
	}
	
	private JPanel createButtonContainer(){
		
		JPanel tempPanel = new JPanel();			//creates bottom button panel
		
		findGameButton = new JOrbitButton("Find Game");
		profileButton = new JOrbitButton("Profile");
		quitButton = new JOrbitButton("Quit");
		
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
		
		
		sendMessageButton = new JOrbitButton("Send");			//Initialize and add glue
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
			
			buttonContainer = new JPanel();			//container for bottom button panel
			playGame = new JOrbitButton("Play");
			trade = new JOrbitButton("Trade");
			acceptButton = new JOrbitButton("Accept");
			declineButton = new JOrbitButton("Decline");
			//buttonContainer.add(playGame);
			//buttonContainer.add(trade);
			statusLabel = new JLabel();
			if(user != null){
				statusLabel.setText(user.getUsername());
			}
			Dimension d = new Dimension(170, 32);
			buttonContainer.setPreferredSize(d);
	
			aviImage = new ImageIcon("bin/" + user.planetPath);
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

	class WaitingWindow extends JFrame{

		private JLabel waitMessage = new JLabel("", SwingConstants.CENTER);
		LobbyWindow lw;
//		Timer countdown;
		int time;
		JButton cancelButton;
		boolean gameStarted = false;
		
		public WaitingWindow(LobbyWindow lw){
			super("Finding Game...");
			System.out.println("Starting waiting window");
			setLayout(new FlowLayout());
			setSize(500,100);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			
			this.lw = lw;

			addWindowListener(new WindowAdapter() {
			    public void windowClosing(WindowEvent e) {
			    	System.out.println("Closing waiting window");
			    	if(checkForGame != null){
			    		checkForGame.cancel();
			    		checkForGame = null;
			    	}
			    }
			    public void windowClosed(WindowEvent e){
			    	System.out.println("waitingwindow closed");
			    	WaitingWindow.this.lw.endMatchmaking();
			    	WaitingWindow.this.lw.ww = null;
			    	if(gameStarted){
			    		//WaitingWindow.this.lw.dispose();
			    	}
			    }
			});
			
			time = 30;
			waitMessage.setText("Waiting for another player...  " + time + "s until timeout.");
			
			cancelButton = new JOrbitButton("Cancel");
			cancelButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					System.out.println("Canceled Matchmaking");
					dispatchEvent(new WindowEvent(WaitingWindow.this, WindowEvent.WINDOW_CLOSING));
				}
			});
			
			add(waitMessage);
			add(cancelButton);
			setVisible(true);
		}
	}
}
