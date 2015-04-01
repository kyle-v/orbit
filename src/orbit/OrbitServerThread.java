package orbit;

import java.net.Socket;

public class OrbitServerThread extends Thread {
	
	public OrbitServerThread(Server server, Socket socket){
		System.out.println("New Orbit Client has connected to server: " + socket.getInetAddress() + " : " + socket.getPort());
	}
	
	public void run(){
		System.out.println("Starting to run OrbitServerThread");
	}
}
