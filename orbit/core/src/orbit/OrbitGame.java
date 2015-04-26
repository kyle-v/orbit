package orbit;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;


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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JTextArea;

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
	private final float MIN_ASTEROID_GAP = 150;
	private final float MAX_ASTEROID_VELOCITY = 5;
	private final int lowerAsteroidAmount = 2;
	private final int upperAsteroidAmount = 4;
	
	private boolean gamePaused = false;
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private User player; 
	private ArrayList<User> opponents;
	private Planet playerPlanet;
	private FPSLogger fps;
	private List<GameObject> gameObjects;
	private List<GameObject> newGameObjects;

	GameClient gc;

	
	
	//Sprites for Weapon GUI
	Sprite[] weaponSprites;
	int weaponGUIX = 800;
	int weaponGUIY = 200;

	//Input control
	public int currentWeapon; // Weapon currently selected
	private float powerPercent; // VALUE FROM 0 to 100 percent of the weapons max power
	private double angle; // Angle to shoot the weapon at
	private boolean increasing = true; //Whether the value that is being set (angle or power ) is currently increasing or decreasing
	private boolean myturn;
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
	int numPlayers;
	
	//Asteroids
	ArrayList<Asteroid> asteroids;
	
	//Fonts are how we write text to the screen so that's what this is for
	BitmapFont writer;
	
	private Texture backgroundImage;
	
	private String gameOverText;
	
	public long randomSeed;
	
	private Socket socket;
	private ServerSocket serverSocket;
	
	boolean isConnected = false;
	boolean isHost;
	public boolean isGameOver = false;
	
	float player1Health = 100;
	float player2Health = 100;

	public OrbitGame(GameData gameData, int playerIndex){
		this.players = gameData.players;
		this.playerIndex = playerIndex;
		this.playerIPAddresses = gameData.ips;
		this.randomSeed = gameData.seed;
		this.numPlayers = players.size();
	}
	
	@Override
	public void create () {
		
		gameState = GameState.CONNECTING;
		//setup server stuff
		if (playerIndex == 0){
			setupServer();
			isHost = true;
			
		} else {
			connectToServer();
			isHost = false;
		}
		isConnected = true;
		
		//Initializing variables
		fps = new FPSLogger();
		batch = new SpriteBatch();
		//gameState = GameState.CONNECTING;
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
		Random randy = new Random(randomSeed);
		GameplayStatics.randy = randy;
		float start = 20;

		GameplayStatics.game = this;
		GameplayStatics.gameObjects = gameObjects;
		
		//setup game state
		currentPlayer = 0;
		
		//create all the planets
		for(int k=0;k<players.size();k++){
			User u = players.get(k);
			u.initialize();
			double radians = Math.toRadians(start + increment*k);
			Planet planet = u.getPlanet();
			planet.SetPosition(new Vector2((float)Math.cos(radians)*SPAWN_RADIUS, (float)Math.sin(radians)*SPAWN_RADIUS));
			planet.setDegreePosition((float)radians);
			gameObjects.add(planet);
		}
		
		
		
		playerPlanet = player.getPlanet();
		
		asteroids = new ArrayList<Asteroid>();
	
		//randomly spawn asteroids
		boolean spawnConflict;
		float asteroidSpawnRadius;
		double x;
		double y;
		
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
			asteroids.add(new Asteroid((float)x,(float)y,new Vector2(0,0),0));
		}
		
		for (Asteroid a: asteroids){
			gameObjects.add(a);
		}

		//this is a texture that gets displayed as the background image
		backgroundImage = AssetLibrary.getTexture("SpaceBackground.jpg");
		
		writer = new BitmapFont();
		writer.setColor(Color.YELLOW);
		writer.setScale(5);
		
		//weapon gui stuff
		weaponSprites = new Sprite[player.equippedWeapons.size()];
		for(int i = 0; i < weaponSprites.length; i++){
			weaponSprites[i] = new Sprite(AssetLibrary.getTexture(player.equippedWeapons.get(i).weaponFilename));
			weaponSprites[i].setPosition(weaponGUIX,weaponGUIY);
			weaponGUIY -= 100;
		}
	}

	public void updateGame(){
		if(newGameObjects != null){
			gameObjects = newGameObjects;
			newGameObjects = null;
		}
		
		
		float DeltaTime = Gdx.graphics.getDeltaTime();
		Gdx.gl.glClearColor(.03f, 0, .08f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//Update
		int numPlanet = 0;
		if(!gamePaused){
			world.step(1f/60f, 6, 2);
			for(int i = 0; i < gameObjects.size(); i++){
				GameObject o = gameObjects.get(i);
				if (!o.isDead){
					if(o.getName().equals("Planet")){
						if (numPlanet == 0){
							player1Health = ((Planet)o).health;
							players.get(0).getPlanet().health = player1Health;
							numPlanet++;
						} else {
							player2Health = ((Planet)o).health;
							players.get(1).getPlanet().health = player2Health;
						}
					}
					o.update(DeltaTime);
				} else {
					gameObjects.remove(o);
				}
			}
		}
		
		checkWinCondition();

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
				//player.setWeapon(currentWeapon);
				//player.fire((int)powerPercent, angle, gameObjects);
				playerTurnOver(powerPercent, (float)angle);

				gameState = GameState.WAITING;
				break;
			case WAITING: // Turn over, waiting for other player
//				if(currentPlayer == playerIndex)
//					gameState = GameState.WEAPON;
				//When opponent's turn is over move back to WEAPON state
				break;
			default:
				break;
		}
	
		draw();
	}



		
	
	public void draw(){
		
		//Drawing everything
		//tell it to draw from our current camera's point of view
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
				
		batch.draw(backgroundImage, -965, -650);
				
			for(GameObject o : gameObjects){
				o.draw(batch);
			}
		
		
		//weapon gui code
		for(int i = 0; i < weaponSprites.length; i++){
			weaponSprites[i].draw(batch);
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
		//weapon selection display
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.rect(weaponGUIX, 200 - currentWeapon * 100, 100, 100);
				
				
		shapeRenderer.end();
				
		debugRenderer.render(GameplayStatics.getWorld(), debugMatrix);
	}
	
	public void setupServer(){
		GameServer gameserver = new GameServer(basePort);
		gameserver.start();
		gc = new GameClient();
		gc.start();
		
	}
	
	public class GameServer extends Thread{
		
		private int gameSocket;
		private Vector<PeerThread> ptVector = new Vector<PeerThread>();
		private int numPlayersConnected = 0;
		private boolean playersConnected = false;
		
		public GameServer(int port){
			gameSocket = port;
		}
		
		public synchronized void sendupdatedGameObjects(List<GameObject> gameObjects) {
			for (PeerThread pt : ptVector) {
				pt.sendGameObjects(gameObjects);
			}
		}
		
		public void run(){
			ServerSocket ss = null;
			try {
				System.out.println("Starting Game Server");
				ss = new ServerSocket(gameSocket);
				while (true) {
					Socket s = ss.accept();
					System.out.println("Client " + s.getInetAddress() + ":" + s.getPort() + " connected");
					PeerThread pt = new PeerThread(s, this);
					ptVector.add(pt);
					pt.start();
					numPlayersConnected++;
					if (numPlayersConnected == numPlayers){
						playersConnected = true;
						System.out.println("All players connected!");
					}
				}
			} catch (IOException ioe) {
				System.out.println("IOE: " + ioe.getMessage());
			} finally {
				if (ss != null) {
					try {
						ss.close();
					} catch (IOException ioe) {
						System.out.println("IOE closing ServerSocket: " + ioe.getMessage());
					}
				}
			}
		}
	}

	public class PeerThread extends Thread{
		private ObjectInputStream ois;
		private ObjectOutputStream oos;
		private GameServer gs;
		private Socket socket;
		public boolean isDisconnected = false;
		public PeerThread(Socket s, GameServer gs){
			socket = s;
			this.gs = gs;
			try {
				ois = new ObjectInputStream(socket.getInputStream());
				oos = new ObjectOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void allPlayersConnected(boolean playersConnected){
			try {
				oos.reset();
				oos.writeBoolean(playersConnected);
				oos.flush();
			} catch (IOException e){
				e.printStackTrace();
			}
		}
		
		public void sendGameObjects(List<GameObject> gameObjects){
			try {
				oos.reset();
				oos.writeObject(gameObjects);
				oos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		public void run(){
			try{

				while(true){
					Vector3 fireInfo = (Vector3)ois.readObject();
					if(fireInfo != null){
						GameplayStatics.game.players.get(currentPlayer).setWeapon((int)fireInfo.z);
						GameplayStatics.game.players.get(currentPlayer).fire((int) fireInfo.x, fireInfo.y, GameplayStatics.game.gameObjects);
						gs.sendupdatedGameObjects(gameObjects);
						incrementToNextPlayer();
					}
				}
			} catch(IOException e){
				System.out.println("Terminated ClientListenerThread");
				isDisconnected = true;
			}	catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					ois.close();
					oos.close();
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		
		public String getAddress(){
			return ("/" + socket.getInetAddress() + ":" + socket.getPort());
		}
		public Socket getSocket(){
			return socket;
		}
	}
	
	public class GameClient extends Thread{

		private ObjectInputStream ois;
		private ObjectOutputStream oos;
		private Socket s;
		String ipAddress = playerIPAddresses.get(0);
		int portNumber = basePort;
		boolean isAlive = true;
		boolean isConnected = false;

		public GameClient(){

		}

		public void sendFireInfo(Vector3 fireInfo){

			try{	
				oos.reset();
				oos.writeObject(fireInfo);
				System.out.println("sending fire information to server");
				oos.flush();
			}catch(IOException ie){
				ie.printStackTrace();
			}
			
		}

		public void run() {
			try {	
				while(!isConnected){
					try{

						s = new Socket(ipAddress, portNumber);

						oos = new ObjectOutputStream(s.getOutputStream());
						ois = new ObjectInputStream(s.getInputStream());
//						while (gameState == GameState.CONNECTING){
//							isConnected = ois.readBoolean();
//							if (isConnected){
								if(isHost){
									gameState = GameState.WEAPON;
									myturn = true;
								} else {
									gameState = GameState.WAITING;
									myturn = false;
//								}
//							}
								}
						isConnected = true;
						
						System.out.println("Streams started");
						
					}catch(IOException ioe){
						this.sleep(3000);
						System.out.println("IOE Exception in GameClient main" + ioe.getMessage());
					}
				}
				while(isAlive){
						sleep(1000);
						System.out.println("waiting");
						GameplayStatics.game.newGameObjects = Collections.synchronizedList((List<GameObject>)ois.readObject());
						
						System.out.println("read objects");
						if(myturn == true){
							myturn = false;
							gameState = GameState.WAITING;
						}
						else if(myturn == false){
							myturn = true;
							gameState = GameState.WEAPON;
						}
				}
				}catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  catch (IOException e) { 
					e.printStackTrace();
					System.out.println("Disconnected from game server with IOException");
				}finally{
					try {
						if(ois != null)ois.close();
						if(oos != null)oos.close();
						s.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
		}
	}
	
	
	
	public void connectToServer(){
		boolean connected = false;
			gc = new GameClient();
			gc.start();
			//System.out.println("Connected to server!");
	}

	@Override
	public void render () {
		updateGame();
	}
	
	public class SpawnAsteroidThread extends Thread{
		long spawnTime;
		
		SpawnAsteroidThread(long spawnTime){
			this.spawnTime = spawnTime;
		}
		
		public void run(){
			try {
				sleep(spawnTime * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			spawnAsteroid();
			SpawnAsteroidThread thread = new SpawnAsteroidThread((long) (GameplayStatics.randy.nextFloat() * 10));
			thread.start();
		}
		
		public void spawnAsteroid(){
			Camera cam = GameplayStatics.game.camera;
			float height = cam.viewportHeight;
			float width = cam.viewportWidth;
			Random randy = GameplayStatics.randy;
			int topOrLeft = randy.nextInt(2);
			int negativeOrPositive = randy.nextInt(2);
			if(negativeOrPositive==0){
				negativeOrPositive = -1;
			}
			else{
				negativeOrPositive = 1;
			}
			float randY;
			float randX;
			
			if(topOrLeft == 0){
				randY = height * randy.nextFloat() - height/2;
				randX = (60 + width/2) * negativeOrPositive;
			}
			else{
				randY = (60 + height/2) * negativeOrPositive;
				randX =  width * randy.nextFloat() - width/2;
			}
			Vector2 vel = new Vector2(-randX, -randY).nor();
			vel = vel.scl(MAX_ASTEROID_VELOCITY);
			Asteroid a = new Asteroid(randX, randY, vel.X, vel.y);
			gameObjects.add(a);
		}
	}
		
	public void playerTurnOver(float powerPercent, float angle){
//		for(int k=0;k<players.size();k++){
//			if(k!=playerIndex){
//				SocketHints socketHint = new SocketHints();
//				socketHint.connectTimeout = 10000;
//				Socket socket = Gdx.net.newClientSocket(Protocol.TCP, playerIPAddresses.get(k), basePort + playerIndex, socketHint);
				//System.out.println("are we connected: " + socket.isConnected());
				//System.out.println("Sending object to port " + (basePort + k));
				//fire info is what we pass over the socket, we are giving power, angle and the weapon that the player is using
				Vector3 fireInfo = new Vector3(powerPercent,angle, players.get(currentPlayer).getPlanet().getWeaponIndex());
				gc.sendFireInfo(fireInfo);
				//gameState = GameState.WAITING;
				//try {
					//ObjectOutputStream stream = new ObjectOutputStream(socket.getOutputStream());
					//stream.writeObject(fireInfo);
					//stream.flush();
					
				//} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				//}
//			}
//		}
	}
	
	public void incrementToNextPlayer(){
		currentPlayer ++;
		currentPlayer %= players.size();	
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
		isGameOver = true;
	}
	
	public void reportLoss(){
		gameOverText = "You Lost, click x in upper right";
		writer.setScale(3);
		gameState = GameState.GAMEOVER;
		writer.setColor(Color.YELLOW);
		isGameOver = true;
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
