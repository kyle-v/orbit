package orbit;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Net.Protocol;

public class OrbitGame extends ApplicationAdapter{


	//Game goes through 4 states: Choosing weapon, choosing angle, choosing power, and waiting for the other player's move.
	public static enum GameState {WEAPON, AIMING, POWER, FIRE, WAITING};
	public GameState gameState; 

	private final float SPAWN_RADIUS = 400;
	
	private boolean gamePaused = false;
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private User player; 
	private ArrayList<User> opponents;
	private Planet playerPlanet;
	private FPSLogger fps;
	private List<GameObject> gameObjects;

	//Input control
	public int currentWeapon; // Weapon currently selected
	private float powerPercent; // VALUE FROM 0 to 100 percent of the weapons max power
	private double angle; // Angle to shoot the weapon at
	private boolean increasing = true; //Whether the value that is being set (angle or power ) is currently increasing or decreasing
	World world;
	Box2DDebugRenderer debugRenderer;
	Camera camera;
	//this magical stuff that converts our pixels coordinates to our box2d world coordinates
	Matrix4 debugMatrix;
	
	//ok im adding stuff that should allow this to actually scale for players
	//an array of all players that will be in the game
	ArrayList<User> players;
	ArrayList<String> playerIPAddresses;
	int basePort = 9020;
	//int that indicates which index we are playing as
	int playerIndex;
	int currentPlayer;

	public OrbitGame(ArrayList<User> players, ArrayList<String> ipaddresses, int playerIndex){
		this.players = players;
		this.playerIndex = playerIndex;
		playerIPAddresses = ipaddresses;
	}
	
	@Override
	public void create () {
		
		//Initializing variables
		//
		fps = new FPSLogger();
		batch = new SpriteBatch();
		gameState = GameState.WEAPON;
		gameObjects = Collections.synchronizedList(new ArrayList<GameObject>());
		
		//Input
		Gdx.input.setInputProcessor(new InputController(this));

		//create a world for the physics simulation
		world = GameplayStatics.getWorld();
		//create a camera that renders our world, the size here isn't how large the frame is, its basically just an aspect ratio
		camera = new OrthographicCamera(1920,1080);
		//this will be used to render where our colliders are
		debugRenderer = new Box2DDebugRenderer();
		debugMatrix=new Matrix4(camera.combined);
		 
		//this thing renders shapes, duh
		shapeRenderer = new ShapeRenderer();
		//setting the projection matrix means its rendering based on the camera's pixels
		//rather then the screen's
		shapeRenderer.setProjectionMatrix(camera.combined); 
		GameplayStatics.setShapeRenderer(shapeRenderer);
		//BoxObjectManager.BOX_TO_WORLD = 100f
		//Scale it by 100 as our box physics bodies are scaled down by 100
		debugMatrix.scale(GameplayStatics.pixelsToMeters(), GameplayStatics.pixelsToMeters(), 1f);
		 
		
		//This is where we would get the user from Orbit and the opponent user from server
		
		player = players.get(playerIndex);
		float increment = 360/players.size();
		Random randy = new Random();
		float start = 20;
		

		GameplayStatics.game = this;
		
		//setup game state
		currentPlayer = 0;
		if(currentPlayer != 0)
			gameState = GameState.WAITING;
		
		for(int k=0;k<players.size();k++){
			User u = players.get(k);
			u.initialize();
			double radians = Math.toRadians(start + increment*k);
			u.getPlanet().SetPosition(new Vector2((float)Math.cos(radians)*SPAWN_RADIUS, (float)Math.sin(radians)*SPAWN_RADIUS));
			gameObjects.add(u.getPlanet());
			if(k!=playerIndex){ 
				NetworkingListenerThread thread = new NetworkingListenerThread(basePort + k);
				thread.start();
			}
		}
		playerPlanet = player.getPlanet();
		
		
	}


