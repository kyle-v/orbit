package orbit;

import java.awt.Image;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;

public abstract class Weapon {
	
	//Weapon Properties
	protected String name;
	protected int damage;
	protected int cooldown;
	protected float projectileMass;
	protected float maxInitialSpeed;
	
	//Graphics
	public Texture weaponImage;
	public String projectileImage;
	
	//Constructor
	public Weapon(String name, int damage, int cooldown, float projectileMass,
			float maxInitialSpeed, Texture weaponImage, String projectileImage) {
		super();
		this.name = name;
		this.damage = damage;
		this.cooldown = cooldown;
		this.projectileMass = projectileMass;
		this.maxInitialSpeed = maxInitialSpeed;
		this.weaponImage = weaponImage;
		this.projectileImage = projectileImage;
	}
	
	public abstract void fire(int powerPercent, double angle, ArrayList<GameObject> gameObjects);
	//changed parameters since the angle and power need to be selected before a projectile can be created
	//added a projectile parameter
}
