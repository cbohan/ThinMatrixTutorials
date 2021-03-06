package models;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;

public class ModelLoader {
	private static List<Integer> vaos = new ArrayList<Integer>();
	private static List<Integer> vbos = new ArrayList<Integer>();
	
	public static RawModel loadToVAO(float[] positions, float[] uvs, float[] normals, int[] indices) {
		int vaoId = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, positions, 3);
		storeDataInAttributeList(1, uvs, 2);
		storeDataInAttributeList(2, normals, 3);
		unbindVAO();
		
		return new RawModel(vaoId, indices.length);
	}
	
	public static RawModel loadAnimatedModelToVAO(float[] positions, float[] uvs, float[] normals, int[] jointIds, 
	float[] vertexWeights, int[] indices) {
		int vaoId = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, positions, 3);
		storeDataInAttributeList(1, uvs, 2);
		storeDataInAttributeList(2, normals, 3);
		storeDataInAttributeList(3, jointIds, 3);
		storeDataInAttributeList(4, vertexWeights, 3);
		unbindVAO();
		return new RawModel(vaoId, indices.length);
	}
	
	public static RawModel loadToVAO(float[] positions) {
		int vaoID = createVAO();
		storeDataInAttributeList(0, positions, 2);
		unbindVAO();
		
		return new RawModel(vaoID, positions.length / 2);
	}
	
	public static int loadToVAO(float[] positions, float[] textureCoords) {
		int vaoID = createVAO();
		storeDataInAttributeList(0, positions, 2);
		storeDataInAttributeList(1, textureCoords, 2);
		unbindVAO();
		
		return vaoID;
	}
	
	private static int createVAO() {
		int vaoID = glGenVertexArrays();
		vaos.add(vaoID);
		glBindVertexArray(vaoID);
		return vaoID;
	}
	
	private static void storeDataInAttributeList(int attributeNumber, float[] data, int dataLength) {
		int vboID = glGenBuffers();
		vbos.add(vboID);
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		glVertexAttribPointer(attributeNumber, dataLength, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	private static void storeDataInAttributeList(int attributeNumber, int[] data, int dataLength) {
		int vboID = glGenBuffers();
		vbos.add(vboID);
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(data);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		glVertexAttribIPointer(attributeNumber, dataLength, GL_INT, dataLength * 4, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	private static void unbindVAO() {
		glBindVertexArray(0);
	}
	
	private static void bindIndicesBuffer(int[] indices) {
		int vboID = glGenBuffers();
		vbos.add(vboID);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
	}
	
	private static FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	private static IntBuffer storeDataInIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	public static void cleanUp() {
		for (int vao:vaos) 
			glDeleteVertexArrays(vao);
		for (int vbo:vbos)
			glDeleteBuffers(vbo);
	}
}
