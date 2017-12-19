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
	
	private FloatBuffer matrixBuffer;
	
	public ShaderProgram(String vertexFile, String fragmentFile) {
		matrixBuffer = BufferUtils.createFloatBuffer(16);
		
		vertexShaderID = loadShader(vertexFile, GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragmentFile, GL_FRAGMENT_SHADER);
		programID = glCreateProgram();
		glAttachShader(programID, vertexShaderID);
		glAttachShader(programID, fragmentShaderID);
		bindAttributes();
		glLinkProgram(programID);
		glValidateProgram(programID);
		getAllUniformLocations();
	}
	
	protected abstract void bindAttributes();
	
	public void start() { glUseProgram(programID); }
	public void stop() { glUseProgram(0); }
	
	public void cleanUp() {
		stop();
		glDetachShader(programID, vertexShaderID);
		glDetachShader(programID, fragmentShaderID);
		glDeleteShader(vertexShaderID);
		glDeleteShader(fragmentShaderID);
		glDeleteProgram(programID);
	}
	
	protected void bindAttribute(int attribute, String variableName) {
		glBindAttribLocation(programID, attribute, variableName);
	}
	
	protected int getUniformLocation(String uniformName) {
		return glGetUniformLocation(programID, uniformName);
	}
	
	protected abstract void getAllUniformLocations();
	
	protected void loadInt(int location, int value) { glUniform1i(location, value); }
	protected void loadFloat(int location, float value) { glUniform1f(location, value); }
	protected void loadVec2(int location, Vector2f value) { glUniform2f(location, value.x(), value.y()); }
	protected void loadVec3(int location, Vector3f value) { glUniform3f(location, value.x(), value.y(), value.z()); }
	protected void loadVec4(int location, Vector4f value) { glUniform4f(location, value.x(), value.y(), value.z(), value.w()); }
	protected void loadMatrix(int location, Matrix4f value) { 
		value.get(matrixBuffer);
		glUniformMatrix4fv(location, false, matrixBuffer); 
	}
	
	private static int loadShader(String fileName, int shaderType) {
		StringBuilder shaderSource = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line;
			while((line = reader.readLine()) != null)
				shaderSource.append(line).append("\n");
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
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
}
