package orbit;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;


public class Projectile extends GameObject {
	Texture projectileImage;
	int damage;
	float width;
	float height;
	float angle;
	static final float g = .1f;
	static final float MAXGRAVITY = 2f;
	static final float MINDISTANCE = 0f;
	ArrayList<GameObject> gameObjects;
	//Constructor
	/*
	 * The weapon should pass the initial speed, 
	 * and the weapon that created the projectile
	 *  every time it creates a new projectile in
	 *  addition to the x and y coords and height, width
	 */
	public Projectile(float x, float y, float width, float height, Vector2 initialSpeed, float initAngle,Weapon owner, ArrayList<GameObject> gameObjects) {
		super(x, y, width, height);
		this.width = width;
		this.height = height;
		this.projectileImage = owner.projectileImage;
		this.mass = owner.projectileMass;
		this.velocity = initialSpeed;
		this.damage = owner.damage;
		this.gameObjects = gameObjects;

	}

	@Override
	public void update(int deltaTime) {
		calculateGravity();
		updateVelocityAndPosition();
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
		batch.draw(projectileImage,
				position.x - width/2,
				position.y - height/2,
				(width/2), //pivot point for scaling and rotation
				(height/2), // ^
				width,
				height,
				0.2f, //scale
				0.2f,
				this.velocity.angle(), //rotation
				0, //From image file (for spritesheets)
				0,
				(int)width,
				(int)height,
				false,
				false);
	}

}
