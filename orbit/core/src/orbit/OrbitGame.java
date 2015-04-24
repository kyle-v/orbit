package orbit;

import java.awt.Font;
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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Net.Protocol;

public class OrbitGame extends ApplicationAdapter{


	//Game goes through 4 states: Choosing weapon, choosing angle, choosing power, and waiting for the other player's move.
	public static enum GameState {WEAPON, AIMING, POWER, FIRE, WAITING, CONNECTING, GAMEOVER};
	public GameState gameState; 

	private final float SPAWN_RADIUS = 400;
	

	//messing with this might cause an infinite loop just fyi
	private final float MAX_ASTEROID_SPAWN_RADIUS = 175;
	private final float MIN_ASTEROID_GAP = 100;

	private final int lowerAsteroidAmount = 3;
	private final int upperAsteroidAmount = 5;
	
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
	
	//Asteroids
	ArrayList<Asteroid> asteroids;
	
	//Fonts are how we write text to the screen so that's what this is for
	BitmapFont writer;
	
	//booleans to check when everyone has connected
	boolean[] connectionChecks;
	
	Socket[] playerSockets;
	
	private Texture backgroundImage;
	
	private String gameOverText;


	public OrbitGame(ArrayList<User> players, ArrayList<String> ipaddresses, int playerIndex){
		this.players = players;
		this.playerIndex = playerIndex;
		playerIPAddresses = ipaddresses;
	}
	
	@Override
	public void create () {
		
		//Initializing variables
		connectionChecks = new boolean[] {false, false, false ,false};
		connectionChecks[playerIndex] = true;
		fps = new FPSLogger();
		batch = new SpriteBatch();
		gameState = GameState.CONNECTING;
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
		
		
		playerSockets = new Socket[4];
		
		//create all the planets
		for(int k=0;k<players.size();k++){
			User u = players.get(k);
			u.initialize();
			double radians = Math.toRadians(start + increment*k);
			u.getPlanet().SetPosition(new Vector2((float)Math.cos(radians)*SPAWN_RADIUS, (float)Math.sin(radians)*SPAWN_RADIUS));
			u.getPlanet().setDegreePosition((float)radians);
			gameObjects.add(u.getPlanet());
			if(k!=playerIndex){ 
				NetworkingListenerThread thread = new NetworkingListenerThread(k);
				thread.start();
			}
		}
		//we do this in two seperate for loops because I want all the sockets being listened to before anyone starts sending messages to them
		/*for(int k=0;k<players.size();k++){
			SocketHints socketHint = new SocketHints();
			socketHint.connectTimeout = 10000;
			//establish connection with each player and continue pinging until we do so
			while(true){
				try{
					Socket socket = Gdx.net.newClientSocket(Protocol.TCP, playerIPAddresses.get(k), basePort + playerIndex, socketHint);
					//fire info is what we pass over the socket, we are giving power, angle and the weapon that the player is using
					try {
						ObjectOutputStream stream = new ObjectOutputStream(socket.getOutputStream());
						stream.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					socket.dispose();
					break;
				}
				catch(GdxRuntimeException ex){
					continue;
				}
			}
		}*/
		playerPlanet = player.getPlanet();
		
		asteroids = new ArrayList<Asteroid>();
	
		//randomly spawn asteroids
		boolean spawnConflict;
		float asteroidSpawnRadius;
		double x;
		double y;
		/*
		int numAsteroids = randy.nextInt(upperAsteroidAmount - lowerAsteroidAmount + 1) + lowerAsteroidAmount;
		for (int i = 0; i < numAsteroids; i++){
			do {
				spawnConflict = false;
				double radians = randy.nextDouble() * (360 + 1); //get a random angle
				asteroidSpawnRadius = randy.nextFloat() * (MAX_ASTEROID_SPAWN_RADIUS + 1); //get a random distance from center
				x = Math.cos(radians)*asteroidSpawnRadius;
				y = Math.sin(radians)*asteroidSpawnRadius;
				for (Asteroid asteroid : asteroids){ //loop for checking other asteroids
					if (x == asteroid.position.x && y == asteroid.position.y){ //makes sure position is not the same as other asteroids
						spawnConflict = true;
						break;
					}
					//checks if distance of asteroids are bigger than MIN_ASTEROID_GAP; makes sure asteroids don't spawn too close together
					if (Math.hypot(x - asteroid.position.x,y - asteroid.position.y) < MIN_ASTEROID_GAP){
						spawnConflict = true;
						break;
					}
				}
			} while (spawnConflict);
			System.out.println("added asteroid");
			asteroids.add(new Asteroid((float)x,(float)y,new Vector2(0,0),0));
		}
		*/
		for (Asteroid a: asteroids){
			gameObjects.add(a);
		}

		//this is a texture that gets displayed as the background image
		backgroundImage = AssetLibrary.getTexture("SpaceBackground.jpg");
		
		writer = new BitmapFont();
		writer.setColor(Color.YELLOW);
		writer.setScale(5);
		//new GameOverDialog("test", "test", new Skin(Gdx.files.internal("uiskin.atlas")));
	}