	@Override
	public void render () {
		//fps.log();
		float DeltaTime = Gdx.graphics.getDeltaTime();
		Gdx.gl.glClearColor(.03f, 0, .08f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		world.step(1f/60f, 6, 2);
		//Update
		if(!gamePaused){
			for(int i = 0; i < gameObjects.size(); i++){
				GameObject o = gameObjects.get(i);
				if (!o.isDead){
					//shapeRenderer.rect(o.bounds.x,o.bounds.y,o.bounds.width,o.bounds.height);
					o.update(DeltaTime);
				} else {
					o.Destroy();
					gameObjects.remove(o);
				}
			}
		}

		//Game loop
		switch(gameState){
		case WEAPON: //Start of turn  - choosing weapon
			if (currentWeapon < 0){
				currentWeapon = player.equippedWeapons.size() - 1;
			} else if (currentWeapon > player.equippedWeapons.size() - 1){
				currentWeapon = 0;
			}
			break;
		case AIMING: //Choosing angle to shoot at - oscillates back and forth - spacebar to stop it
			angle += 3 * DeltaTime;
			if(angle > 2 * Math.PI)
				angle -= 2* Math.PI;
			break;
		case POWER: //Choosing power to shoot
			if(increasing) powerPercent += 50 * DeltaTime;
			else powerPercent -= 50 * DeltaTime;
			if (powerPercent > 100) increasing = false;
			if (powerPercent < 0) increasing = true;
			break;
		case FIRE:
			player.setWeapon(currentWeapon);
			player.fire((int)powerPercent, angle, gameObjects);
			gameState = GameState.WAITING;
			currentPlayer ++;
			if(currentPlayer > players.size()){
				currentPlayer = 0;
			}
			playerTurnOver(powerPercent, (float)angle);
			break;
		case WAITING: // Turn over, waiting for other player
			if(currentPlayer == playerIndex)
				gameState = GameState.WEAPON;
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
		//shitty weapon gui code
		int weaponGUIX = 800;
		int weaponGUIY = 200;
		for(int i = 0; i < player.equippedWeapons.size(); i++){
			player.equippedWeapons.get(i).sprite.setPosition(weaponGUIX,weaponGUIY);
			player.equippedWeapons.get(i).sprite.draw(batch);
			weaponGUIY -= 100;
		}
		batch.end();

		shapeRenderer.begin(ShapeType.Filled);
		for(User u : players){
			Planet p = u.getPlanet();
			if(p.health>0){
				Vector2 pos = p.position;
				shapeRenderer.setColor(Color.RED);
				shapeRenderer.rect(pos.x - 50,pos.y + 80, p.health, 10);
			}
		}
		shapeRenderer.setColor(1, 1, 0, 1);
		Vector2 playerPos = playerPlanet.position;
		shapeRenderer.rect(playerPos.x - 80, playerPos.y, 10, powerPercent);
		shapeRenderer.setColor(1, 1, 0, 1);
		shapeRenderer.line(playerPos.x, playerPos.y, playerPos.x+(float)Math.cos(angle) * 100 ,  playerPos.y+(float)Math.sin(angle) * 100 );
		shapeRenderer.end();
		//shitty weapon selection display
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.rect(weaponGUIX, 200 - currentWeapon * 100, 100, 100);
		
		
		shapeRenderer.end();
		
		debugRenderer.render(GameplayStatics.getWorld(), debugMatrix);
	}

	public class NetworkingListenerThread extends Thread{
		int port;
		
		public NetworkingListenerThread(int port){
			this.port = port;
		}
		
		public void run(){
			System.out.println("Port opened on port " + port);
			ServerSocketHints socketHint = new ServerSocketHints();
			socketHint.acceptTimeout = 0;
			ServerSocket serverSocket = Gdx.net.newServerSocket(Protocol.TCP, port, socketHint);
			while(true){
				Socket socket = serverSocket.accept(null);
				try {
					ObjectInputStream reader = new ObjectInputStream(socket.getInputStream());
					Object obj = reader.readObject();
					if(currentPlayer >= players.size())
						currentPlayer = 0;
					 Vector2 fireInfo = (Vector2)obj;
					 GameplayStatics.game.players.get(currentPlayer).fire((int) fireInfo.x, fireInfo.y, GameplayStatics.game.gameObjects);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void playerTurnOver(float powerPercent, float angle){
		currentPlayer++;
		for(int k=0;k<players.size();k++){
			if(k!=playerIndex){
				SocketHints socketHint = new SocketHints();
				socketHint.connectTimeout = 3000;
				Socket socket = Gdx.net.newClientSocket(Protocol.TCP, playerIPAddresses.get(k), basePort + playerIndex, socketHint);
				System.out.println("Sending object to port " + (basePort + k));
				Vector2 fireInfo = new Vector2(powerPercent,angle);
				try {
					ObjectOutputStream stream = new ObjectOutputStream(socket.getOutputStream());
					stream.writeObject(fireInfo);
					stream.flush();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void checkWinCondition(){
		int possibleWinnerIndex = -1;
		for(int k=0;k<players.size();k++){
			User u = players.get(k);
			Planet p = u.getPlanet();
			if(p.health>0){
				if(possibleWinnerIndex == -1)
					possibleWinnerIndex = k;
				else
					return;
			}	
		}
		reportWinsAndLosses(possibleWinnerIndex);
	}
	
	public void reportWinsAndLosses(int winner){
		System.out.println(winner);
	}

}
