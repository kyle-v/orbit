package com.orbit.game.desktop;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ProfileWindow extends Window{

	private static final long serialVersionUID = 2895673463672850898L;
	private JPanel containerPanel;		//panel to hold button panel and current panel
	private JPanel buttonPanel;
	private JPanel currentPanel;
	private JPanel planetScreen;			//these panels will be the 3 various profile screens
	private JPanel inventoryScreen;
	private JPanel profileScreen;
	
	private JButton planetButton;
	private JButton inventoryButton;
	private JButton profileButton;
	private JButton backButton;
	private final ImageIcon backgroundImage = new ImageIcon("assets/StarBackground.jpg");
	
	ProfileWindow(Orbit orbit){
		
		super(orbit);
		
		containerPanel = new JPanel(new BorderLayout());		//Initializing
		currentPanel = new JProfilePanel();
		planetScreen = new JPanel();
		inventoryScreen = new JPanel();
		profileScreen = new JPanel();
		buttonPanel = new JPanel();
	
		
		planetButton = new JButton("Planet");
		inventoryButton = new JButton("Inventory");
		profileButton = new JButton("Profile");
		backButton = new JButton("Back to Lobby");
		
		
		buttonPanel.add(profileButton);			//add buttons to panel
		buttonPanel.add(inventoryButton);
		buttonPanel.add(planetButton);
		buttonPanel.add(Box.createHorizontalStrut(40));
		buttonPanel.add(backButton);
		
		containerPanel.add(currentPanel, BorderLayout.CENTER);		//add panels to main panel
		containerPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		add(containerPanel);
		
		setSize(1024,600);
	}
	
	private void addActionListeners(){
		profileButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				//TODO
			}
		});
		
		inventoryButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				//TODO
			}
		});
		
		planetButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				//TODO
			}
		});
	}
	
	class JProfilePanel extends JPanel{				//custom Lobby panel with overridden paint component

		
		private static final long serialVersionUID = 2L;
		
		JProfilePanel(){					
			
		}
		
		protected void paintComponent(Graphics g){
			g.drawImage(backgroundImage.getImage() ,0,0,null );
		}
	}

}
