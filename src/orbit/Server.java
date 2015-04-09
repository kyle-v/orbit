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
import java.util.Vector;


public class Server {
	//access to the database
	private Database d;
	
	//Connected client threads
	private Vector<OrbitServerThread> clients = new Vector<OrbitServerThread>();
	
	private HashMap<String, OrbitServerThread> usernameToThreadMap = new HashMap<String, OrbitServerThread>();
	
	public Server(){
		//start database
		d = initializeDatabase("src/database.txt");
		
		//Accept connections and create new threads for each
		//Should probably change to EXECUTOR SERVICE
		ServerSocket ss = null;
		try{
			ss = new ServerSocket(6789);
			while(true){
				System.out.println("Waiting for connection...");
				Socket s = ss.accept();
				OrbitServerThread ost = new OrbitServerThread(this, s, d);
				clients.add(ost);
				ost.start();
			}
		}catch(IOException ioe){
			System.out.println("IOE Exception in Server constructor " + ioe.getMessage());
		}finally{
			if (ss != null){
				try {
					ss.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
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
				if(oos != null)
					try {
						oos.close();
					} catch (IOException e) {
						e.printStackTrace();
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
		System.out.println("Launching server.");
		new Server();
	}
}
