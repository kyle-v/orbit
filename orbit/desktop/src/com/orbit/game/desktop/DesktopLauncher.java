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
		config.width = 1024;
		config.height = 600;

		//new LoginWindow(new Orbit()); //uncomment to see the gui windows
		//new LobbyWindow(); 
		//new ProfileWindow();
		ArrayList<User> users = new ArrayList<User>();
		users.add(new User("Kyle","P"));
		users.add(new User("Steven", "LJK"));
		ArrayList<String> IPs = new ArrayList<String>();
		IPs.add("localhost");
		IPs.add("localhost");
		new LwjglApplication(new OrbitGame(users,IPs,0), config);
	}
}
