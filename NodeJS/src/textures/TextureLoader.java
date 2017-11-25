package textures;

import java.util.ArrayList;
import java.util.List;

public class TextureLoader {
	private static List<Texture> textures = new ArrayList<Texture>();
	
	public static Texture loadTexture(String fileName) {
		Texture texture = new Texture(fileName);
		textures.add(texture);
		return texture;
	}
	
	public static void cleanUp() {
		for(Texture texture : textures)
			texture.cleanUp();
	}
}
