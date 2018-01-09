package shaders;

import static org.lwjgl.opengl.GL20.glUniform4f;

import org.joml.Vector4f;

public class UniformVec4 extends Uniform {
	public UniformVec4(String name) { super(name); }
	public void loadVec4(float x, float y, float z, float w) { glUniform4f(super.getLocation(), x, y, z, w); }
	public void loadVec4(Vector4f vec) { loadVec4(vec.x, vec.y, vec.z, vec.w); }
}
