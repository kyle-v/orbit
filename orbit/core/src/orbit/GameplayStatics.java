package orbit;

import java.util.Random;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

public class GameplayStatics {
	//this things just has statics that are good to have across the entire game
	private static World world;
	private static final float PIXELS_TO_METERS = 50;
	private static ShapeRenderer renderer;
	public static OrbitGame game;
	public static Random randy;
	
	public static void setShapeRenderer(ShapeRenderer srenderer){
		renderer = srenderer;
	}
	
	public static ShapeRenderer getShapeRenderer(){
		return renderer;
	}
	
	public static float pixelsToMeters(){
		return PIXELS_TO_METERS;
	}
	
	public static World getWorld(){
		if(world == null)
		{
			world = new World(new Vector2(0,0),true);
			world.setContactListener(new CollisionListener());
		}
		return world;
		
	}
	
	private static class CollisionListener implements ContactListener{

		public void beginContact(Contact contact) {
			//call the collision functions on the two bodies that collided
			((GameObject)(contact.getFixtureA().getBody().getUserData())).OnCollisionEnter(contact, true);
			((GameObject)(contact.getFixtureB().getBody().getUserData())).OnCollisionEnter(contact, false);
		}

		@Override
		public void endContact(Contact contact) {
			((GameObject)(contact.getFixtureA().getBody().getUserData())).OnCollisionExit(contact, true);
			((GameObject)(contact.getFixtureB().getBody().getUserData())).OnCollisionExit(contact, false);
		}

		@Override
		public void preSolve(Contact contact, Manifold oldManifold) {
			
		}

		@Override
		public void postSolve(Contact contact, ContactImpulse impulse) {
			
		}
		
	}
	
}
