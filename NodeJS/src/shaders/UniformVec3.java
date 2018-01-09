package shaders;

import static org.lwjgl.opengl.GL20.glUniform3f;

import org.joml.Vector3f;

public class UniformVec3 extends Uniform {
	public UniformVec3(String name) { super(name); }
	public void loadVec3(float x, float y, float z) { glUniform3f(super.getLocation(), x, y, z); }
	public void loadVec3(Vector3f vec) { loadVec3(vec.x, vec.y, vec.z); }
}
