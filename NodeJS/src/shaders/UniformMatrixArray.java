package shaders;

import org.joml.Matrix4f;

public class UniformMatrixArray extends Uniform {
	private UniformMatrix[] matrixArray;
	private int size;
	
	public UniformMatrixArray(String name, int size) { 
		super(name); 
		matrixArray = new UniformMatrix[size];
		for (int i = 0; i < size; i++) 
			matrixArray[i] = new UniformMatrix(name + "[" + i + "]");
		this.size = size;
	}
	
	protected void storeUniformLocation(int programId) {
		for (int i = 0; i < size; i++)
			matrixArray[i].storeUniformLocation(programId);
	}
	
	public void loadMatrix(int i, Matrix4f x) { matrixArray[i].loadMatrix(x); }
}