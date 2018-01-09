package shaders;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import java.nio.FloatBuffer;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

public class UniformMatrix extends Uniform {
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);	
	
	public UniformMatrix(String name) { super(name); }
	
	public void loadMatrix(Matrix4f value) { 
		value.get(matrixBuffer);
		glUniformMatrix4fv(getLocation(), false, matrixBuffer); 
	}
}
