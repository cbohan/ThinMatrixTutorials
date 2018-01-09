package shaders;

import static org.lwjgl.opengl.GL20.*;

public abstract class Uniform {
	private String name;
	private int location;
	
	protected Uniform(String name) {
		this.name = name;
	}
	
	protected void storeUniformLocation(int programId) {
		location = glGetUniformLocation(programId, name);
		if (location == -1)
			System.err.println("No uniform called " + name + " found.");
	}
	
	protected String getName() { return name; }
	protected int getLocation() { return location; }
}
