package shaders;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.joml.*;
import org.lwjgl.BufferUtils;

public abstract class ShaderProgram {
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
		
	public ShaderProgram(String vertexFile, String fragmentFile, String... attributes) {
		vertexShaderID = loadShader(vertexFile, GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragmentFile, GL_FRAGMENT_SHADER);
		programID = glCreateProgram();
		glAttachShader(programID, vertexShaderID);
		glAttachShader(programID, fragmentShaderID);
		bindAttributes(attributes);
		glLinkProgram(programID);
		glDetachShader(programID, vertexShaderID);
		glDetachShader(programID, fragmentShaderID);
		glDeleteShader(vertexShaderID);
		glDeleteShader(fragmentShaderID);		
				
		System.out.println("Loaded shader: " + vertexFile + " -> " + fragmentFile);
	}
	
	protected void bindAttributes(String[] attributes) {
		for (int i = 0; i < attributes.length; i++)
			glBindAttribLocation(programID, i, attributes[i]);
		glValidateProgram(programID);
	}
	
	public void start() { glUseProgram(programID); }
	public void stop() { glUseProgram(0); }
	
	public void cleanUp() {
		stop();
		glDeleteProgram(programID);
	}
	
	protected void bindAttribute(int attribute, String variableName) {
		glBindAttribLocation(programID, attribute, variableName);
	}
	
	protected int getUniformLocation(String uniformName) {
		return glGetUniformLocation(programID, uniformName);
	}
	
	protected void storeAllUniformLocations(Uniform... uniforms) {
		for (Uniform uniform : uniforms)
			uniform.storeUniformLocation(programID);
	}
	
	private static int loadShader(String fileName, int shaderType) {
		String shaderSource = loadFile(fileName);
		
		int shaderID = glCreateShader(shaderType);
		glShaderSource(shaderID, shaderSource);
		glCompileShader(shaderID);
		if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println(glGetShaderInfoLog(shaderID, 500));
			System.err.println("Could not compile shader");
			System.exit(-1);
		}
		
		return shaderID;
	}
	
	private static String loadFile(String fileName) {
		StringBuilder shaderSource = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line;
			while((line = reader.readLine()) != null) {
				if (line.startsWith("#include")) {
					String[] lineParts = line.split("\"");
					shaderSource.append(loadFile(lineParts[1])).append("\n");
				} else {
					shaderSource.append(line).append("\n");
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		return shaderSource.toString();
	}
}
