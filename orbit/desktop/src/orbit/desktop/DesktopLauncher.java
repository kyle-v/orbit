package orbit.desktop;

import java.util.ArrayList;
import java.util.Random;

import orbit.GameData;
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

	
		
//		LobbyWindow lw = new LobbyWindow(null);
//		lw.setVisible(true);
//		Orbit o = new Orbit();
//		o.currentUser = new User("mazen", "azar");
//		ProfileWindow pw = new ProfileWindow(o);
//		pw.setVisible(true);
		GameData g = new GameData(1);
		ArrayList<User> users = new ArrayList<User>();
		users.add(new User("Kyle","P"));
		users.add(new User("Steven", "LJK"));
		g.players = users;
		ArrayList<String> IPs = new ArrayList<String>();
		IPs.add("localhost");
		IPs.add("localhost");
		g.ips = IPs;
		g.seed = 1;
		int playerID = 0;
		new LwjglApplication(new OrbitGame(g,playerID), config);
	}
}
