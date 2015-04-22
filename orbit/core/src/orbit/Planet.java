package orbit;

import java.io.Serializable;
import java.util.ArrayList;

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
	
	public static final float DEFAULT_MASS = 5000f;
	
	public static final float DEFAULT_STARTING_HEALTH = 100;
	
	public float health;
	private float radius;
	private Texture planetSkin;
	private Sprite sprite;
	private Weapon equippedWeapon;

	public Planet(Weapon weapon) {
		super(0, 0, DEFAULT_RADIUS*2, DEFAULT_RADIUS*2);
		this.mass = DEFAULT_MASS;
		this.radius = DEFAULT_RADIUS;
		this.health = DEFAULT_STARTING_HEALTH;
		planetSkin = new Texture(Gdx.files.internal("defaultPlanet.png"));
		sprite = new Sprite(planetSkin);
		createPhysicsBody();
		equippedWeapon = weapon;
	}
	
	
	public Planet(float x, float y, float radius, float startingHealth, Weapon weapon) {
		super(x, y, radius*2, radius*2);
		this.mass = DEFAULT_MASS;
		this.radius = radius;
		health = startingHealth;
		planetSkin = new Texture(Gdx.files.internal("defaultPlanet.png"));
		sprite = new Sprite(planetSkin);
		sprite.setPosition(position.x - radius, position.y - radius);
		createPhysicsBody();
		equippedWeapon = weapon;
	}
	
	//function to generate the physics body that this sprite is paired to
	public void createPhysicsBody(){
		BodyDef bodyDef = new BodyDef();
	     bodyDef.type = BodyDef.BodyType.StaticBody;
	     bodyDef.position.set((sprite.getX() + radius) /
	                        GameplayStatics.pixelsToMeters(),
	                (sprite.getY() + radius) / GameplayStatics.pixelsToMeters());
	     body = GameplayStatics.getWorld().createBody(bodyDef);
	     PolygonShape shape = new PolygonShape();
	     shape.setRadius(radius/ GameplayStatics.pixelsToMeters());
	     shape.setAsBox(radius/2/GameplayStatics.pixelsToMeters(), radius/2/GameplayStatics.pixelsToMeters());
	     FixtureDef fixtureDef = new FixtureDef();
	     fixtureDef.shape = shape;
	     fixtureDef.density = 0.1f;
	     fixtureDef.restitution = 0.5f;
	     body.createFixture(fixtureDef);
	     body.setUserData(this);
	     shape.dispose();
	}
	
	public void FireWeapon(int powerPercent, double angle, ArrayList<GameObject> gameObjects){
		int buffer = equippedWeapon.projectileImage.getWidth() + 50;
		buffer += radius;
		float xPosition = (float) Math.cos(angle) * buffer;
		float yPosition = (float) Math.sin(angle) * buffer;
		equippedWeapon.fire(sprite.getX() + xPosition, sprite.getY() + yPosition, powerPercent, angle, gameObjects);
	}
	
	//should use set position to change a game objects position now because it updates the physics body as well
	public void SetPosition(Vector2 pos){
		position = pos;
		sprite.setPosition(pos.x - radius, pos.y - radius);
		body.setTransform((sprite.getX() + radius) /
                GameplayStatics.pixelsToMeters(),
                (sprite.getY() + radius) / GameplayStatics.pixelsToMeters(), 0);
	}

	@Override
	public void update(float DeltaTime) {
		updateVelocityAndPosition(DeltaTime);
		//set the sprites position to the same as 
		sprite.setPosition((body.getPosition().x * GameplayStatics.pixelsToMeters()) - 
                radius,
        (body.getPosition().y * GameplayStatics.pixelsToMeters()) - radius);
		// TODO Auto-generated method stub
		
	}
	
	public void draw(SpriteBatch batch){
		sprite.setSize(radius*2 , radius*2);
		sprite.draw(batch);
		//batch.draw(planetSkin, position.x - radius, position.y - radius, radius*2, radius*2);
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


	@Override
	public void OnCollisionEnter(Contact contact, boolean isA) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void OnCollisionExit(Contact contact, boolean isA) {
		// TODO Auto-generated method stub
		
	}

}

