package orbit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Planet extends GameObject implements Serializable{
	
	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	
	public static final float DEFAULT_RADIUS = 40f;
	
	public static final float DEFAULT_MASS = 2500f;
	
	public static final float DEFAULT_STARTING_HEALTH = 100;
	
	public static final float ORBIT_ROTATION_SPEED = .05f; //in radians

	public static final float LOCAL_ROTATION_SPEED = 50;
	
	public float health;
	private float radius;
	private String planetSkinPath;
	
	private Vector<Weapon> weapons;
	private Weapon equippedWeapon;
	private int equippedWeaponIndex;
	private float degreePosition;
	private float localRotation;
	private String destroyedTexturePath;

	public Planet(Vector<Weapon> weapons, String texturePath, String destroyedTexturePath) {
		super(0, 0, DEFAULT_RADIUS*2, DEFAULT_RADIUS*2);
		this.mass = DEFAULT_MASS;
		this.radius = DEFAULT_RADIUS;
		this.health = DEFAULT_STARTING_HEALTH;
		planetSkinPath = texturePath;
		this.imagePath = planetSkinPath;
		Texture texture = AssetLibrary.getTexture(planetSkinPath);
		sprite = new Sprite(texture);
		//createPhysicsBody();
		this.weapons = weapons;
		equippedWeapon = this.weapons.get(0);
		equippedWeaponIndex = 0;
		Random randy = new Random();
		localRotation = randy.nextFloat() * 360;
		this.destroyedTexturePath = destroyedTexturePath;
	}
	
	
	public Planet(float x, float y, float radius, float startingHealth, Weapon weapon) {
		super(x, y, radius*2, radius*2);
		this.mass = DEFAULT_MASS;
		this.radius = radius;
		health = startingHealth;
		planetSkinPath = "defaultPlanet.png";
		this.imagePath = planetSkinPath;
		Texture texture = AssetLibrary.getTexture(planetSkinPath);
		sprite = new Sprite(texture);
		sprite.setPosition(position.x - radius, position.y - radius);
		equippedWeapon = weapon;
	}
	
	public void takeDamage(float damage){
		System.out.println("Got damaged");
		health -= damage;
		if(health<=0){
			Texture texture = AssetLibrary.getTexture(destroyedTexturePath);
			sprite.setTexture(texture);
		}
		System.out.println(health);
	}
	
	public void setWeapon(int weaponIndex){
		equippedWeapon = weapons.get(weaponIndex);
	}
	
	public void setWeapon(Weapon weapon){
		equippedWeapon = weapon;
	}
	
	public int getWeaponIndex(){
		return equippedWeaponIndex;
	}
	
	public void setDegreePosition(float pos){
		degreePosition = pos;
	}
	
	public void FireWeapon(int powerPercent, double angle, List<GameObject> gameObjects){
		int buffer = AssetLibrary.getTexture(equippedWeapon.projectileFilename).getWidth() + 50;
		buffer += radius;
		float xPosition = (float) Math.cos(angle) * buffer;
		float yPosition = (float) Math.sin(angle) * buffer;
		equippedWeapon.fire(position.x + xPosition, position.y + yPosition, powerPercent, angle, gameObjects);
	}
	
	//should use set position to change a game objects position now because it updates the physics body as well
	public void SetPosition(Vector2 pos){
		position = pos;
		sprite.setPosition(pos.x - radius, pos.y - radius);
	}

	@Override
	public void update(float DeltaTime) {
		updateVelocityAndPosition(DeltaTime);
		degreePosition += DeltaTime * ORBIT_ROTATION_SPEED;
	}
	
	public void draw(SpriteBatch batch){
		
		sprite.setSize(radius*2 , radius*2);
		super.draw(batch);
	}
	
	public boolean checkCollision(GameObject other){
		if (this.bounds.overlaps(other.bounds)){
			//deplete planet health
			System.out.println("Planet is hit");
			return true;
		}
		return false;
	}
	
	public String getName(){
		return "Planet";
	}

}

