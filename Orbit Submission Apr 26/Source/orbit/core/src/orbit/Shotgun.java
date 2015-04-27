package orbit;

import java.util.List;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

public class Shotgun extends Weapon {

	private int projectileWidth = 20;
	private int projectileHeight = 11;
	private int numShots = 3;
	private boolean gravitytoggle = true;
	
	public Shotgun(String name, int damage, int cooldown, float projectileMass,
			float maxInitialSpeed, String weaponFilename,
			String projectileFilename) {
		super(name, damage, cooldown, projectileMass, maxInitialSpeed, weaponFilename,
				projectileFilename);
		// TODO Auto-generated constructor stub
	}

	@Override
	public synchronized void fire(float xPosition, float yPosition, int powerPercent,
			double angle, List<GameObject> gameObjects) {
		System.out.println("Shotgun " + name + " was fired with power "+ powerPercent + "% at an angle " + angle);
		float increment = (float)Math.PI/(3f*numShots);
		Vector2 initSpeed = new Vector2(maxInitialSpeed*(float)(powerPercent)/100f, 0f);
		initSpeed.rotateRad((float)angle);
		Vector2 initSpeed2 = new Vector2(maxInitialSpeed*(float)(powerPercent)/100f, 0f);
		initSpeed2.rotateRad((float)angle + increment);
		Vector2 initSpeed3 = new Vector2(maxInitialSpeed*(float)(powerPercent)/100f, 0f);
		initSpeed3.rotateRad((float)angle - increment);
		Sound fireSound = AssetLibrary.getSound("LaserShot.wav");
		fireSound.play();
		/*
		 * creates a new vector with the selected power in the x direction and then rotates
		 * it by the selected angle
		 */

		Projectile p = new Projectile(xPosition, yPosition, projectileWidth, projectileHeight, initSpeed, (float)angle, this, gameObjects, gravitytoggle);
		gameObjects.add(p);
		Projectile p1 = new Projectile(xPosition, yPosition - 50, projectileWidth, projectileHeight, initSpeed2, (float)angle + increment, this, gameObjects, gravitytoggle);
		gameObjects.add(p1);
		Projectile p2 = new Projectile(xPosition, yPosition + 50, projectileWidth, projectileHeight, initSpeed3, (float)angle - increment, this, gameObjects, gravitytoggle);
		gameObjects.add(p2);
		
	}

}
