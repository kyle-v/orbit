package orbit;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.Input.Keys;

public class OrbitGame extends ApplicationAdapter{


	//Game goes through 4 states: Choosing weapon, choosing angle, choosing power, and waiting for the other player's move.
	public static enum GameState {WEAPON, AIMING, POWER, WAITING};
	public GameState gameState; 

	private boolean gamePaused = false;
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private User player; 
	private ArrayList<User> opponents;
	private Planet playerPlanet;
	private FPSLogger fps;
	private ArrayList<GameObject> gameObjects;
	private ArrayList<Projectile> projectiles;


	//Input control
	private int powerPercent; // VALUE FROM 0 to 100 percent of the weapons max power
	private double angle; // Angle to shoot the weapon at
	private boolean increasing = true; //Whether the value that is being set (angle or power ) is currently increasing or decreasing
	private double maxAngle = Math.PI/2;
	private double minAngle = 0;



	@Override
	public void create () {
		
		//Initializing variables
		//
		fps = new FPSLogger();
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		gameState = GameState.WEAPON;
		gameObjects = new ArrayList<GameObject>();
		projectiles = new ArrayList<Projectile>();
		
		//Input
		Gdx.input.setInputProcessor(new InputController(this));

		
		
		//This is where we would get the user from Orbit and the opponent user from server
		this.player = new User("kyle", "p");
		this.opponents = new ArrayList<User>();
		this.opponents.add(new User("steven", "lol"));
		playerPlanet = player.getPlanet();
		playerPlanet.setPosition(100, 100);
		opponents.get(0).getPlanet().setPosition(400, 400);
		
		gameObjects.add(playerPlanet);
		gameObjects.add(opponents.get(0).getPlanet());
		
		System.out.println("made the game");

	}


	@Override
	public void render () {
		//fps.log();
		Gdx.gl.glClearColor(.03f, 0, .08f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//Update
		shapeRenderer.begin(ShapeType.Filled); 
		if(!gamePaused){
			
			for(GameObject o : gameObjects){
				shapeRenderer.rect(o.bounds.x,o.bounds.y,o.bounds.width,o.bounds.height);
				o.update();
				for (Projectile p : projectiles){ //checks if any of the projectiles hit the GameObject
					o.checkCollision(p);
					p.checkCollision(o);
				}
			}
			
			for (int i = 0; i < projectiles.size(); i++){ //goes through the projectiles, updates them, and checks if they are destroyed
				Projectile p = projectiles.get(i);
				shapeRenderer.rect(p.bounds.x,p.bounds.y,p.bounds.width,p.bounds.height);
				p.update();
				if (p.isDead()){
					projectiles.remove(p);
				}
			}
		}
		shapeRenderer.end();


		//Game loop
		switch(gameState){
		case WEAPON: //Start of turn  - choosing weapon 

			break;
		case AIMING: //Choosing angle to shoot at - oscillates back and forth - spacebar to stop it
			if(increasing) angle += .01f;
			else angle -= .01f;
			if (angle >= maxAngle) increasing = false;
			if (angle <= minAngle) increasing = true;

			break;
		case POWER: //Choosing power to shoot
			if(increasing) powerPercent ++;
			else powerPercent --;
			if (powerPercent == 100) increasing = false;
			if (powerPercent == 0) increasing = true;
			break;
		case WAITING: // Turn over, waiting for other player
			player.equippedWeapons.get(0).fire(powerPercent, angle, gameObjects, projectiles);
			
			gameState = GameState.WEAPON;
			System.out.println("Begin WEAPON state");

			//When opponent's turn is over move back to WEAPON state
			break;
		default:
			break;
		}



		//Drawing everything

		batch.begin();
		//Draw player planet

		//Draw opponent planets
		for(GameObject o : gameObjects){
			o.draw(batch);
		}
		
		for(Projectile p : projectiles){
			p.draw(batch);
		}

		batch.end();

		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(1, 1, 0, 1);
		shapeRenderer.rect(10, 10, 10, powerPercent);
		shapeRenderer.setColor(1, 1, 0, 1);
		shapeRenderer.line(20f, 10f, 20+(float)Math.cos(angle) * 100 , 10+(float)Math.sin(angle) * 100 );
		shapeRenderer.end();



	}


}
