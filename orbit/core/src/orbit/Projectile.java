package orbit;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;


public class Projectile extends GameObject {
	String projectileImage;
	int damage;
	float angle;
	static final float g = 5f;
	static final float MAXGRAVITY = 2f;
	static final float MINDISTANCE = 0f;
	boolean gravitytoggle;
	List<GameObject> gameObjects;
	//transient Sprite sprite;
	
	//Constructor
	/*
	 * The weapon should pass the initial speed, 
	 * and the weapon that created the projectile
	 *  every time it creates a new projectile in
	 *  addition to the x and y coords and height, width
	 */
	public Projectile(float x, float y, float width, float height, Vector2 initialSpeed, float initAngle,Weapon owner, List<GameObject> gameObjects2, boolean gravitytoggle) {
		super(x, y, width, height);
		sprite = new Sprite(AssetLibrary.getTexture(owner.projectileFilename));
		sprite.setPosition(x, y);
		this.imagePath = owner.projectileFilename;
		this.mass = owner.projectileMass;
		this.velocity = initialSpeed;
		System.out.println("Initial speed" + initialSpeed);
		this.damage = owner.damage;
		this.gameObjects = gameObjects2;
		this.gravitytoggle = gravitytoggle;
		//createPhysicsBody();
		//body.setLinearVelocity(initialSpeed);
	}

	@Override
	public void update(float DeltaTime) {
		if(gravitytoggle) calculateGravity();
		updateVelocityAndPosition(DeltaTime);
		System.out.println(" x " + position.x + " y " + position.y);
		System.out.println(" vx " + velocity.x + " vy " + velocity.y);
		System.out.println(" ax " + acceleration.x + " ay " + acceleration.y);

		for (GameObject o : gameObjects){
			if(o != this){
//				if (checkCollision(o)){
//					//isDead = true;
//				}
			}
		}
		// TODO Auto-generated method stub
	}

	public void calculateGravity(){
		Vector2 gravity_o;
		float distance;
		Vector2 grav = new Vector2(0f,0f);
		/*
		 * We need to iterate through all the game objects to calculate the gravity
		 */
		for(GameObject o: gameObjects){
			if(o != this){

				/*
				 * Figure out whether the acceleration is positive or negative
				 */

				distance = Math.abs(o.position.dst(this.position));

				gravity_o = new Vector2(o.position.x - this.position.x, o.position.y - this.position.y);
				gravity_o.setLength((o.mass * g)/(distance*distance));
				grav.add(gravity_o);

				// calculate Gravity
			}

		}


		if(grav.x > MAXGRAVITY){
			grav.x = MAXGRAVITY;
		}
		else if(grav.x < -MAXGRAVITY){
			grav.x = -MAXGRAVITY;
		}

		if(grav.y > MAXGRAVITY){
			grav.y = MAXGRAVITY;
		}
		else if(grav.y < -MAXGRAVITY){
			grav.y = -MAXGRAVITY;
		}

		//Limit the gravity since it can get very high 
		this.acceleration = grav;
	}

	public void draw(SpriteBatch batch){
		//--------------WE'RE USING SPRITES NOW MOTHERFUCKERS
		
		sprite.setRotation(velocity.angle());
		super.draw(batch);
		/*
		batch.draw(AssetLibrary.getTexture(projectileImage),
				position.x - width/2,
				position.y - height/2,
				width/2, //pivot point for scaling and rotation
				height/2, // ^
				width,
				height,
				1 , //scale
				1,
				this.velocity.angle(), //rotation
				0, //From image file (for spritesheets)
				0,
				(int)width,
				(int)height,
				false,
				false);*/
	}
	
	public boolean checkCollision(GameObject other){
		if (this.bounds.overlaps(other.bounds)){ //use this to check for collisions
			//System.out.println(this.getName() + " hit " + other.getName());
			return true;
		}
		return false;
	}
	
	public String getName(){
		return "Projectile";
	}
	
	public boolean isDead(){
		return this.isDead;
	}

	@Override
	public void OnCollisionEnter(Contact contact, boolean isA) {
		GameObject collided;
		if(isA){
			collided = (GameObject)contact.getFixtureB().getBody().getUserData();
		}
		else{
			collided = (GameObject)contact.getFixtureA().getBody().getUserData();
		}
		System.out.println("test");
		if(collided instanceof Planet){
			Planet p = (Planet)collided;
			p.takeDamage(damage);
			isDead = true;
			GameplayStatics.game.checkWinCondition();
		}
		if(collided instanceof Asteroid){
			isDead = true;
		}
	}

	@Override
	public void OnCollisionExit(Contact contact, boolean isA) {
		// TODO Auto-generated method stub
		
	}
}
