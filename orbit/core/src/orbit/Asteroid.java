package orbit;

import java.io.Serializable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;



public class Asteroid extends GameObject implements Serializable {
	float angle;
	Texture texture;
	public Asteroid(float x, float y,Vector2 initialSpeed, float initialAngle){
		super(x,y,50,50);
		this.texture = new Texture(Gdx.files.internal("asteroids.png"));
		this.sprite = new Sprite(this.texture);
		createPhysicsBody();
	}

	@Override
	public void OnCollisionEnter(Contact contact, boolean isA) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnCollisionExit(Contact contact, boolean isA) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(float DeltaTime) {
		// TODO Auto-generated method stub
		updateVelocityAndPosition(DeltaTime);
		sprite.rotate(5);
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

}
