package orbit;

import java.awt.Image;

import com.badlogic.gdx.math.Vector2;


public class Projectile extends GameObject {
	Image projectileImage;
	float mass;
	Vector2 initialSpeed;
	int damage;
	
	//Constructor
	/*
	 * The weapon should pass the initial speed, 
	 * and the weapon that created the projectile
	 *  every time it creates a new projectile in
	 *  addition to the x and y coords and height, width
	 */
	public Projectile(float x, float y, float width, float height, Vector2 initialSpeed, Weapon owner) {
		super(x, y, width, height);
		this.projectileImage = owner.projectileImage;
		this.mass = owner.projectileMass;
		this.initialSpeed = initialSpeed;
		this.damage = owner.damage;
	}

	@Override
	public void update(int deltaTime) {
		// TODO Auto-generated method stub
		
	}

}
