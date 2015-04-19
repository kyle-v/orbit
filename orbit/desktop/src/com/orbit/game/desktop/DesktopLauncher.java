package com.orbit.game.desktop;

import orbit.OrbitGame;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Interplanet Orbit";
		config.width = 1024;
		config.height = 600;

		new LoginWindow(); //both windows are shown for debugging
		new LobbyWindow(); 
		new LwjglApplication(new OrbitGame(), config);
	}
}
