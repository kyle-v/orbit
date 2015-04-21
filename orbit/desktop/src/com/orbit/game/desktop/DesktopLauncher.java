<<<<<<< HEAD
package com.orbit.game.desktop;

import orbit.Orbit;
import orbit.OrbitGame;

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
		new LwjglApplication(new OrbitGame(), config);
	}
}
=======
package com.orbit.game.desktop;

import orbit.Orbit;
import orbit.OrbitGame;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Interplanet Orbit";
		config.width = 1024;
		config.height = 600;

		new LoginWindow(new Orbit()); //uncomment to see the gui windows
		//new LobbyWindow(); 
		//new ProfileWindow();
		//new LwjglApplication(new OrbitGame(), config);
	}
}
>>>>>>> branch 'master' of https://github.com/kyle-v/orbit.git
