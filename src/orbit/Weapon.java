package orbit;

import java.awt.Image;

public abstract class Weapon {
	
	//Weapon Properties
	protected String name;
	protected int damage;
	protected int cooldown;
	protected float projectileMass;
	protected float maxInitialSpeed;
	
	//Graphics
	public Image weaponImage;
	public Image projectileImage;
	
	//Constructor
	public Weapon(String name, int damage, int cooldown, float projectileMass,
			float maxInitialSpeed, Image weaponImage, Image projectileImage) {
		super();
		this.name = name;
		this.damage = damage;
		this.cooldown = cooldown;
		this.projectileMass = projectileMass;
		this.maxInitialSpeed = maxInitialSpeed;
		this.weaponImage = weaponImage;
		this.projectileImage = projectileImage;
	}
	
	public abstract GameObject[] fire();

	
	
	
}
