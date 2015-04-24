package orbit;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Rocket extends Weapon{
	
	private int projectileWidth = 51;
	private int projectileHeight = 36;
	
	public Rocket(String name, int damage, int cooldown, float projectileMass,
			float maxInitialSpeed, String weaponFilename, String projectileFilename) {
			super(name, damage, cooldown, projectileMass, maxInitialSpeed, weaponFilename, projectileFilename);
	}

	public void fire(float xPosition, float yPosition, int powerPercent, double angle, List<GameObject> gameObjects) {
		System.out.println("Rocket " + name + " was fired with power "+ powerPercent + "% at an angle " + angle);
		Vector2 initSpeed = new Vector2(maxInitialSpeed*(float)(powerPercent)/100f, 0f);
		initSpeed.rotateRad((float)angle);
		/*
		 * creates a new vector with the selected power in the x direction and then rotates
		 * it by the selected angle
		 */
		
		Projectile p = new Projectile(xPosition, yPosition, projectileWidth, projectileHeight, initSpeed, (float)angle, this, gameObjects);
		gameObjects.add(p);
	}
	

}
