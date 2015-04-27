package orbit;

import java.util.Random;

public class WeaponGenerator {
	String[] adjectives = {"Boss","Cool","Dapper","Ecstatic","Jammin'","Wacky","Sick","Killer","Lame","Tubular","Super","Baller","Dank",
			"Jacked","Smashin'","Evil"};
	String [] nouns = {"Lizard","Eagle","Lion","Destructor","Zebra","Octopus","Lai","Aditya","Park","Kyle","Li","Miller","Ford","Lemon",
			"Predator","Beast","Duck","Rocket","Shooter","Gun","Launcher"};
	private Random random;
	private int numAdjectives;
	private int numNouns;
	private static final int MAX_DAMAGE = 20;
	private static final int MAX_COOLDOWN = 5;
	private static final float MAX_MASS = 100f;
	private static final float MAX_SPEED = 100f;
	WeaponGenerator(){
		random = new Random();
		numAdjectives = adjectives.length;
		numNouns = nouns.length;
	}
	
	//generates a random weapon
	public Weapon makeWeapon(){
		int adjectiveIndex = random.nextInt(numAdjectives);
		int nounIndex = random.nextInt(numNouns);
		String name = "The " + adjectives[adjectiveIndex] + nouns[nounIndex];
		int damage = random.nextInt(MAX_DAMAGE + 1) + 1;
		int cooldown = random.nextInt(MAX_COOLDOWN + 1);
		float mass = random.nextFloat() * (MAX_MASS - 1) + 1;
		float speed = random.nextFloat() * (MAX_SPEED - 1) + 1;
		
		return new Rocket(name,damage,cooldown,mass,speed,"weapons/weaponTexture1.png","projectiles/rocket.png");
	}
}
