package orbit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;


public class Projectile extends GameObject {
	Texture projectileImage;
	float mass;
	int damage;
	float width;
	float height;
	float angle;
	//Constructor
	/*
	 * The weapon should pass the initial speed, 
	 * and the weapon that created the projectile
	 *  every time it creates a new projectile in
	 *  addition to the x and y coords and height, width
	 */
	public Projectile(float x, float y, float width, float height, Vector2 initialSpeed, float initAngle,Weapon owner) {
		super(x, y, width, height);
		this.width = width;
		this.height = height;
		this.projectileImage = owner.projectileImage;
		this.mass = owner.projectileMass;
		this.velocity = initialSpeed;
		this.damage = owner.damage;
	}

	@Override
	public void update(int deltaTime) {
		if(this.velocity.angle() > 180){
			angle = this.velocity.angle()-360;
			angle = angle*Gdx.graphics.getHeight()/Gdx.graphics.getWidth();
			angle += 360;
		}
		else angle = this.velocity.angle()*Gdx.graphics.getHeight()/Gdx.graphics.getWidth();
		/*
		 * The angle needs to be corrected for the screen size since screen is rectangular
		 */
		updateVelocityAndPosition();
		// TODO Auto-generated method stub
		
	}
	
	public void draw(SpriteBatch batch){
		//batch.draw(projectileImage, position.x, position.y, width, height);
		batch.draw(projectileImage,
				position.x,
				position.y,
				 position.x - (width/2),
				 position.y - (height/2),
                width,
                height,
                0.5f,
                0.5f,
                angle,
                0,
                0,
                (int)width,
                (int)height,
                false,
                false);
	}

}
