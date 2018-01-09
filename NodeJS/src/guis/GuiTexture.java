package guis;

import org.joml.Vector2f;
import org.joml.Vector3f;

import textures.Texture;
import textures.TextureLoader;

public class GuiTexture {
	private Vector3f color = new Vector3f(0, 0, 0);
	private float useColor = 0;
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
	
	public GuiTexture(Vector3f color, float x, float y, float scaleX, float scaleY) {
		this.color = color;
		this.useColor = 1;
		position = new Vector2f(x, y);
		scale = new Vector2f(scaleX, scaleY);
	}
	
	public Texture getTexture() { return texture; }
	public Vector2f getPosition() { return position; }
	public Vector2f getScale() { return scale; }
	public void setColor(Vector3f color) { this.color = color; }
	public Vector3f getColor() { return color; }
	public float getUseColor() { return useColor; }
}
