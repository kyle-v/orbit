package orbit;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class GameObject {
	protected Vector2 position; //Vector for position, velocity, acceleration (like Unity)
	protected Vector2 velocity;
	protected Vector2 acceleration;
	protected Rectangle bounds; //Rectangle for checking collisions
	protected float rotation;
	protected float mass;
	
	public GameObject (float x, float y, float width, float height){
		/*
		 * WARNING: It seems that libgx, unlike most graphical libraries,
		 * treats the screen as: x going left to right and y going down to up.
		 * Most graphical libraries treat y as going from up to down, i.e. adding i to y will make y go down screen i pixels.
		 * Just a heads up when messing with coordinate values.
		 * 
		 */
		this.position = new Vector2(x,y);
		this.bounds = new Rectangle(x - width/2, y - height/2, width, height); 
		//Rectangles are initialized as Rectangle(x,y,width,height), with x and y being the coordinates of the lower left of rectangle
		this.velocity = new Vector2();
		this.acceleration = new Vector2();
		rotation = 0;
		mass = 0;
	}
	
	//Setters
	public void setPosition(float x, float y){
		this.position = new Vector2(x,y);
	}
	
	public void setVelocity(float x, float y){
		this.velocity = new Vector2(x,y);
	}
	
	public void setAcceleration(float x, float y){
		this.acceleration = new Vector2(x,y);
	}
	
	public void setRotation(float degrees){
		this.rotation = degrees;
	}
	
	public void setMass(float mass){
		this.mass = mass;
	}
	
	public void setBounds (float x, float y, float width, float height){
		this.bounds = new Rectangle(x - width/2, y - height/2, width, height); 
	}
	
	//Getters
	public Vector2 getPosition(){
		return this.position;
	}
	
	public Vector2 getVelocity(){
		return this.velocity;
	}
	
	public Vector2 getAcceleration(){
		return this.acceleration;
	}
	
	public float getRotation(){
		return this.rotation;
	}
	
	public float getMass(){
		return this.mass;
	}
	
	public Rectangle getBounds(){
		return this.bounds;
	}
	
	protected void updateVelocityAndPosition(){
		velocity.add(acceleration);
		position.add(velocity);
	}
	
	abstract public void update(int deltaTime);
}
