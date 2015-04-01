package orbit;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;


public class Server {
	
	//Connected client threads
	Vector<OrbitServerThread> clients = new Vector<OrbitServerThread>();
	
	public Server(){
		
		//Accept connections and create new threads for each - Should probably change to EXECUTOR SERVICE
		ServerSocket ss = null;
		try{
			ss = new ServerSocket(6789);
			while(true){
				System.out.println("Waiting for connection...");
				Socket s = ss.accept();
				OrbitServerThread oc = new OrbitServerThread(this, s);
				clients.add(oc);
				oc.start();
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
		}
	}
	
	public static void main (String [] args){
		System.out.println("Launching server.");
		new Server();
	}
}
