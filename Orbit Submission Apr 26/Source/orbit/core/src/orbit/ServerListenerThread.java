package orbit;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//client-side thread listens for to incoming requests
//starts a client-side thread (OrbitClientThread) to handle requests
public class ServerListenerThread extends Thread{
	public static final int portNumber = 6790;
	ServerSocket ss;
	Socket s;
	
	public ServerListenerThread(){
		try {
			ss = new ServerSocket(portNumber);
		} catch (IOException e) {e.printStackTrace();
		}
	}
	public void run(){
		try {
			while(true){
				//accept connections and update status labels
				s = ss.accept();
				System.out.println(s.getInetAddress() + " : " + s.getPort());
				
				//start new thread to handle client requests
				OrbitClientThread ost = new OrbitClientThread();
				ost.start();
			}
		} catch (IOException e) { e.printStackTrace();
		}
	}
}
