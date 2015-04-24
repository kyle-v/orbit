package orbit;

import java.awt.Image;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Weapon implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Weapon Properties
	protected String name;
	protected int damage;
	protected int cooldown;
	protected float projectileMass;
	protected float maxInitialSpeed;
	protected String weaponFilename;
	protected String projectileFilename;
	
	protected Sprite sprite;
	
	//Constructor
	public Weapon(String name, int damage, int cooldown, float projectileMass,
			float maxInitialSpeed, String weaponFilename, String projectileFilename) {
		super();
		this.name = name;
		this.damage = damage;
		this.cooldown = cooldown;
		this.projectileMass = projectileMass;
		this.maxInitialSpeed = maxInitialSpeed;
		this.weaponFilename = weaponFilename;
		this.projectileFilename = projectileFilename;
		
		this.sprite = new Sprite(AssetLibrary.getTexture(weaponFilename));
	}
	
	public abstract void fire(float xPosition, float yPosition, int powerPercent, double angle, List<GameObject> gameObjects);
	//changed parameters since the angle and power need to be selected before a projectile can be created
	//added a projectile parameter
	
	public String getName(){
		return this.name;
	}
	
	public int getDamage(){
		return this.damage;
	}
	
	public int getCooldown(){
		return this.cooldown;
	}
	
	public float getMass(){
		return this.projectileMass;
	}
	
	public float getSpeed(){
		return this.maxInitialSpeed;
	}
}
