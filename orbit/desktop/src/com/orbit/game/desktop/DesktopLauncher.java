package com.orbit.game.desktop;

import java.util.ArrayList;

import orbit.OrbitGame;
import orbit.User;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Interplanet Orbit";
		config.width = 1000;
		config.height = 700;

	
		
		//LobbyWindow lw = new LobbyWindow(null);
		//lw.setVisible(true);
//		ProfileWindow pw = new ProfileWindow(null);
//		pw.setVisible(true);
<<<<<<< HEAD
		
=======
>>>>>>> 20856e67d40ff307fe33450cd038cb8f120a222c
		ArrayList<User> users = new ArrayList<User>();
		users.add(new User("Kyle","P"));
		users.add(new User("Steven", "LJK"));
		ArrayList<String> IPs = new ArrayList<String>();
		IPs.add("localhost");
		IPs.add("localhost");
<<<<<<< HEAD
		new LwjglApplication(new OrbitGame(users,IPs,0), config);
//		ArrayList<User> users = new ArrayList<User>();
//		users.add(new User("Kyle","P"));
//		users.add(new User("Steven", "LJK"));
//		ArrayList<String> IPs = new ArrayList<String>();
//		IPs.add("localhost");
//		IPs.add("localhost");
//		new LwjglApplication(new OrbitGame(users,IPs,1), config);
=======
		new LwjglApplication(new OrbitGame(users,IPs,1), config);
>>>>>>> 20856e67d40ff307fe33450cd038cb8f120a222c
	}
}
