package com.orbit.game.desktop;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import orbit.ServerRequest;

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
	
	private static final String ipAddress = "localhost";
	private static final int portNumber = 6789;
	
	
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
		Vector<String> strings = new Vector<String>();
		strings.add(username);
		strings.add(password);
		String response = (String)sendRequest(new ServerRequest("Authenticate Login", strings));
		//DEBUG System.out.println("Received response: " + response);
		if(response.equalsIgnoreCase("Valid")){
			//Login as user
			//start new server listener thread
		}
		else{
			//Deny login
		}
	}
	
	public void createUser(String username, String password){ //adds username and password to databaseb
		Vector<String> strings = new Vector<String>();
		strings.add(username);
		strings.add(password);
		String response = (String)sendRequest(new ServerRequest("Create New User", strings));
		//DEBUG System.out.println("Received response: " + response);
		if(response.equalsIgnoreCase("Valid")){
			//Signal that new user has been created
		}
		else{
			//Signal that new user has not been created
		}
	}
	
	public void guestLogin(){ //logins without requiring a username or password
		//start new server listener thread
	}
	
	public static synchronized Object sendRequest(ServerRequest sr){
		Socket s = null;
		try {
			s = new Socket(ipAddress, portNumber);
		} catch (UnknownHostException e1) { e1.printStackTrace(); return null;
		} catch (IOException e1) { e1.printStackTrace(); return null;
		}
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		Object response = null;
		try {
			System.out.println("Sending ServerRequest...");
			oos = new ObjectOutputStream(s.getOutputStream());
			oos.writeObject(sr);
			oos.flush();
			System.out.println("ServerRequest sent. Waiting for response...");
			ois = new ObjectInputStream(s.getInputStream());
			response = ois.readObject();
			System.out.println("Got response. Returned.");
		} catch (IOException e) { e.printStackTrace();
		} catch (ClassNotFoundException e) { e.printStackTrace();
		} finally{
			try {
				if(oos != null){ oos.close(); }
				if(ois != null){ ois.close(); }
				if(s != null){
					s.close();
					s = null;
				}
			} catch (IOException e) { e.printStackTrace();
			}
		}
		return response;
	}
}
