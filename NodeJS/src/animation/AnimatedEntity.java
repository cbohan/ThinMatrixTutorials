package animation;

import entities.Transform;
import models.*;
import textures.*;

public class AnimatedEntity {	
	protected AnimatedModel model;
	protected Transform transform;
	protected int textureIndex = 0;
	
	public AnimatedEntity(AnimatedModel model) {
		this.model = model;
		transform = new Transform();
	}
	
	public AnimatedEntity(String modelPath, String texturePath) {
		RawModel rawModel = OBJLoader.loadOBJ(modelPath);
		Texture texture = TextureLoader.loadTexture(texturePath);
		model = new AnimatedModel(rawModel, texture, null, 0);
		transform = new Transform();
	}
	
	public AnimatedModel getAnimatedModel() { return model; }
	public Transform getTransform() { return transform; }
}
