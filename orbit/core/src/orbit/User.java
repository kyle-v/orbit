package orbit;

import java.awt.Image;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class User implements Serializable{

	private static final long serialVersionUID = 1L;

	//The amount of money a new user starts with
	public static final int STARTING_MONEY = 1000;

	private static final int MIN_PASS_LENGTH = 5;
	
	//User account data
	private String username;
	private String encryptedPass = null; //password stored and received by server in encrypted format.
	private int money;
	
	//Weapon data
	Vector<Weapon> weapons;
	Vector<Weapon> equippedWeapons;
	
	//Planet
	private Planet planet;
	
	public User(String username, String newPass){
		this.username = username;
		encryptedPass = newPass;
		money = STARTING_MONEY;
		weapons = new Vector<Weapon>();
		equippedWeapons = new Vector<Weapon>();
	}
	
	//we need this because now users are going to be created prior to being in game
	//this we way call initialize to make all the gdx object only once the game has started
	public void initialize(){
		Weapon defaultRocket = new Rocket("N00b Rocket", 5, 0, 10f,
				10f, AssetLibrary.getTexture("weaponTexture1.png"), AssetLibrary.getTexture("missile.png"));
		weapons.add(defaultRocket);
		equippedWeapons.add(defaultRocket);
		Weapon godRocket = new Rocket("GOD Rocket", 50, 0, 10f,
				100f, AssetLibrary.getTexture("weaponTexture2.png"), AssetLibrary.getTexture("missile.png"));
		weapons.add(godRocket);
		equippedWeapons.add(godRocket);
		
	}
	
	//set current equipped weapon
	public void setWeapon(int weaponIndex){
		planet.setWeapon(weaponIndex);
	}
	
	//this is how we fire our weapons, it goes through the players currently equipped weapon and the planet automaticlly fires it in the correct spot
	public void fire(int powerPercent, double angle, List<GameObject> gameObjects){
		planet.FireWeapon(powerPercent, angle, gameObjects);
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPass() {
		return encryptedPass;
	}

	public boolean setPassword(String currPass, String newPass){
		if(currPass == encryptedPass){
			this.encryptedPass = newPass;
			return true;
		}
		return false;
	}

	public int getMoney() {
		return money;
	}
	
	public Planet getPlanet(){
		if(planet==null)
			planet = new Planet(equippedWeapons);
		return planet;
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
	
	public boolean removeWeapon(Weapon w){
		if(weapons.contains(w)){
			weapons.remove(w);
			return true;
		}else return false;
	}
	
	public void addWeapon(Weapon w){
		weapons.add(w);
	}
	
}

class InvalidPassException extends Exception{
	private static final long serialVersionUID = 1L;
	private String message;
	public InvalidPassException(String message){
		this.message = message;
	}
	public String getMessage(){
		return message;
	}
	public String toString(){
		return message;
	}
}
