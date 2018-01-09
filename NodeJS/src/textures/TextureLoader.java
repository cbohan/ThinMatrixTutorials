package textures;

import java.util.HashMap;
import java.util.Map;

public class TextureLoader {
	private static Map<String, Texture> texturesMap = new HashMap<String, Texture>();
	
	public static Texture loadTexture(String fileName) {
		return loadTexture(fileName, false, true);
	}
	
	public static Texture loadTexture(String fileName, boolean clamp, boolean mip) {
		if (texturesMap.containsKey(fileName))
			return texturesMap.get(fileName);
		
		long beforeTime = System.nanoTime();
		Texture texture = new Texture(fileName, clamp, mip);
		texturesMap.put(fileName, texture);
		
		long afterTime = System.nanoTime();
		double duration = (afterTime - beforeTime) / 1000000000.0;
		System.out.println("Loaded texture: " + fileName + " (" + duration + " seconds)");
		return texture;
	}
	
	public static void cleanUp() {
		for(Texture texture : texturesMap.values())
			texture.cleanUp();
	}
}
