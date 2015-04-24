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
	WeaponGenerator(){
		random = new Random();
		numAdjectives = adjectives.length;
		numNouns = nouns.length;
	}
	
	public Weapon generateWeapon(){
		int adjectiveIndex;
		int nounIndex;
	}
}
