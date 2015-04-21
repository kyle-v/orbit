package orbit;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
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


	//Input control
	private int powerPercent; // VALUE FROM 0 to 100 percent of the weapons max power
	private double angle; // Angle to shoot the weapon at
	private boolean increasing = true; //Whether the value that is being set (angle or power ) is currently increasing or decreasing
	private double maxAngle = Math.PI/2;
	private double minAngle = 0;
	World world;
	Box2DDebugRenderer debugRenderer;
	Camera camera;

	@Override
	public void create () {
		
		//Initializing variables
		//
		fps = new FPSLogger();
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		gameState = GameState.WEAPON;
		gameObjects = new ArrayList<GameObject>();
		
		//Input
		Gdx.input.setInputProcessor(new InputController(this));

		//create a world for the physics simulation
		world = GameplayStatics.getWorld();
		//create a camera that renders our world, the size here isn't how large the frame is, its basically just an aspect ratio
		camera = new OrthographicCamera(1920,1080);
		//this will be used to render where our colliders are
		debugRenderer = new Box2DDebugRenderer();
		
		
		//This is where we would get the user from Orbit and the opponent user from server
		this.player = new User("kyle", "p");
		this.opponents = new ArrayList<User>();
		this.opponents.add(new User("steven", "lol"));
		playerPlanet = player.getPlanet();
		playerPlanet.SetPosition(new Vector2(-400, -400));
		opponents.get(0).getPlanet().SetPosition(new Vector2(400, 400));
		
		gameObjects.add(playerPlanet);
		gameObjects.add(opponents.get(0).getPlanet());
		
		
	}


	@Override
	public void render () {
		//fps.log();
		Gdx.gl.glClearColor(.03f, 0, .08f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		debugRenderer.render(GameplayStatics.getWorld(), camera.combined);
		world.step(1f/60f, 6, 2);
		//Update
		shapeRenderer.begin(ShapeType.Filled); 
		if(!gamePaused){
			
			for(int i = 0; i < gameObjects.size(); i++){
				GameObject o = gameObjects.get(i);
				if (!o.isDead){
					//shapeRenderer.rect(o.bounds.x,o.bounds.y,o.bounds.width,o.bounds.height);
					o.update();
				} else {
					gameObjects.remove(o);
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
			player.equippedWeapons.get(0).fire(powerPercent, angle, gameObjects);
			
			gameState = GameState.WEAPON;
			System.out.println("Begin WEAPON state");

			//When opponent's turn is over move back to WEAPON state
			break;
		default:
			break;
		}



		//Drawing everything
		//tell it to draw from our current camera's point of view
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		for(GameObject o : gameObjects){
			o.draw(batch);
		}

		batch.end();

		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(1, 1, 0, 1);
		shapeRenderer.rect(10, 10, 10, powerPercent);
		shapeRenderer.setColor(1, 1, 0, 1);
		shapeRenderer.line(20, 20, 20+(float)Math.cos(angle) * 100 ,  10+(float)Math.sin(angle) * 100 );
		shapeRenderer.end();



	}


}
