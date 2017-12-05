package entities;

import models.OBJLoader;
import models.RawModel;
import models.TexturedModel;
import textures.Texture;
import textures.TextureLoader;

public class Entity {	
	protected TexturedModel model;
	protected Transform transform;
	protected int textureIndex = 0;
	
	public Entity(TexturedModel model) {
		this.model = model;
		transform = new Transform();
	}
	
	public Entity(String modelPath, String texturePath) {
		RawModel rawModel = OBJLoader.loadOBJ(modelPath);
		Texture texture = TextureLoader.loadTexture(texturePath);
		model = new TexturedModel(rawModel, texture);
		transform = new Transform();
	}
	
	public Entity(String modelPath, String texturePath, int textureIndex, int numberOfRows) {
		RawModel rawModel = OBJLoader.loadOBJ(modelPath);
		Texture texture = TextureLoader.loadTexture(texturePath);
		texture.setNumberOfRows(numberOfRows);
		model = new TexturedModel(rawModel, texture);
		transform = new Transform();
		this.textureIndex = textureIndex;
	}
	
	public TexturedModel getTexturedModel() { return model; }
	public Transform getTransform() { return transform; }
	
	public int getTextureIndex() { return textureIndex; } 
	public float getTextureXOffset() {
		int column = textureIndex % model.getTexture().getNumberOfRows();
		return (float)column / (float)model.getTexture().getNumberOfRows();
	}
	public float getTextureYOffset() {
		int row = textureIndex / model.getTexture().getNumberOfRows();
		return (float)row / (float)model.getTexture().getNumberOfRows();
	}
}
