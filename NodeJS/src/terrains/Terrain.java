package terrains;

import entities.Transform;
import models.*;
import textures.*;

public class Terrain {
	private static final float SIZE = 128;
	private static final int VERTEX_COUNT = 128;
	
	private float x;
	private float z;
	private RawModel model;
	private TerrainTexturePack texturePack;
	private Texture splatMap;
	private Transform transform;
	
	public Terrain(int gridX, int gridZ, ModelLoader loader, TerrainTexturePack texturePack, Texture splatMap) {
		this.texturePack = texturePack;
		this.splatMap = splatMap;
		this.x = (gridX * SIZE) - (SIZE / 2.0f);
		this.z = (gridZ * SIZE) - (SIZE / 2.0f);
		this.model = generateTerrain(loader);
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
	
	private RawModel generateTerrain(ModelLoader loader){
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
		int vertexPointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
				vertices[vertexPointer*3+1] = 0;
				vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
				normals[vertexPointer*3] = 0;
				normals[vertexPointer*3+1] = 1;
				normals[vertexPointer*3+2] = 0;
				textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<VERTEX_COUNT-1;gz++){
			for(int gx=0;gx<VERTEX_COUNT-1;gx++){
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}
}
