package shaders;

import static org.lwjgl.opengl.GL20.glUniform2f;

import org.joml.Vector2f;

public class UniformVec2 extends Uniform {
	public UniformVec2(String name) { super(name); }
	public void loadVec2(float x, float y) { glUniform2f(super.getLocation(), x, y); }
	public void loadVec2(Vector2f vec) { loadVec2(vec.x, vec.y); }
}