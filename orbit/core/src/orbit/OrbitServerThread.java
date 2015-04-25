package orbit;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

import javafx.util.Pair;

//server-side thread reads and fulfills requests to be sent back to client
public class OrbitServerThread extends Thread {
	//member variables
	private Database d = null;
	private Server server = null;
	Socket s = null;
	private ObjectInputStream ois = null;
	private ObjectOutputStream oos = null;
	private User user = null;
	
	public Pair<ArrayList<User>, ArrayList<String>> opponents = null;
	public boolean inGame = false;
	
	public OrbitServerThread(Server server, Socket s, Database d){
		System.out.println("New Orbit Client has connected to server: " + s.getInetAddress() + " : " + s.getPort());
		this.server = server;
		this.s = s;
		this.d = d;
	}
	
	public void run(){
		System.out.println("Starting to run OrbitServerThread");
		try {
			//acquire streams
			ois = new ObjectInputStream(s.getInputStream());
			oos = new ObjectOutputStream(s.getOutputStream());
			
			//only ServerRequests are received (sent by client)
			while(true){
				ServerRequest sr = (ServerRequest)ois.readObject();
				fulfillRequest(sr);
			}
			
		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("IOE in OrbitServerThread.run(): " + e.getMessage());
		}catch (ClassNotFoundException e) {
			System.out.println("ClassNotFoundException in OrbitServerThread.run(): " + e.getMessage());
		} catch (UnidentifiedRequestException e) {
			System.out.println("UnidentifiedRequestException in OrbitServerThread.run(): " + e.getMessage());
		}
		finally{
			System.out.println("Client disconnected");
			if(user!= null)Server.activeUsers.remove(user);
			server.clients.remove(this);
			server.numConnections.setText(new Integer(server.clients.size()).toString());
			try {
				oos.close();
				ois.close();
				s.close();
			} catch (IOException e) {
				System.out.println("IOE in OrbitServerThread.run() - finally: " + e.getMessage());
			}
		}
	}

	//reads the request received and performs the appropriate action
	private  synchronized void fulfillRequest(ServerRequest sr) throws UnidentifiedRequestException{
		String request = sr.getRequest();
		Object o = sr.getObject();
		
		/* Compares request to strings in request bank (predetermined allowable requests)

		IMPORTANT
		 * Request strings must be unique
		 * Upon creating a new request string, create the corresponding method to be called
		 	* These methods (in this class) should cast the object to the appropriate type
		 	* Then, the information is extracted/parsed and sent to the database
		 	* Create matching methods in the database as needed (see "Authenticate Login")
		
		/* Request Bank
		 * Authenticate Login
		 * some other request
		 * etc.
		 */

		if(request.equalsIgnoreCase("Authenticate Login")){
			authenticateLogin(o);
		}
		else if(request.equals("Create New User")){
			createUser(o);
		}else if(request.equals("Get User")){
			sendResponse(user);
		}else if(request.equals("Get User List")){
			sendResponse(Server.activeUsers);
		}else if(request.equalsIgnoreCase("Find Game")){
			server.addToReady(this);
			sendResponse(true);
		}else if(request.equalsIgnoreCase("User Quit")){
			server.d.usernameToUserMap.replace(user.getUsername(), user);
			server.clients.remove(this);
			server.activeUsers.remove(this.user);
			server.usernameToThreadMap.remove(this.user.getUsername());
			server.readyClients.remove(this);
			sendResponse(true);
		}else if(request.equalsIgnoreCase("Get Opponents")){
			//opponents vector should be the same in all clients that are in the same game
			sendResponse(opponents);
		}else if(request.equals("Update User")){
			User u = (User)o;
			server.activeUsers.remove(this.user);
			server.activeUsers.add(u);
			this.user = u;
			server.d.usernameToUserMap.replace(user.getUsername(), user);
			sendResponse("Done");
		}else if(request.equals("User to Profile Screen")){
			server.readyClients.remove(server.usernameToThreadMap.get(user.getUsername()));
			sendResponse("Done");
		}else if(request.equalsIgnoreCase("End Matchmaking")){
			//opponents vector should be the same in all clients that are in the same game
			server.readyClients.remove(server.usernameToThreadMap.get(user.getUsername()));
			sendResponse("Done");
		}else{
			//request does not match an existing request. unable to fulfill request
			throw new UnidentifiedRequestException(request);
		}
	}
	
	public User getUser(){
		return user;
	}

	//send response to client (writes response object to oos)
	private void sendResponse(Object responseObject){
		try {
			oos.reset();
			oos.writeObject(responseObject);
			oos.flush();
		} catch (IOException e) {
			System.out.println("IOException in OrbitServerThread.sendResponse(): " + e.getMessage());
		}
	}


	
/*
 * request methods
 */
	
	
	//sends username and password to database for verification and sends appropriate response to client
	private void authenticateLogin(Object o){
		Vector<String> strings = (Vector<String>)o;
		String username = strings.get(0);
		String password = strings.get(1);
		String response = "";
		if(d == null){
			System.out.println("Database is null");
		}
		if(d.authenticateLogin(username, password)){
			response = "Valid";
			this.user = d.usernameToUserMap.get(username);
			Server.activeUsers.add(user);
		}
		else{
			response = "Invalid";
		}
		sendResponse(response);
	}
	
	//sends username and password to database to check if new user would collide with existing user
	private void createUser(Object o){
		Vector<String> strings = (Vector<String>)o;
		String username = strings.get(0);
		String password = strings.get(1);
		String response = "";
		if(d == null){
			System.out.println("Database is null");
		}
		if(d.createUser(username, password)){
			response = "Valid";
		}
		else{
			response = "Invalid";
		}
		sendResponse(response);
	}

}

//thrown when a request being made cannot be fulfilled by the server
//i.e. the request does not exists in the request bank
class UnidentifiedRequestException extends Exception{
	private static final long serialVersionUID = 1L;
	private String message;
	
	public UnidentifiedRequestException(String s){
		message = "could not identify request \"" + s + "\"";
	}
	
	public String getMessage(){
		return message;
	}
}