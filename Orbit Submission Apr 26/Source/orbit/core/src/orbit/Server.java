/*
 * Mazen Azar - mazar@usc.edu
 * Steven Lai - laisteve@usc.edu
 * Steven Li - listeven@usc.edu
 * Teddy Park - theodorp@usc.edu
 * Aditya Radhakrishna - radh439@usc.edu
 * Kyle Vaidyanathan - kvaidyan@usc.edu
 */

package orbit;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import javafx.util.Pair;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;



public class Server extends JFrame{
	//member variables
	ServerSocket ss = null;
	private Socket s;
	public static final int portNumber = 6789;
	public static final int NUM_PLAYERS_PER_GAME = 2;
	int numGames = 0;
	public ChatServer chatServer;
	
	//access to the database
	Database d;
	
	//Connected client threads
	public Vector<OrbitServerThread> clients = new Vector<OrbitServerThread>();
	public static Vector<User> activeUsers = new Vector<User>();
	public HashMap<String, OrbitServerThread> usernameToThreadMap = new HashMap<String, OrbitServerThread>();
	public LinkedList<OrbitServerThread> readyClients = new LinkedList<OrbitServerThread>();

	
	//GUI members
	JLabel serverStatus;
	JLabel connectionStatus;
	JLabel lastConnection;
	JLabel numConnections;
	JPanel mainPanel;
	
	public Server(){
		System.out.println("Launching server.");
		
		//start database
		d = initializeDatabase("src/database.txt");
		
		//serialize database to file when server is closed (via window close event)
		addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		        System.out.println("Server Closed.");
		        destruct();
		    }
		});
		
		//set frame properties
		setSize(500,200);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//button to start server
		final JButton startServerButton = new JButton("Start Server");
		startServerButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				startServerButton.setEnabled(false);
				startServer();
			}
		});
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(startServerButton);
		add(buttonPanel, BorderLayout.SOUTH);
		
		
		//display useful information
		serverStatus = new JLabel("Server stopped");
		connectionStatus = new JLabel("Idle");
		lastConnection = new JLabel("none");
		numConnections = new JLabel("0");
		mainPanel = new JPanel(new GridLayout(4,2));
		mainPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		mainPanel.add(new JLabel("Server Status: ", SwingConstants.RIGHT));
		mainPanel.add(serverStatus);
		mainPanel.add(new JLabel("Connection Listener Status: ", SwingConstants.RIGHT));
		mainPanel.add(connectionStatus);
		mainPanel.add(new JLabel("Last Connected IP: ", SwingConstants.RIGHT));
		mainPanel.add(lastConnection);
		mainPanel.add(new JLabel("Number of Connections: ", SwingConstants.RIGHT));
		mainPanel.add(numConnections);
		add(mainPanel, BorderLayout.NORTH);
		

		setVisible(true);
	}
	
	//reads database from file or creates a default database if no file found
	private Database initializeDatabase(String filename){
		System.out.println("Initializing database...");
		Database database = new Database();
		
		try {
			//attempt to read database from file
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
			database = (Database)ois.readObject();
			System.out.println("Read database from file successfully");
			System.out.println("HASHMAP SIZE: " + database.usernameToUserMap.size());
//			if(usernameToUserMap == null){
//				usernameToUserMap = new 
//			}
			System.out.println("YELI: " + database.usernameToUserMap);//.get("yeli").getUsername());
		} catch (FileNotFoundException e) {
			//could not find the file. initialize default database
			System.out.println("File not found. Creating default");
			database = new Database();
			database.resetSQL();
		} catch (IOException e) {
			System.out.println("IOException in Server.initializeDatabase(): " + e.getMessage());
			database =  new Database();
			database.resetSQL();
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFoundException in Server.initializeDatabase(): " + e.getMessage());
			database =  new Database();
			database.resetSQL();
		} 
		
		System.out.println("Database initialized");
		return database;
	}
	
	//start thread that listens for client connections
	private void startServer(){
		serverStatus.setText("Server Started");
		try{
			ss = new ServerSocket(portNumber);
			ClientListenerThread cl = new ClientListenerThread(this, d, ss);
			cl.start();	//listens for client connections while user has not terminated
		}catch(IOException ioe){
			System.out.println("IOE Exception in Server constructor " + ioe.getMessage());
		}
		chatServer = new ChatServer();
		chatServer.start();
	}
	
	//serialize database to file and close streams and sockets
	private void destruct(){
		System.out.println("Stopping server...");
		if (ss != null){ //close server socket
			try {
				ss.close();
			} catch (IOException ioe) {
				System.out.println("IOException in terminating server (finally): " + ioe.getMessage());
			}
		}
		
		//serialize d (the database) to database.txt
		ObjectOutputStream oos = null;
		try {
			//serialize database to database.txt on server termination
			oos = new ObjectOutputStream(new FileOutputStream("src/database.txt"));
			oos.writeObject(d);
			oos.flush();
			System.out.println("Wrote to database.txt");
		} catch (FileNotFoundException fnfe) {
			System.out.println("FileNotFoundException in Server.destruct(): " + fnfe.getMessage());
			System.out.println("Could not write database to file.");
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.out.println("IOException in Server.destruct(): " + ioe.getMessage());
			System.out.println("Could not write database to file.");
		}finally{
			d.disconnectFromSQL();
			if(oos != null){
				try {
					oos.close();
				} catch (IOException ioe) {
					System.out.println("IOException in terminating server ( finally): " + ioe.getMessage());
				}
			}
		}
		System.out.println("Server stopped");
	}
	
	String getIP(){
		String ip = "";
		try {
			URL toCheckIp = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(toCheckIp.openStream()));
			ip = in.readLine();
			System.out.println(ip);
		} catch (MalformedURLException e) {
			System.out.println("MalformedURLException in NetworkWindow(): " + e.getMessage());
		} catch (IOException e) {
			ip = "Error";
		}
		return ip;
	}
	
	public synchronized void addToReady(OrbitServerThread ost){
		if(!readyClients.contains(ost)){
			readyClients.add(ost);
			if(readyClients.size() >= NUM_PLAYERS_PER_GAME){
				ArrayList<User> users = new ArrayList<User>();
				ArrayList<String> ips = new ArrayList<String>();
				GameData gameData = new GameData(numGames);
				numGames++;
				OrbitServerThread[] clients = new OrbitServerThread[NUM_PLAYERS_PER_GAME];
				for(int i = 0; i < NUM_PLAYERS_PER_GAME; i++){
					OrbitServerThread c = readyClients.pop();
					users.add(c.getUser());
					String ip = c.s.getInetAddress().toString().substring(1);
					if(ip.equals("127.0.0.1")) ip = getIP();
					ips.add(ip);//substring to remove leading '/'
					clients[i] = c;
				}
				gameData.players = users;
				gameData.ips = ips;
				
				for(OrbitServerThread player : clients){
					player.gameData = gameData;
				}
				
			}
		}
	}
	
	public static void main (String[] args){
		new Server();
	}
}
