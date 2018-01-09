package shaders;

import org.joml.Vector3f;

public class UniformVec3Array extends Uniform {
	private UniformVec3[] vec3Array;
	private int size;
	
	public UniformVec3Array(String name, int size) { 
		super(name); 
		vec3Array = new UniformVec3[size];
		for (int i = 0; i < size; i++) 
			vec3Array[i] = new UniformVec3(name + "[" + i + "]");
		this.size = size;
	}
	
	protected void storeUniformLocation(int programId) {
		for (int i = 0; i < size; i++)
			vec3Array[i].storeUniformLocation(programId);
	}
	
	public void loadVec3(int i, float x, float y, float z) { vec3Array[i].loadVec3(x, y, z); }
	public void loadVec3(int i, Vector3f vec) { vec3Array[i].loadVec3(vec); }
}
