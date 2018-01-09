package shaders;

import static org.lwjgl.opengl.GL20.glUniform1f;

public class UniformFloat extends Uniform{
	public UniformFloat(String name) { super(name); }
	public void loadFloat(float x) { glUniform1f(super.getLocation(), x); }
}