	public void updateGame(){
		float DeltaTime = Gdx.graphics.getDeltaTime();
		Gdx.gl.glClearColor(.03f, 0, .08f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//Update
		if(!gamePaused){
			world.step(1f/60f, 6, 2);
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
			incrementToNextPlayer();
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
		
		batch.draw(backgroundImage, -965, -650);
		
		for(GameObject o : gameObjects){
			o.draw(batch);
		}
		//weapon gui code
		int weaponGUIX = 800;
		int weaponGUIY = 200;
		for(int i = 0; i < player.equippedWeapons.size(); i++){
			player.equippedWeapons.get(i).sprite.setPosition(weaponGUIX,weaponGUIY);
			player.equippedWeapons.get(i).sprite.draw(batch);
			weaponGUIY -= 100;
		}
		if(gameState == GameState.GAMEOVER){
			TextBounds bound = writer.getBounds(gameOverText);
			writer.draw(batch, gameOverText, -bound.width/2, -bound.height/2);
		}
		batch.end();

		//display health bars
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
	
	public void waitForConnect(){
		for(int k=0;k<players.size();k++){
			if(k!=playerIndex){
				SocketHints socketHint = new SocketHints();
				socketHint.connectTimeout = 1000;
				//establish connection with each player and continue pinging until we do so
				try{
					Socket socket = Gdx.net.newClientSocket(Protocol.TCP, playerIPAddresses.get(k), basePort + playerIndex, socketHint);
					try {
						ObjectOutputStream stream = new ObjectOutputStream(socket.getOutputStream());
						stream.writeObject(playerIndex);
						stream.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					socket.dispose();
					break;
				}
				catch(GdxRuntimeException ex){
				}
			}
		}
		

		Gdx.gl.glClearColor(.03f, 0, .08f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		Random randy = new Random();
		writer.setColor(new Color(randy.nextFloat(),randy.nextFloat(),randy.nextFloat(),1));
		TextBounds bound = writer.getBounds("Waiting for players to connect");
		writer.draw(batch, "Waiting for players to connect", -bound.width/2, -bound.height/2);
		batch.end();
		
		for(int k=0;k<players.size();k++){
			if(!connectionChecks[k])
				return;
		}
		if(playerIndex == 0)
			gameState = GameState.WEAPON;
		else
			gameState = GameState.WAITING;
		
	}

	@Override
	public void render () {
		//fps.log();
		
		if(gameState == GameState.CONNECTING){
			waitForConnect();
		}
		else{
			updateGame();
		}
	}

	public class NetworkingListenerThread extends Thread{
		int port;
		int playerIndex;
		
		public NetworkingListenerThread(int port){
			this.port = GameplayStatics.game.basePort + port;
			playerIndex = port;
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
					if(obj instanceof Vector3){
						Vector3 fireInfo = (Vector3)obj;
						//go to our current player and set their weapon to the one that the other player fired with
						GameplayStatics.game.players.get(currentPlayer).setWeapon((int)fireInfo.z);
						GameplayStatics.game.players.get(currentPlayer).fire((int) fireInfo.x, fireInfo.y, GameplayStatics.game.gameObjects);
						incrementToNextPlayer();
					}
					else {
						Integer id = (Integer)obj;
						connectionChecks[id] = true;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					break;
					//e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void playerTurnOver(float powerPercent, float angle){
		for(int k=0;k<players.size();k++){
			if(k!=playerIndex){
				SocketHints socketHint = new SocketHints();
				socketHint.connectTimeout = 10000;
				Socket socket = Gdx.net.newClientSocket(Protocol.TCP, playerIPAddresses.get(k), basePort + playerIndex, socketHint);
				System.out.println("are we connectd: " + socket.isConnected());
				System.out.println("Sending object to port " + (basePort + k));
				//fire info is what we pass over the socket, we are giving power, angle and the weapon that the player is using
				Vector3 fireInfo = new Vector3(powerPercent,angle, players.get(currentPlayer).getPlanet().getWeaponIndex());
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
	
	public void incrementToNextPlayer(){
		currentPlayer ++;
		if(currentPlayer >= players.size()){
			currentPlayer = 0;
		}
		if(!players.get(currentPlayer).isPlaying)
			incrementToNextPlayer();
	}
	
	public void checkWinCondition(){
		int possibleWinnerIndex = -1;
		for(int k=0;k<players.size();k++){
			User u = players.get(k);
			Planet p = u.getPlanet();
			if(p.health>0){
				if(possibleWinnerIndex == -1)
					possibleWinnerIndex = k;
				else{
					return;
				}
			}
			else{
				players.get(k).isPlaying = false;
				if(k == playerIndex){
					reportLoss();
					return;
				}
			}
		}
		reportWin();
	}
	
	public void reportWin(){
		gameOverText = "You Won! Click x in upper right";
		writer.setScale(3);
		gameState = GameState.GAMEOVER;
		writer.setColor(Color.YELLOW);
	}
	
	public void reportLoss(){
		gameOverText = "You Lost, click x in upper right";
		writer.setScale(3);
		gameState = GameState.GAMEOVER;
		writer.setColor(Color.YELLOW);
	}
	
	public class GameOverDialog extends Dialog{

		public GameOverDialog(String title,String text, Skin skin) {
			super(title, skin);
			this.text(text);
			Button button = new Button(skin, "Quit"){
				protected void result(Object object){
					Gdx.app.exit();
				}
			};
			this.button(button);
		}
	}

}
