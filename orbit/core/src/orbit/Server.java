package orbit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;


public class Server {
	//member variables
	public static boolean terminated = false;
	
	//access to the database
	private Database d;
	
	//Connected client threads
	public Vector<OrbitServerThread> clients = new Vector<OrbitServerThread>();
	private HashMap<String, OrbitServerThread> usernameToThreadMap = new HashMap<String, OrbitServerThread>();
	
	public Server(){
		System.out.println("Launching server.");
		
		//start database
		d = initializeDatabase("src/database.txt");
		
		//Accept connections and create new threads for each
		//Should probably change to EXECUTOR SERVICE
		ServerSocket ss = null;
		
		try{
			ss = new ServerSocket(6789);
			QuitPromptThread qp = new QuitPromptThread();
			ClientListenerThread cl = new ClientListenerThread(this, d, ss);
			qp.start();	//prompts user to enter q to quit
			cl.start();	//listens for client connections while user has not terminated
			
			while(!terminated){ //wait for termination signal
				if(terminated){
					System.out.println("Terminate signal received. Quitting.");
				}
			}
			
			//server terminated, kill ClientListenerThread by closing the socket
			try{
				cl.s.close();
			}catch(NullPointerException e){ //no connections received yet. ignore exception.
				System.out.println("Socket closed.");
			}
		}catch(IOException ioe){
			System.out.println("IOE Exception in Server constructor " + ioe.getMessage());
		}finally{
			//close server socket
			if (ss != null){
				try {
					ss.close();
				} catch (IOException e) {
					System.out.println("IOException in terminating server (finally): " + e.getMessage());
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
			} catch (FileNotFoundException e) {
				System.out.println("FileNotFoundException in terminating server (finally): " + e.getMessage());
				System.out.println("Could not write database to file.");
			} catch (IOException e) {
				System.out.println("IOException in terminating server (finally): " + e.getMessage());
				System.out.println("Could not write database to file.");
			}finally{
				if(oos != null){
					try {
						oos.close();
					} catch (IOException e) {
						System.out.println("IOException in terminating server (finally finally): " + e.getMessage());
					}
				}
			}
		}
	}
	
	//reads database from file or creates a default database if no file found
	private Database initializeDatabase(String filename){
		System.out.println("Initializing database...");
		Database database = null;
		
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
	
	public static void main (String [] args){
		new Server();
	}
}

//listens for new connections
class ClientListenerThread extends Thread{
	private Server server;
	private Database d;
	private ServerSocket ss;
	public Socket s;
	public ClientListenerThread(Server server, Database d, ServerSocket ss){
		this.server = server;
		this.d = d;
		this.ss = ss;
	}
	public void run(){
		try {
			while(true){
				System.out.println("Waiting for connection...");
				s = ss.accept();
				System.out.println("Got connection...");
				OrbitServerThread ost = new OrbitServerThread(server, s, d);
				server.clients.add(ost);
				ost.start();
			}
		} catch (IOException e) { //closing s to interrupt blocking ss.accept() call throws IOException. ignore exception.
			System.out.println("Terminated ClientListenerThread");
//			System.out.println("IOException in ClientListenerThread.run(): " + e.getMessage());
		}
	}
}

//prompts input to quit server. asks user to enter "q" twice to quit
class QuitPromptThread extends Thread{
	public void run(){
		boolean quit = false;
		Scanner in = new Scanner(System.in);
		while(!quit){
			System.out.println("Enter \"q\" to quit.");
			String input = in.nextLine();
			if(input.equalsIgnoreCase("q")){
				System.out.println("Are you sure you want to quit?\nEnter \"q\" again to quit.");
				input = in.nextLine();
				if(input.equalsIgnoreCase("q")){
					quit = true;
				}
			}
		}
		
		//signal server to terminate
		Server.terminated = true;
	}
}
