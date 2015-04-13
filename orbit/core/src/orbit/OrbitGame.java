package orbit;

import java.awt.Image;
import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class OrbitGame extends ApplicationAdapter {
	SpriteBatch batch;
	User player; 
	ArrayList<User> opponents;
	private Planet playerPlanet;
	private Projectile p;

	@Override
	public void create () {
		batch = new SpriteBatch();
		
		//This is where we would get the user from Orbit and the opponent user from server
		this.player = new User("kyle", "p");
		this.opponents = new ArrayList<User>();
		this.opponents.add(new User("steven", "lol"));
		playerPlanet = player.getPlanet();
		playerPlanet.setPosition(100, 100);
		opponents.get(0).getPlanet().setPosition(400, 400);
		//Test projectile 
		p = new Projectile(100, 100, 253, 178, new Vector2(15, 15), 30f, new Rocket("Rocket", 0, 0, 0,
			0, null, new Texture("missile.png")));
		p.acceleration = new Vector2(0, -1f);
		

	}


	@Override
	public void render () {
		Gdx.gl.glClearColor(.03f, 0, .08f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		playerPlanet.update(1);
		p.update(1);
		
		batch.begin();
		//Draw player planet
		playerPlanet.draw(batch);
		p.draw(batch);
		//Draw opponent planets
		for(User o : opponents){
			o.getPlanet().draw(batch);
		}

		batch.end();
	}
}
