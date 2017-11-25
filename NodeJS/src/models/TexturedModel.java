package models;

import textures.Texture;

public class TexturedModel {
	private RawModel rawModel;
	private Texture texture;
	private float shineDamping = 1;
	private float reflectivity = 0;
	
	public TexturedModel(RawModel model, Texture texture) {
		this.rawModel = model;
		this.texture = texture;
	}
	
	public RawModel getRawModel() { return rawModel; }
	public Texture getTexture() { return texture; }
	
	public float getShineDamping() { return shineDamping; }
	public void setShineDamping(float damping) { shineDamping = damping; }
	public float getReflectivity() { return reflectivity; }
	public void setReflectivity(float reflectivity) { this.reflectivity = reflectivity; }
}
