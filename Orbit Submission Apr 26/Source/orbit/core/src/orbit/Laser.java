package orbit;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Laser extends Weapon{
	
	private int projectileWidth = 20;
	private int projectileHeight = 11;
	private boolean gravitytoggle = false;
	
	public Laser(String name, int damage, int cooldown, float projectileMass,
			float maxInitialSpeed, String weaponFilename, String projectileFilename) {
			super(name, damage, cooldown, projectileMass, maxInitialSpeed, weaponFilename, projectileFilename);
	}

	public void fire(float xPosition, float yPosition, int powerPercent, double angle, List<GameObject> gameObjects) {
		System.out.println("Laser " + name + " was fired with power "+ powerPercent + "% at an angle " + angle);
		Vector2 initSpeed = new Vector2(maxInitialSpeed*(float)(powerPercent)/100f, 0f);
		initSpeed.rotateRad((float)angle);
		Sound fireSound = AssetLibrary.getSound("LaserShot.wav");
		fireSound.play();
		/*
		 * creates a new vector with the selected power in the x direction and then rotates
		 * it by the selected angle
		 */
		
		Projectile p = new Projectile(xPosition, yPosition, projectileWidth, projectileHeight, initSpeed, (float)angle, this, gameObjects, gravitytoggle);
		gameObjects.add(p);
	}
	

}