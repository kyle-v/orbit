package orbit;

import java.io.Serializable;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;



public class Asteroid extends GameObject implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final float DEFAULT_MASS = 500f;
	public static final float DEFAULT_RADIUS = 30f;
	public static final float DEFAULT_DRAG = .0f;
	private static final float MAX_ROTATION = 5f;
	private static final float MIN_ROTATION = -5f;
	private float drag;
	private float radius;
	private float rotationDirection;
	float angle;
	transient Texture texture;
	public Asteroid(float x, float y,Vector2 initialSpeed, float initialAngle){
		super(x,y,DEFAULT_RADIUS*2,DEFAULT_RADIUS*2);
		this.imagePath = "asteroid.png";
		this.texture = AssetLibrary.getTexture("asteroid.png");
		this.sprite = new Sprite(this.texture);
		this.mass = DEFAULT_MASS;
		this.radius = DEFAULT_RADIUS;
		this.drag = DEFAULT_DRAG;
		
		//sets a random rotation direction for the asteroid
		Random randomizer = GameplayStatics.randy;
		rotationDirection = randomizer.nextFloat() * (MAX_ROTATION - MIN_ROTATION + 1) + MIN_ROTATION;
	}

	@Override
	public void update(float DeltaTime) {
		// TODO Auto-generated method stub
		updateVelocityAndPosition(DeltaTime);
	}
	
	protected void updateVelocityAndPosition(float DeltaTime){
		super.updateVelocityAndPosition(DeltaTime);
	}
	
	public void draw(SpriteBatch batch){
		sprite.setPosition(position.x - 
                radius,
        position.y - radius);
		//spin the asteroid around
		sprite.rotate(rotationDirection);
		sprite.draw(batch);
	}

	@Override
	public boolean checkCollision(GameObject other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Asteroid";
	}
	
	private Vector2 getLinearDrag(Vector2 vel){ //gets a new Vector2 with linear drag applied
		float newXVel = vel.x;
		float newYVel = vel.y;
		
		
		//X Velocity
		if (newXVel > 0){ // subtract drag if velocity is positive
			newXVel -= drag * vel.x;
			if (newXVel < 0){ // set velocity to 0 if it becomes negative
				newXVel = 0;
			}
		} else if (newXVel < 0){ // add drag if velocity is negative
			newXVel += drag * -vel.x; // set velocity to 0 if it becomes positive
			if (newXVel > 0){ // set velocity to 0 if it becomes positive
				newXVel = 0;
			}
		}
		
		//Y Velocity
		if (newYVel > 0){ // subtract drag if velocity is positive
			newYVel -= drag * vel.y;
			if (newYVel < 0){ // set velocity to 0 if it becomes negative
				newYVel = 0;
			}
		} else if (newYVel < 0){ // add drag if velocity is negative
			newYVel += drag * -vel.y; // set velocity to 0 if it becomes positive
			if (newYVel > 0){ // set velocity to 0 if it becomes positive
				newYVel = 0;
			}
		}

		
		return new Vector2(newXVel,newYVel);
	}

}
