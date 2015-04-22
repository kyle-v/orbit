package orbit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ChatServer {
	
	private static final int chatSocket = 9000;
	private Vector<ChatThread> ctVector = new Vector<ChatThread>();
	public ChatServer() {
		ServerSocket ss = null;
		try {
			System.out.println("Starting Chat Server");
			ss = new ServerSocket(chatSocket);
			while (true) {
				System.out.println("Waiting for client to connect...");
				Socket s = ss.accept();
				System.out.println("Client " + s.getInetAddress() + ":" + s.getPort() + " connected");
				ChatThread ct = new ChatThread(s, this);
				ctVector.add(ct);
				ct.start();
			}
		} catch (IOException ioe) {
			System.out.println("IOE: " + ioe.getMessage());
		} finally {
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException ioe) {
					System.out.println("IOE closing ServerSocket: " + ioe.getMessage());
				}
			}
		}
	}
	public void removeChatThread(ChatThread ct) {
		ctVector.remove(ct);
	}
	public void sendMessageToClients(ChatThread ct, String str) {
		for (ChatThread ct1 : ctVector) {
			if (!ct.equals(ct1)) {
				ct1.sendMessage(str);
			}
		}
	}

	public static void main(String [] args) {
		new ChatServer();
	}
}

class ChatThread extends Thread {
	private BufferedReader br;
	private PrintWriter pw;
	private ChatServer cs;
	private Socket s;
	public ChatThread(Socket s, ChatServer cs) {
		this.cs = cs;
		this.s = s;
		try {
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			pw = new PrintWriter(s.getOutputStream());
		} catch (IOException ioe) {
			System.out.println("IOE in ChatThread constructor: " + ioe.getMessage());
		}
	}

	public void sendMessage(String str) {
		pw.println(str);
		pw.flush();
	}

	public void run() {
		try {
			while (true) {
				String line = br.readLine();
				cs.sendMessageToClients(this, line);
			}
		} catch (IOException ioe) {
			cs.removeChatThread(this);
			System.out.println(s.getInetAddress() + ":" + s.getPort() + " disconnected.");
		}
	}
}