package orbit;

import orbit.OrbitGame.GameState;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;

public class InputController extends InputAdapter{
	
	private OrbitGame game;
	
	public InputController(OrbitGame og){
		game = og;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Keys.SPACE){
			switch(game.gameState){
			case WEAPON: //Start of turn  - choosing weapon 
				//If space bar is pressed - go to AIMING state
				game.gameState = GameState.AIMING;
				System.out.println("Begin AIMING state");
				break;
			case AIMING: //Choosing angle to shoot at - oscillates back and forth - spacebar to stop it
				game.gameState = GameState.POWER;
				System.out.println("Begin POWER state");
				break;
			case POWER: //Choosing power to shoot
				game.gameState = GameState.FIRE;
				//AssetLibrary.playSound("explode.wav");
				//System.out.println("Fired with angle: " + angle + " and power " + powerPercent + "%");
				System.out.println("Begin WAITING state");
				break;
			case WAITING: // Turn over, waiting for other player
				//game.gameState = GameState.WEAPON;
				//System.out.println("Begin WEAPON state");
				//When opponent's turn is over move back to WEAPON state
				break;
			default:
				break;

			}
		}		
		return false;

	}

}
