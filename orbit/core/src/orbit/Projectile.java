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
	static final float g = 0.1f;
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
		float directionx;
		float directiony;
		float gravity_x;
		float gravity_y;
		Vector2 grav = new Vector2(0f,0f);
		for(GameObject o: gameObjects){
			if(o != this){
				if(o.position.x > this.position.x){
					directionx = 1f;
				}
				else 
					directionx = -1f;
				if(o.position.y > this.position.y){
					directiony = 1f;
				}
				else 
					directiony = -1f;
				gravity_x = directionx * (float) ((o.mass * g)/Math.pow(o.position.x - this.position.x, 2f));
				gravity_y = directiony * (float) ((o.mass * g)/Math.pow(o.position.y - this.position.y, 2f));
				//System.out.println(" x " + (o.position.x - this.position.x));
				//System.out.println(" y " + (o.position.y - this.position.y));
				if(gravity_x > 1){
					gravity_x = 1;
				}
				else if(gravity_x < -1){
					gravity_x = -1;
				}
				
				if(gravity_y > 1){
					gravity_y = 1;
				}
				else if(gravity_y < -1){
					gravity_y = -1;
				}
				
					
			
				grav.add(new Vector2(gravity_x , gravity_y));
			}

		}
		//System.out.println("Gravity y: "+ grav.x +" Gravity x: "+ grav.y);
		//this.acceleration = grav;
	}
	
	public void draw(SpriteBatch batch){
		batch.draw(projectileImage,
				position.x,
				position.y,
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
