package entities;

import models.ModelLoader;
import models.OBJLoader;
import models.RawModel;
import models.TexturedModel;
import textures.Texture;
import textures.TextureLoader;

public class Entity {
	private static ModelLoader modelLoader = new ModelLoader();
	private static TextureLoader textureLoader = new TextureLoader();
	
	private TexturedModel model;
	private Transform transform;
	
	public Entity(TexturedModel model) {
		this.model = model;
		transform = new Transform();
	}
	
	public Entity(String modelPath, String texturePath) {
		RawModel model = OBJLoader.loadOBJ(modelPath, modelLoader);
		Texture texture = textureLoader.loadTexture(texturePath);
		this.model = new TexturedModel(model, texture);
		transform = new Transform();
	}
	
	public TexturedModel getTexturedModel() { return model; }
	public Transform getTransform() { return transform; }
}
