/*
 * Mazen Azar - mazar@usc.edu
 * Steven Lai - laisteve@usc.edu
 * Steven Li - listeven@usc.edu
 * Teddy Park - theodorp@usc.edu
 * Aditya Radhakrishna - radh439@usc.edu
 * Kyle Vaidyanathan - kvaidyan@usc.edu
 */


package orbit.desktop;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javafx.util.Pair;

import javax.swing.JOptionPane;

import orbit.GameData;
import orbit.OrbitGame;
import orbit.ServerRequest;
import orbit.User;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;


public class Orbit {

	Window login = null; 
	LobbyWindow lobby = null;
	Window profile = null;
	OrbitGame game = null;
	LwjglApplication app = null;
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

	public void launchGame(GameData gameData){
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Interplanet Orbit";
		config.width = 1000;
		config.height = 700;
		config.forceExit = false;

		int playerID = -1;
		for(int i = 0 ; i < gameData.players.size(); i++){
			if(gameData.players.get(i).getUsername().equals(currentUser.getUsername())) 
				playerID = i;
		}
		System.out.println("User " + currentUser.getUsername() + " " + playerID);
		if(playerID == -1) {
			System.out.println("User not found in list of players");
			return;
		}
		System.out.println("Launching game");
		game = new OrbitGame(gameData, playerID, oos, ois);
		app = new LwjglApplication(game, config);


		System.out.println("Game has been launched");
		Timer checkIfGameOver = new Timer("Check if game is over");
		checkIfGameOver.schedule(new TimerTask(){
			public void run(){
				if(game.isGameOver || game == null || app == null){
					lobby.enableButtons(true);
					System.out.println("Game has ended");
					this.cancel();
				}
			}
		}, 0l, 2000l);


	}

	public static void main (String[] arg) {
		new Orbit();
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
		synchronized(oos){

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
		}
		return response;
	}

}
