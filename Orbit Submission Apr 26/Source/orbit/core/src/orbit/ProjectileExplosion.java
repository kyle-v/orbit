package orbit;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class ProjectileExplosion extends GameObject {

	private static final int FRAME_COLS = 5;
	private static final int FRAME_ROWS = 5;
	
	Animation explosionAnimation;
	Texture explosionSheet;
	TextureRegion[] explosionFrames;
	TextureRegion currentFrame;
	float stateTime;
	
	public ProjectileExplosion(float x, float y, float width, float height) {
		super(x, y, width, height);
		explosionSheet = AssetLibrary.getTexture("spritesheets/ProjectileExplosion.png");
		TextureRegion[][] temp = TextureRegion.split(explosionSheet, explosionSheet.getWidth()/FRAME_COLS, explosionSheet.getHeight()/FRAME_ROWS);
		explosionFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
		int index = 0;
		for(int k=0;k<FRAME_ROWS;k++){
			for(int j =0;j<FRAME_COLS; j++){
				explosionFrames[index] = temp[k][j];
				index++;
			}
		}
		explosionAnimation = new Animation(.1f, explosionFrames);
		stateTime = 0f;
		this.width = explosionSheet.getWidth()/FRAME_COLS;
		this.height = explosionSheet.getHeight()/FRAME_ROWS;
		this.bounds = new Rectangle(x - width/2, y - height/2, width, height); 
		System.out.println(explosionSheet.getWidth());
	}
	
	public void draw(SpriteBatch b){
		currentFrame = explosionAnimation.getKeyFrame(stateTime, false);
		b.draw(currentFrame,0,0);
	}

	@Override
	public void update(float DeltaTime) {
		stateTime += DeltaTime;
		// TODO Auto-generated method stub

	}

	@Override
	public boolean checkCollision(GameObject other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
