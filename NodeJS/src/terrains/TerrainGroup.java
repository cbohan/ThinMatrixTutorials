package terrains;

import textures.*;

public class TerrainGroup {
	private Terrain[][] terrains;
	private int sizeX;
	private int sizeZ;
	
	public TerrainGroup(int x, int z, TerrainTexturePack texturePack, String splatMapFile, String heightMapFile,
			String normalMapFile) {
		sizeX = x;
		sizeZ = z;
		
		terrains = new Terrain[x][z];
		for(int i = 0; i < x; i++){
			int xPos = i - (x / 2);
			for(int j = 0; j < z; j++) {
				int zPos = j - (z / 2);
				String splatMapChunkFile = splatMapFile + "_" + i + "_" + j + ".png";
				String heightMapChunkFile = heightMapFile + "_" + i + "_" + j + ".png";
				String normalMapChunkFile = normalMapFile + "_" + i + "_" + j + ".png";
				terrains[i][j] = new Terrain(xPos, zPos, texturePack, splatMapChunkFile, heightMapChunkFile,
						normalMapChunkFile);
			}
		}
	}
	
	public void addToRenderer() {
		for(int i = 0; i < sizeX; i++){
			for(int j = 0; j < sizeZ; j++){
				TerrainRenderer.addTerrain(terrains[i][j]);
			}
		}
	}
	
	public float getHeight(float worldX, float worldZ) {
		for(int i = 0; i < sizeX; i++){
			for(int j = 0; j < sizeZ; j++){
				if (terrains[i][j].onTerrain(worldX, worldZ)) {
					return terrains[i][j].getHeight(worldX, worldZ);
				}
			}
		} 
		
		return 0;
	}
	
	public int getSizeX() { return sizeX; }
	public int getSizeZ() { return sizeZ; }
}
