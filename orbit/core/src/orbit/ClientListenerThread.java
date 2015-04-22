package orbit;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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