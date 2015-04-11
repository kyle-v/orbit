package orbit;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

public class OrbitServerThread extends Thread {
	//member variables
	private Database d = null;
	private Server server = null;
	private Socket s = null;
	private ObjectInputStream ois = null;
	private ObjectOutputStream oos = null;
	
	public OrbitServerThread(Server server, Socket s, Database d){
		System.out.println("New Orbit Client has connected to server: " + s.getInetAddress() + " : " + s.getPort());
		this.server = server;
		this.s = s;
		this.d = d;
	}
	
	public void run(){
		System.out.println("Starting to run OrbitServerThread");
		try {
			ois = new ObjectInputStream(s.getInputStream());
			oos = new ObjectOutputStream(s.getOutputStream());
			
			//read object
			//<T> someObject = (<T>)ois.readObject();
			
			//write object
			/* parse request
			 * handle request and generate someResponseObject
			 * oos.writeObject(someResponseObject);
			 */
		} catch (IOException e) {
			System.out.println("IOE in OrbitServerThread.run(): " + e.getMessage());
		} 
		//uncomment while readObject()/writeObject() are called in above try block
		/*catch (ClassNotFoundException e) {
			System.out.println("ClassNotFoundException in OrbitServerThread.run(): " + e.getMessage());
		} 
		*/
		finally{
			try {
				oos.close();
				ois.close();
				s.close();
				//someSemaphore.release() if using semaphores
			} catch (IOException e) {
				System.out.println("IOE in OrbitServerThread.run() - finally: " + e.getMessage());
			}
		}
	}

	//reads the request received and performs the appropriate action
	private void fulfillRequest(ServerRequest sr) throws UnidentifiedRequestException{
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

		if(request.equals("Authenticate Login")){
			authenticateLogin(o);
		}
		else if(request.equals("some other request")){
			//someOtherRequest(o);
		}
		else{
			//request does not match an existing request. unable to fulfill request
			throw new UnidentifiedRequestException(request);
		}
	}

	//send response to client (writes response object to oos)
	private void sendResponse(Object o){
		try {
			oos.writeObject(o);
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
		//change implementation as needed
		Vector<String> strings = (Vector<String>)o;
		String username = strings.get(0);
		String password = strings.get(1);
		String response = "";
		if(d.authenticateLogin(username, password)){
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