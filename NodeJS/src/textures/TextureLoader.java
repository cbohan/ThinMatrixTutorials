package textures;

import java.util.HashMap;
import java.util.Map;

public class TextureLoader {
	private static Map<String, Texture> texturesMap = new HashMap<String, Texture>();
	
	public static Texture loadTexture(String fileName) {
		if (texturesMap.containsKey(fileName))
			return texturesMap.get(fileName);
		
		Texture texture = new Texture(fileName);
		texturesMap.put(fileName, texture);
		return texture;
	}
	
	public static void cleanUp() {
		for(Texture texture : texturesMap.values())
			texture.cleanUp();
	}
}
