package orbit;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Vector;

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
	
	//access to the database
	private Database d;
	
	//Connected client threads
	public Vector<OrbitServerThread> clients = new Vector<OrbitServerThread>();
	private HashMap<String, OrbitServerThread> usernameToThreadMap = new HashMap<String, OrbitServerThread>();
	
	
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
		} catch (FileNotFoundException e) {
			//could not find the file. initialize default database
			database = new Database();
			System.out.println("File not found. Creating default");
		} catch (IOException e) {
			System.out.println("IOException in Server.initializeDatabase(): " + e.getMessage());
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFoundException in Server.initializeDatabase(): " + e.getMessage());
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
	
	public static void main (String[] args){
		new Server();
	}
}

//thread that listens for new connections
class ClientListenerThread extends Thread{
	private Server server;
	private Database d;
	private ServerSocket ss;
	private Socket s;
	
	public ClientListenerThread(Server server, Database d, ServerSocket ss){
		this.server = server;
		this.d = d;
		this.ss = ss;
	}
	
	public void run(){
		try {
			while(true){
				//accept connections and update status labels
				server.connectionStatus.setText("Waiting for connection...");
				s = ss.accept();
				server.connectionStatus.setText("Got connection...");
				server.lastConnection.setText(s.getInetAddress() + " : " + s.getPort());
				
				//start new thread to handle client requests
				OrbitServerThread ost = new OrbitServerThread(server, s, d);
				server.clients.add(ost);
				server.numConnections.setText(new Integer(server.clients.size()).toString());
				ost.start();
			}
		} catch (IOException e) { //closing s to interrupt blocking ss.accept() call throws IOException. ignore exception.
			System.out.println("Terminated ClientListenerThread");
//			System.out.println("IOException in ClientListenerThread.run(): " + e.getMessage());
		}
	}
}
