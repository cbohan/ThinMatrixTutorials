package shaders;

public class UniformFloatArray extends Uniform {
	private UniformFloat[] floatArray;
	private int size;
	
	public UniformFloatArray(String name, int size) { 
		super(name); 
		floatArray = new UniformFloat[size];
		for (int i = 0; i < size; i++) 
			floatArray[i] = new UniformFloat(name + "[" + i + "]");
		this.size = size;
	}
	
	protected void storeUniformLocation(int programId) {
		for (int i = 0; i < size; i++)
			floatArray[i].storeUniformLocation(programId);
	}
	
	public void loadFloat(int i, float x) { floatArray[i].loadFloat(x); }
}
