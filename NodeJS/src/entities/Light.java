package entities;

import org.joml.Vector3f;

public class Light {
	private String name;
	private Vector3f position;
	private Vector3f color;
	private float strength;
	private float attenuationFactor;
	private float radius;
	
	public Light(Vector3f position, Vector3f color){
		this.name = "";
		this.position = position;
		this.color = color;
		strength = 10;
		attenuationFactor = 1;
		radius = 25f;
	}
	
	public void setName(String name) { this.name = name; }
	public String getName() { return name; }
	public Vector3f getPosition() { return position; }
	public void setPosition(Vector3f position) { this.position = position; }
	public Vector3f getColor() { return color; }
	public Vector3f getColorTimesStrength() { return new Vector3f(color.x * strength, color.y * strength, color.z * strength); }
	public void setColor(Vector3f color) { this.color = color; }
	public float getStrength() { return strength; }
	public void setStrength(float strength) { this.strength = strength; }
	public float getAttenuationFactor() { return attenuationFactor; }
	public void setAttenuationFactor(float attenuationFactor) { this.attenuationFactor = attenuationFactor; }
	public float getRadius() { return radius; }
	public void setRadius(float radius) { this.radius = radius; }
}
