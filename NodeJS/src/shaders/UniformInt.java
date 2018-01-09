package shaders;

import static org.lwjgl.opengl.GL20.glUniform1i;

public class UniformInt extends Uniform{
	public UniformInt(String name) { super(name); }
	public void loadInt(int x) { glUniform1i(super.getLocation(), x); }
}
