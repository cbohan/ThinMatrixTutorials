package entities;

import java.util.ArrayList;
import java.util.List;

import renderEngine.EntityRenderer;
import renderEngine.TerrainRenderer;
import terrains.TerrainGroup;

public class Scene {
	private Light light;
	private TerrainGroup terrainGroup;
	private List<Entity> entities = new ArrayList<Entity>();
	
	public Scene() {
		
	}
	
	public void setLight(Light light) { this.light = light; }
	public Light getLight() { return this.light; } 
	public void addEntity(Entity entity) { entities.add(entity); }
	public void setTerrainGroup(TerrainGroup terrainGroup) { this.terrainGroup = terrainGroup; }
	
	public void render(Camera camera) {
		terrainGroup.addToRenderer();
		TerrainRenderer.render(this, camera);
		
		for (Entity entity : entities) 
			EntityRenderer.addEntity(entity);
		EntityRenderer.render(this, camera);
	}
}
