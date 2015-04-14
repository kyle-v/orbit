package orbit;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class AssetLibrary {

	private static HashMap<String, Texture> textures = new HashMap<String, Texture>();
	private static HashMap<String, Sound> sounds = new HashMap<String, Sound>();
	
	
	public AssetLibrary(){
		loadTextures();
		loadSounds();
	}
	
	public static Texture getTexture(String filename){
		Texture t = textures.get(filename);
		if(t != null){
			return t;
		}else{
			t = new Texture(Gdx.files.internal(filename));
			textures.put(filename, t);
			return t;
		}
	}
	
	public static Sound getSound(String filename){
		Sound s  = sounds.get(filename);
		if(s != null){
			return s;
		}else{
			s = Gdx.audio.newSound(Gdx.files.internal("sound/" + filename));
			sounds.put(filename, s);
			return s;
		}
	}
	
	public static long playSound(String filename){
		Sound s  = getSound(filename);
		long id = s.play(1.0f); 
		return id;
	}
	
	private static void loadTextures(){
		
	}
	
	private static void loadSounds(){
		
	}
}
