package orbit;

import java.awt.Image;

public class Rocket extends Weapon{

	public Rocket(String name, int damage, int cooldown, float projectileMass,
			float maxInitialSpeed, Image weaponImage, Image projectileImage) {
		super(name, damage, cooldown, projectileMass, maxInitialSpeed, weaponImage,
				projectileImage);
	}

	public GameObject[] fire() {
		System.out.println("Rocket " + name + " was fired");
		return null;
	}
	

}
