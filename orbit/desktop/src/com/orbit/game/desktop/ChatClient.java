package com.orbit.game.desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JTextArea;


public class ChatClient extends Thread{

	private PrintWriter pw;
	private BufferedReader br;
	private Socket s;
	private JTextArea textArea;
	static final String ipAddress = "localhost";
	static final int portNumber = 9000;

	public ChatClient(JTextArea jta){
		textArea = jta;
		try{
			s = new Socket(ipAddress, portNumber);
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			pw = new PrintWriter(s.getOutputStream());
		}catch(IOException ioe){
			System.out.println("IOE Exception in ChatClient main" + ioe.getMessage());
		}
//		finally{
//			try{
//				if(pw != null) pw.close();
//				if(br != null) br.close();
//				if(s != null) s.close();
//			}
//			catch(IOException ioe){
//				System.out.println("IOE exception in chat client finally block " + ioe.getMessage());
//			}
//		}
	}

	public void sendMessage(String str){
		System.out.println("sending message to server");
		pw.println(str);
		pw.flush();
	}

	public void run() {
		String line;
		try {
			line = br.readLine();
			if(line == null){
				System.out.println("line is null");
			}

			while(line != null){
				System.out.println("From Server: " + line);
				textArea.append(line);
				line = br.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
