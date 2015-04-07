package orbit;

import java.util.Vector;

public class User {
	
	//The amount of money a new user starts with
	public static final int STARTING_MONEY = 1000;
	
	//User account data
	private String username;
	private String password;
	private int money;
	
	//Weapon data
	Vector<Weapon> weapons;
	Vector<Weapon> equippedWeapons;
	
	//Planet
	//TO DO
	
	public User(String username, String password){
		this.username = username;
		this.password = password;
		int money = STARTING_MONEY;
		
		weapons = new Vector<Weapon>();
		equippedWeapons = new Vector<Weapon>();
		
	}
	
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getMoney() {
		return money;
	}

	public void addMoney(int amt) {
		this.money += amt;
	}
	
	public boolean withdrawMoney(int amt){
		if(amt > money){
			return false;
		}else{
			money -= amt;
			return true;
		}
	}

	
	
}
