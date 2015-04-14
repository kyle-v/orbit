package orbit;

import java.io.Serializable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Planet extends GameObject implements Serializable{
	
	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	
	public static final float DEFAULT_RADIUS = 40f;
	
	public static final float DEFAULT_MASS = 5000f;

	private float radius;
	private Texture planetSkin;
	

	public Planet() {
		super(0, 0, DEFAULT_RADIUS*2, DEFAULT_RADIUS*2);
		this.mass = DEFAULT_MASS;
		this.radius = DEFAULT_RADIUS;
		planetSkin = new Texture(Gdx.files.internal("defaultPlanet.png"));
	}
	
	
	public Planet(float x, float y, float radius) {
		super(x, y, radius*2, radius*2);
		this.mass = DEFAULT_MASS;
		this.radius = radius;
		planetSkin = new Texture(Gdx.files.internal("defaultPlanet.png"));
	}

	@Override
	public void update(int deltaTime) {
		updateVelocityAndPosition();
		// TODO Auto-generated method stub
		
	}
	
	public void draw(SpriteBatch batch){
		batch.draw(planetSkin, position.x, position.y, radius*2, radius*2);
	}

}
