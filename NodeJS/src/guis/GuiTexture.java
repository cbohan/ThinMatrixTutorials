package guis;

import org.joml.Vector2f;

import textures.Texture;
import textures.TextureLoader;

public class GuiTexture {
	private Texture texture;
	private Vector2f position;
	private Vector2f scale;
	
	public GuiTexture(String textureFile, float x, float y, float scaleX, float scaleY) {
		texture = TextureLoader.loadTexture(textureFile);
		position = new Vector2f(x, y);
		scale = new Vector2f(scaleX, scaleY);
	}
	
	public GuiTexture(Texture texture, float x, float y, float scaleX, float scaleY) {
		this.texture = texture;
		position = new Vector2f(x, y);
		scale = new Vector2f(scaleX, scaleY);
	}
	
	public GuiTexture(String textureFile, float x, float y) { this(textureFile, x, y, 1, 1); }
	public GuiTexture(String textureFile) { this(textureFile, 0, 0, 1, 1); }
	
	public Texture getTexture() { return texture; }
	public Vector2f getPosition() { return position; }
	public Vector2f getScale() { return scale; }
}
