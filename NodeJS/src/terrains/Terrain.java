package terrains;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.joml.Vector2f;
import org.joml.Vector3f;

import entities.Transform;
import models.*;
import textures.*;

public class Terrain {
	private static final float SIZE = 128;
	private static final float MAX_HEIGHT = 10;
	private static final float MAX_PIXEL_COLOR = 255 * 255 * 255;
	
	private float x;
	private float z;
	private RawModel model;
	private TerrainTexturePack texturePack;
	private Texture splatMap;
	private Transform transform;
	private float[][] heightMapValues;
	
	public Terrain(int gridX, int gridZ, TerrainTexturePack texturePack, Texture splatMap, String heightMap) {
		this.texturePack = texturePack;
		this.splatMap = splatMap;
		this.x = (gridX * SIZE) - (SIZE / 2.0f);
		this.z = (gridZ * SIZE) - (SIZE / 2.0f);
		this.model = generateTerrain(heightMap);
		this.transform = new Transform();
		this.transform.setPosX(x);
		this.transform.setPosZ(z); 
	}
	
	public float getX() { return x; }
	public float getZ() { return z; }
	public RawModel getModel() { return model; }
	public Transform getTransform() { return transform; }
	public TerrainTexturePack getTexturePack() { return texturePack; }
	public Texture getSplatMap() { return splatMap; }
	
	private RawModel generateTerrain(String heightMapFile){
		//Load the height map.
		BufferedImage heightMap = null;
		try {
			heightMap = ImageIO.read(new File(heightMapFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int vertexCount = heightMap.getHeight();		
		heightMapValues = new float[heightMap.getWidth()][heightMap.getHeight()];
		for (int x = 0; x < heightMap.getWidth(); x++)
			for (int z = 0; z < heightMap.getHeight(); z++)
				heightMapValues[x][z] = getHeight(x, z, heightMap);
		
		int count = vertexCount * vertexCount;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(vertexCount-1)*(vertexCount-1)];
		int vertexPointer = 0;
		for(int i=0;i<vertexCount;i++){
			for(int j=0;j<vertexCount;j++){
				vertices[vertexPointer*3] = (float)j/((float)vertexCount - 1) * SIZE;
				vertices[vertexPointer*3+1] = heightMapValues[j][i];
				vertices[vertexPointer*3+2] = (float)i/((float)vertexCount - 1) * SIZE;
				Vector3f normal = calculateNormal(j, i, heightMapValues);
				normals[vertexPointer*3] = normal.x;
				normals[vertexPointer*3+1] = normal.y;
				normals[vertexPointer*3+2] = normal.z;
				textureCoords[vertexPointer*2] = (float)j/((float)vertexCount - 1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)vertexCount - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<vertexCount-1;gz++){
			for(int gx=0;gx<vertexCount-1;gx++){
				int topLeft = (gz*vertexCount)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*vertexCount)+gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return ModelLoader.loadToVAO(vertices, textureCoords, normals, indices);
	}
	
	private Vector3f calculateNormal(int x, int z, float[][] heightMapValues) {
		float heightL = getHeight(x-1, z, heightMapValues);
		float heightR = getHeight(x+1, z, heightMapValues);
		float heightD = getHeight(x, z-1, heightMapValues);
		float heightU = getHeight(x, z+1, heightMapValues);
		Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD-heightU);
		return normal.normalize();
	}
	
	public boolean onTerrain(float worldX, float worldZ) {
		float terrainX = worldX - this.x;
		float terrainZ = worldZ - this.z;
		float gridSquareSize = SIZE / ((float)heightMapValues.length - 1);
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
		
		if (gridX >= heightMapValues.length - 1 || gridZ >= heightMapValues.length - 1)
			return false;
		if (gridX < 0 || gridZ < 0)
			return false;
		
		return true;
	}
	
	public float getHeight(float worldX, float worldZ) {
		float terrainX = (worldX - this.x);
		float terrainZ = (worldZ - this.z);
		float gridSquareSize = SIZE / ((float)heightMapValues.length - 1);
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
		
		if (gridX >= heightMapValues.length - 1 || gridZ >= heightMapValues.length - 1)
			return 0;
		if (gridX < 0 || gridZ < 0)
			return 0;

		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;		
		
		if (xCoord <= (1-zCoord)) {
			return barryCentric(new Vector3f(0, heightMapValues[gridX][gridZ], 0), 
					new Vector3f(1, heightMapValues[gridX + 1][gridZ], 0), 
					new Vector3f(0, heightMapValues[gridX][gridZ + 1], 1), 
					new Vector2f(xCoord, zCoord));
		} else {
			return barryCentric(new Vector3f(1, heightMapValues[gridX + 1][gridZ], 0), 
					new Vector3f(1, heightMapValues[gridX + 1][gridZ + 1], 1), 
					new Vector3f(0, heightMapValues[gridX][gridZ + 1], 1), 
					new Vector2f(xCoord, zCoord));
		}
		
	}
	
	private float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}
	
	private float getHeight(int x, int z, float[][] heightMapValues) {
		if (x < 0 || x >= heightMapValues.length || z < 0 || z >= heightMapValues[0].length)
			return 0;
		return heightMapValues[x][z];
	}
	
	private float getHeight(int x, int z, BufferedImage heightMap) {
		if (x < 0 || x >= heightMap.getWidth() || z < 0 || z >= heightMap.getHeight())
			return 0;
		
		float height = heightMap.getRGB(x, z);
		height += MAX_PIXEL_COLOR / 2f;
		height /= MAX_PIXEL_COLOR / 2f;
		height *= MAX_HEIGHT;
		
		return height;
	}
}
