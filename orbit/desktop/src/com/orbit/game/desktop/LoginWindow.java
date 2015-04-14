package com.orbit.game.desktop;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import orbit.Orbit;

public class LoginWindow extends Window{

	private static final long serialVersionUID = 6765474456932332037L;
	private JTextField usernameTextField;
	private JTextField passwordTextField;
	private JButton userLoginButton;
	private JButton guestLoginButton;
	private JButton newUserButton;
	private JLabel backgroundLabel;
	private JLabel titleLabel;
	private JPanel mainPanel;
	
	
	//need to pass in an orbit ref. will temporarily use blank constructor
	LoginWindow(){
		super();
		//initialize all the shit
		mainPanel = new JPanel();
		mainPanel.setSize(1024, 600);
		mainPanel.setBackground(Color.BLACK);
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
	

}
