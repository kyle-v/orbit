package orbit;

import java.io.Serializable;
import java.util.ArrayList;

public class GameData implements Serializable{

	private static final long serialVersionUID = 1L;
	public ArrayList<User> players;
	public ArrayList<String> ips;
	public int seed;
	public int baseport = 9020;	
	public GameData(int gameNum){
		players = new ArrayList<User> ();
		ips = new ArrayList<String> ();
		this.seed = (int) (System.currentTimeMillis()%1000000);
		this.baseport = baseport + gameNum;
	}
}
