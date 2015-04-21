package orbit;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
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
	
	protected void updateVelocityAndPosition(){
		velocity.add(acceleration);
		position.add(velocity);
		bounds.x = position.x - width/2;
		bounds.y = position.y - height/2;
	}
	
	//functions that will be called when a collision happens
	//because contact has info for both A and B and does not distinguish the bool tells you which object to look at for the gameobject collided with
	abstract public void OnCollisionEnter(Contact contact, boolean isA);
	
	abstract public void OnCollisionExit(Contact contact, boolean isA);
	
	abstract public void update();
	
	abstract public void draw(SpriteBatch b);
	
	abstract public boolean checkCollision(GameObject other);
	
	abstract public String getName();
}
