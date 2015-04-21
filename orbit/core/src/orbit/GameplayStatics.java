package orbit;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

public class GameplayStatics {
	private static World world;
	
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