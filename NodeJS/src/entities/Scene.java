package entities;

import java.util.ArrayList;
import java.util.List;

import terrains.TerrainGroup;
import terrains.TerrainRenderer;
import water.WaterFrameBuffers;
import water.WaterPlane;
import window.Window;

public class Scene {
	private List<Light> lights = new ArrayList<Light>();
	private TerrainGroup terrainGroup;
	private List<Entity> entities = new ArrayList<Entity>();
	private WaterPlane waterPlane;
	private WaterFrameBuffers waterFBOs;
	
	public Scene() {
		waterFBOs = new WaterFrameBuffers();
		waterPlane = new WaterPlane(waterFBOs);
	}
	
	public WaterPlane getWaterPlane() { return waterPlane; }
	public WaterFrameBuffers getWaterFBOs() { return waterFBOs; }
	
	public void addLight(Light light) { this.lights.add(light); }
	public List<Light> getLights() { return this.lights; } 
	public void addEntity(Entity entity) { entities.add(entity); }
	public void setTerrainGroup(TerrainGroup terrainGroup) { this.terrainGroup = terrainGroup; }
	
	public void render(Camera camera, float clippingPlaneDirection, float clippingPlaneHeight, boolean renderWaterPlane) {
		Window.clearBuffer(camera);
		terrainGroup.addToRenderer();
		TerrainRenderer.render(this, camera, clippingPlaneDirection, 
				-1 * clippingPlaneDirection * clippingPlaneHeight);
		
		for (Entity entity : entities) 
			EntityRenderer.addEntity(entity);
		EntityRenderer.render(this, camera, clippingPlaneDirection, 
				-1 * clippingPlaneDirection * clippingPlaneHeight);
		
		if (renderWaterPlane) 
			waterPlane.render(this, camera);	
	}
}
