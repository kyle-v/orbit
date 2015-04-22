package orbit;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class OrbitClientThread extends Thread{
	private Socket s = null;
	private ObjectInputStream ois = null;
	private ObjectOutputStream oos = null;
	
	public OrbitClientThread(){
		
	}
	public void run(){
		System.out.println("Starting to run OrbitClientThread");
		try {
			//acquire streams
			ois = new ObjectInputStream(s.getInputStream());
			oos = new ObjectOutputStream(s.getOutputStream());
			
			//only ServerRequests are received (sent by client)
			ServerRequest sr = (ServerRequest)ois.readObject();
			fulfillRequest(sr);
			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IOE in OrbitClientThread.run(): " + e.getMessage());
		}catch (ClassNotFoundException e) {
			System.out.println("ClassNotFoundException in OrbitClientThread.run(): " + e.getMessage());
		} catch (UnidentifiedRequestException e) {
			System.out.println("UnidentifiedRequestException in OrbitClientThread.run(): " + e.getMessage());
		}
		finally{
			try {
				oos.close();
				ois.close();
				s.close();
			} catch (IOException e) {
				System.out.println("IOE in OrbitClientThread.run() - finally: " + e.getMessage());
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

		if(request.equalsIgnoreCase("Authenticate Login")){
			//authenticateLogin(o);
		}
		else if(request.equals("Create New User")){
			//createUser(o);
		}
		else{
			//request does not match an existing request. unable to fulfill request
			throw new UnidentifiedRequestException(request);
		}
	}
	
	//send response to client (writes response object to oos)
	private void sendResponse(Object responseObject){
		try {
			oos.writeObject(responseObject);
			oos.flush();
		} catch (IOException e) {
			System.out.println("IOException in OrbitClientThread.sendResponse(): " + e.getMessage());
		}
	}
}
