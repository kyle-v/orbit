package com.orbit.game.desktop;

import java.util.ArrayList;

import orbit.Orbit;
import orbit.OrbitGame;
import orbit.User;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Interplanet Orbit";
		config.width = 1920;
		config.height = 1080;

	
		
//		LobbyWindow lw = new LobbyWindow(null);
//		lw.setVisible(true);
//		ProfileWindow pw = new ProfileWindow(null);
//		pw.setVisible(true);
		
		ArrayList<User> users = new ArrayList<User>();
		users.add(new User("Kyle","P"));
		users.add(new User("Steven", "LJK"));
		ArrayList<String> IPs = new ArrayList<String>();
		IPs.add("localhost");
		IPs.add("localhost");
		new LwjglApplication(new OrbitGame(users,IPs,0), config);
	}
}
