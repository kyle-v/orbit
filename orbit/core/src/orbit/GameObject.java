package orbit;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public abstract class GameObject {
	protected Vector2 position; //Vector for position, velocity, acceleration (like Unity)
	protected Vector2 velocity;
	protected Vector2 acceleration;
	protected Rectangle bounds; //Rectangle for checking collisions
	protected float rotation;
	protected float mass;
	protected float width;
	protected float height;
	protected boolean isDead;
	protected Body body;
	protected Sprite sprite;
	
	public GameObject (float x, float y, float width, float height){
		/*
		 * WARNING: It seems that libdgx, unlike most graphical libraries,
		 * treats the screen as: x going left to right and y going down to up.
		 * Most graphical libraries treat y as going from up to down, i.e. adding i to y will make y go down screen i pixels.
		 * Just a heads up when messing with coordinate values.
		 * 
		 */
		this.position = new Vector2(x,y);
		this.bounds = new Rectangle(x - width/2, y - height/2, width, height); 
		this.width = width;
		this.height = height;
		//Rectangles are initialized as Rectangle(x,y,width,height), with x and y being the coordinates of the lower left of rectangle
		this.velocity = new Vector2();
		this.acceleration = new Vector2();
		rotation = 0;
		mass = 0;
	}
	
	public void createPhysicsBody(){
		BodyDef bodyDef = new BodyDef();
	     bodyDef.type = BodyDef.BodyType.DynamicBody;
	     bodyDef.position.set((position.x + width/2) /
	                        GameplayStatics.pixelsToMeters(),
	                (position.y + height/2) / GameplayStatics.pixelsToMeters());
	     body = GameplayStatics.getWorld().createBody(bodyDef);
	     PolygonShape shape = new PolygonShape();
	     shape.setAsBox(width/2/GameplayStatics.pixelsToMeters(), height/2/GameplayStatics.pixelsToMeters());
	     FixtureDef fixtureDef = new FixtureDef();
	     fixtureDef.shape = shape;
	     fixtureDef.density = 0.1f;
	     fixtureDef.restitution = 0.5f;
	     body.createFixture(fixtureDef);
	     body.setUserData(this);
	     shape.dispose();
	}
	
	protected void updateVelocityAndPosition(float DeltaTime){
		body.setLinearVelocity(body.getLinearVelocity().add(acceleration.setLength(DeltaTime)));
		//body.setTransform(body.getPosition(), body.getLinearVelocity().angle());
		body.setAngularVelocity(0);
		velocity.add(acceleration);
		position.add(velocity);
		bounds.x = position.x - width/2;
		bounds.y = position.y - height/2;
	}
	
	//functions that will be called when a collision happens
	//because contact has info for both A and B and does not distinguish the bool tells you which object to look at for the gameobject collided with
	abstract public void OnCollisionEnter(Contact contact, boolean isA);
	
	abstract public void OnCollisionExit(Contact contact, boolean isA);
	
	abstract public void update(float DeltaTime);
	
	public void draw(SpriteBatch b){
		sprite.setPosition(body.getPosition().x * GameplayStatics.pixelsToMeters() - width/2, body.getPosition().y * GameplayStatics.pixelsToMeters() - height/2);
		sprite.draw(b);
	}
	
	abstract public boolean checkCollision(GameObject other);
	
	abstract public String getName();
}
