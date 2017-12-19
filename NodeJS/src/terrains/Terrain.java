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
	private static final float MAX_HEIGHT = 600;
	
	private float x;
	private float z;
	private RawModel model;
	private TerrainTexturePack texturePack;
	private Texture splatMap;
	private Transform transform;
	private float[][] heightMapValues;
	private Vector3f[][] normalMapValues;
	
	public Terrain(int gridX, int gridZ, TerrainTexturePack texturePack, String splatMap, String heightMap,
			String normalMap) {
		this.texturePack = texturePack;
		this.splatMap = TextureLoader.loadTexture(splatMap, true, false);
		this.x = (gridX * SIZE) - (SIZE / 2.0f);
		this.z = (gridZ * SIZE) - (SIZE / 2.0f);
		this.model = generateTerrain(heightMap, normalMap);
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
	
	private RawModel generateTerrain(String heightMapFile, String normalMapFile){
		//Load the height map.
		BufferedImage heightMap = null;
		BufferedImage normalMap = null;
		try {
			heightMap = ImageIO.read(new File(heightMapFile));
			normalMap = ImageIO.read(new File(normalMapFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int vertexCount = heightMap.getHeight();		
		heightMapValues = new float[heightMap.getWidth()][heightMap.getHeight()];
		for (int x = 0; x < heightMap.getWidth(); x++)
			for (int z = 0; z < heightMap.getHeight(); z++)
				heightMapValues[x][z] = getHeight(x, z, heightMap);
		
		normalMapValues = new Vector3f[normalMap.getWidth()][normalMap.getHeight()];
		for (int x = 0; x < normalMap.getWidth(); x++)
			for (int z = 0; z < normalMap.getHeight(); z++)
				normalMapValues[x][z] = getNormal(x, z, normalMap);
		
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
				normals[vertexPointer*3] = normalMapValues[j][i].x;
				normals[vertexPointer*3+1] = normalMapValues[j][i].y;
				normals[vertexPointer*3+2] = normalMapValues[j][i].z;
				textureCoords[vertexPointer*2] = ((float)j/((float)vertexCount - 1)) * (1 - (1f / SIZE)) + (.5f / SIZE);
				textureCoords[vertexPointer*2+1] = (1f - (float)i/((float)vertexCount - 1)) * (1 - (1f / SIZE)) + (.5f / SIZE);
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
	
	private float getHeight(int x, int z, BufferedImage heightMap) {
		if (x < 0 || x >= heightMap.getWidth() || z < 0 || z >= heightMap.getHeight())
			return 0;
		
		int heightBytes = heightMap.getRGB(x, heightMap.getHeight() - z - 1);
		int rByte = unsignedToBytes((byte)((heightBytes >> 16) & 0xFF));
		int gByte = unsignedToBytes((byte)((heightBytes >> 8) & 0xFF));
		int bByte = unsignedToBytes((byte)((heightBytes >> 0) & 0xFF));
						
		float height = (float) ((rByte * (1.0/(255.0))) + 
				(gByte * (1.0/(255.0 * 255.0))) + 
				(bByte * (1.0/(255.0 * 255.0 * 255.0))));
		height *= MAX_HEIGHT;
		
		return height;
	}
	
	private Vector3f getNormal(int x, int z, BufferedImage normalMap) {
		if (x < 0 || x >= normalMap.getWidth() || z < 0 || z >= normalMap.getHeight())
			return new Vector3f(0, 1, 0);
		
		int heightBytes = normalMap.getRGB(x, normalMap.getHeight() - z - 1);
		int rByte = unsignedToBytes((byte)((heightBytes >> 16) & 0xFF));
		int gByte = unsignedToBytes((byte)((heightBytes >> 8) & 0xFF));
		int bByte = unsignedToBytes((byte)((heightBytes >> 0) & 0xFF));
						
		float xNorm = rByte / 255f;
		xNorm = -((xNorm * 2f) - 1f);
		float yNorm = gByte / 255f;
		yNorm = (yNorm * 2f) - 1f;
		float zNorm = bByte / 255f;
		zNorm = ((zNorm * 2f) - 1f);
		
		
		return new Vector3f(xNorm, yNorm, zNorm);
	}
	
	private int unsignedToBytes(byte a)
	{
	    int b = a & 0xFF;
	    return b;
	}
}
