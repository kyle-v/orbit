package orbit;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class OrbitServerThread extends Thread {
	Server server = null;
	Socket s = null;
	ObjectInputStream ois = null;
	ObjectOutputStream oos = null;
	
	public OrbitServerThread(Server server, Socket s){
		System.out.println("New Orbit Client has connected to server: " + s.getInetAddress() + " : " + s.getPort());
		this.server = server;
		this.s = s;
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
}
