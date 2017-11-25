package terrains;

import models.ModelLoader;
import renderEngine.TerrainRenderer;
import textures.*;

public class TerrainGroup {
	private Terrain[][] terrains;
	private int sizeX;
	private int sizeZ;
	
	public TerrainGroup(int x, int z, ModelLoader loader, TerrainTexturePack texturePack, Texture splatMap) {
		sizeX = x;
		sizeZ = z;
		
		terrains = new Terrain[x][z];
		for(int i = 0; i < x; i++){
			int xPos = i - (x / 2);
			for(int j = 0; j < z; j++) {
				int zPos = j - (z / 2);
				terrains[i][j] = new Terrain(xPos, zPos, loader, texturePack, splatMap);
			}
		}
	}
	
	public void addToRenderer(TerrainRenderer renderer) {
		for(int i = 0; i < sizeX; i++){
			for(int j = 0; j < sizeZ; j++){
				renderer.addTerrain(terrains[i][j]);
			}
		}
	}
	
	public int getSizeX() { return sizeX; }
	public int getSizeZ() { return sizeZ; }
}
