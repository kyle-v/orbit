package com.orbit.game.desktop;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import orbit.ServerRequest;
import orbit.User;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Orbit {

	Window login = null; 
	Window lobby = null;
	Window profile = null;

	static String ipAddress = "localhost";
	private static final int portNumber = 6789;
	private static Socket s = null;
	private static ObjectOutputStream oos = null;
	private static ObjectInputStream ois = null;

	public User currentUser = null;


	public Orbit(){
		login = new LoginWindow(this); //uncomment to see the gui windows
		//new ProfileWindow();
		//lobby.setVisible(true);
		login.setVisible(true);
	}

	public void launchGame(){
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Interplanet Orbit";
		config.width = 1024;
		config.height = 600;

//		new LwjglApplication(new OrbitGame(users,0), config);

	}

	public static void main (String[] arg) {
		new Orbit();
		//initializeSocket();
	}

	public void openLobby(){
		//System.out.println("Opening lobby as " + currentUser.getUsername());
		lobby = new LobbyWindow(this); 

		login.setVisible(false);
		lobby.setVisible(true);
	}

	public void destruct(){
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

	public static boolean initializeSocket(){

		try {
			s = new Socket(ipAddress, portNumber);
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			return true;
		} catch (UnknownHostException e1) { e1.printStackTrace();
			System.out.println("UnknownHostException");
			return false;
		} catch (IOException e1) { //e1.printStackTrace();
			System.out.println("Could not connect to server.");
			JOptionPane.showMessageDialog(null,
					"ERROR: Could not connect to server.",
					"Connection Error",
					JOptionPane.ERROR_MESSAGE);
			//quit game
			return false;
		}
	}

	public static Object sendRequest(ServerRequest sr){
		Object response = null;
		try {
			//System.out.println("Sending ServerRequest...");
			oos.reset();
			oos.writeObject(sr);
			oos.flush();
			//System.out.println("ServerRequest sent. Waiting for response...");
			response = ois.readObject();
			//System.out.println("Got response. Returned.");
		} catch (IOException e) { e.printStackTrace();
		} catch (ClassNotFoundException e) { e.printStackTrace();
		}
		return response;
	}

}
