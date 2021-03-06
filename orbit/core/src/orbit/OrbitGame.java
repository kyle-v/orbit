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
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;





import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
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
	private final float MAX_ASTEROID_SPAWN_RADIUS = 700;
	private final float MIN_ASTEROID_GAP = 300;
	private final float MAX_ASTEROID_VELOCITY = 5;
	private final int lowerAsteroidAmount = 5;
	private final int upperAsteroidAmount = 8;
	
	private boolean gamePaused = false;
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private User player; 
	private ArrayList<User> opponents;
	private Planet playerPlanet;
	private FPSLogger fps;
	private Vector<GameObject> gameObjects;
	private Vector<GameObject> newGameObjects;

	GameClient gc;

	
	
	//Sprites for Weapon GUI
	Sprite[] weaponSprites;
	int weaponGUIX = 800;
	int weaponGUIY = 200;
	
	Sprite arrow;

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
	public ArrayList<User> players;
	ArrayList<String> playerIPAddresses;
	int basePort = 9020;
	//int that indicates which index we are playing as
	public int playerIndex;
	int currentPlayer;
	int numPlayers;
	public boolean win = false;
	
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
	
	float player1Health;
	float player2Health;
	
	private static HashMap<String, String> themeSongs;
	static
    {
        themeSongs = new HashMap<String, String>();
        themeSongs.put("planets/miller.png", "imperial.wav");
        themeSongs.put("planets/classicford.png", "mygirl.wav");
        themeSongs.put("planets/deathstar.png", "starwars.wav");
        themeSongs.put("planets/earth.png", "starwars.wav");
        themeSongs.put("planets/moon.png", "starwars.wav");
        themeSongs.put("planets/neptune.png", "starwars.wav");
        themeSongs.put("planets/worker.png", "ymca.wav");
        themeSongs.put("planets/gangsterford.png", "howwedo.wav");
        themeSongs.put("planets/mars.png", "starwars.wav");
    }
	
	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	public OrbitGame(GameData gameData, int playerIndex, ObjectOutputStream oos, ObjectInputStream ois){
		this.players = gameData.players;
		this.playerIndex = playerIndex;
		this.playerIPAddresses = gameData.ips;
		this.randomSeed = gameData.seed;
		this.numPlayers = players.size();
		this.basePort = gameData.baseport;
		this.oos = oos;
		this.ois = ois;
	}
	
	@Override
	public void create () {

		System.out.println(themeSongs.get(players.get(playerIndex).planetPath));
		Sound themeSound = AssetLibrary.getSound(themeSongs.get(players.get(playerIndex).planetPath));
		if(themeSound != null)
			themeSound.play();
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
		gameObjects = new Vector<GameObject>();
		
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
		
		player1Health = 100;
		player2Health = 100;
		
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
				for (User u : players){
					if (x == u.getPlanet().position.x && y == u.getPlanet().position.y){ //makes sure position is not the same as other asteroids
						spawnConflict = true;
						break;
					}
					//checks if distance of asteroids are bigger than MIN_ASTEROID_GAP; makes sure asteroids don't spawn too close together
					if (Math.hypot(x - u.getPlanet().position.x,y - u.getPlanet().position.y) < MIN_ASTEROID_GAP){
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
		arrow = new Sprite(AssetLibrary.getTexture("misc/arrow.png"));
		weaponSprites = new Sprite[player.equippedWeapons.size()];
		for(int i = 0; i < weaponSprites.length; i++){
			weaponSprites[i] = new Sprite(AssetLibrary.getTexture(player.equippedWeapons.get(i).weaponFilename));
			weaponSprites[i].setPosition(weaponGUIX,weaponGUIY);
			weaponGUIY -= 100;
		}
	}

	public void updateGame(){
		float DeltaTime = Gdx.graphics.getDeltaTime();
		Gdx.gl.glClearColor(.03f, 0, .08f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		synchronized(gameObjects){
			if(newGameObjects != null){
				gameObjects = newGameObjects;
				newGameObjects = null;
			}
			
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
				player.setWeapon(currentWeapon);
				//player.fire((int)powerPercent, angle, gameObjects);
				playerTurnOver(powerPercent, (float)angle);
				gameState = GameState.WAITING;
				powerPercent = 0;

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
			
		synchronized(gameObjects){
			for(GameObject o : gameObjects){
				o.draw(batch);
			}
		}
		
		//weapon gui code
		for(int i = 0; i < weaponSprites.length; i++){
			weaponSprites[i].draw(batch);
		}
		
		
		arrow.setPosition(weaponGUIX - 90, 200 - currentWeapon * 100);
		arrow.draw(batch);
		
		if(gameState == GameState.GAMEOVER){
			TextBounds bound = writer.getBounds(gameOverText);
			writer.draw(batch, gameOverText, -bound.width/2, -bound.height/2);
		} else {
			String whichuser = "You are Player " + (playerIndex+1);
			writer.draw(batch,whichuser, -800,-400);
			if(myturn){
				String currentUserText = "Your Turn";
				writer.draw(batch, currentUserText, 400, -400);
			}
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
		
		public synchronized void sendupdatedGameObjects(Vector<GameObject> gameObjects) {
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
		
		public void sendGameObjects(Vector<GameObject> gameObjects){
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
								if(isHost){
									gameState = GameState.WEAPON;
									myturn = true;
								} else {
									gameState = GameState.WAITING;
									myturn = false;
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
						GameplayStatics.game.newGameObjects =(Vector<GameObject>)ois.readObject();
						
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
			System.out.println("Asteroid created");
			SpawnAsteroidThread thread = new SpawnAsteroidThread((long) (GameplayStatics.randy.nextFloat() * 10));
			thread.start();
		}
		
		public void spawnAsteroid(){
			float height = 1000;
			float width = 700;
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
			newGameObjects.add(a);
		}
	}
		
	public void playerTurnOver(float powerPercent, float angle){
				Vector3 fireInfo = new Vector3(powerPercent,angle, currentWeapon);
				gc.sendFireInfo(fireInfo);
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
		if (isGameOver == false){
			WeaponGenerator weaponGenerator = new WeaponGenerator();
			Weapon newWeapon = weaponGenerator.makeWeapon();
			players.get(playerIndex).addWeapon(newWeapon);
			players.get(playerIndex).addMoney(100);
			Object serverResponse = sendRequest(new ServerRequest("Update User",players.get(playerIndex)));
			gameOverText = "You Won! Your reward is \n" + newWeapon.getName() + " and 100 coins";
			writer.setScale(3);
			gameState = GameState.GAMEOVER;
			writer.setColor(Color.YELLOW);
		}
		isGameOver = true;
		win = true;
		gc.isAlive = false;
	}
	
	public void reportLoss(){
		gameOverText = "You Lost, click x in upper right";
		writer.setScale(3);
		gameState = GameState.GAMEOVER;
		writer.setColor(Color.YELLOW);
		isGameOver = true;
		win = false;
		gc.isAlive = false;
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
	
	public Object sendRequest(ServerRequest sr){
		Object response = null;
		synchronized(oos){
		try {
			//System.out.println("Sending ServerRequest...");
			oos.reset();
			oos.writeObject(sr);
			oos.flush();
			//System.out.println("ServerRequest sent. Waiting for response...");
			response = ois.readObject();
			//System.out.println("Got response. Returned.");
		} catch (IOException e) { e.printStackTrace();
		} catch (ClassNotFoundException e) { e.printStackTrace();
		}
		return response;
	}
	}
}
