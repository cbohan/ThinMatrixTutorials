package entities;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.*;
import org.joml.Vector3f;
import org.w3c.dom.*;
import javax.xml.parsers.*;

import animation.AnimatedEntity;
import terrains.*;
import textures.TerrainTexturePack;
import water.*;
import animation.*;
import window.Window;

public class Scene {
	private List<Light> lights = new ArrayList<Light>();
	private TerrainGroup terrainGroup;
	private List<Entity> entities = new ArrayList<Entity>();
	private List<AnimatedEntity> animatedEntities = new ArrayList<AnimatedEntity>();
	private WaterPlane waterPlane;
	private WaterFrameBuffers waterFBOs;
	private float waterHeight;
	
	public Scene() {
		waterFBOs = new WaterFrameBuffers();
		waterPlane = new WaterPlane(waterFBOs);
	}
	
	public WaterPlane getWaterPlane() { return waterPlane; }
	public WaterFrameBuffers getWaterFBOs() { return waterFBOs; }
	public void setWaterHeight(float waterHeight) {
		getWaterPlane().getTransform().move(0f, waterHeight, 0f);
		this.waterHeight = waterHeight;
	}
	public float getWaterHeight() { return waterHeight; }
	public TerrainGroup getTerrainGroup() { return terrainGroup; }
	
	public void addLight(Light light) { this.lights.add(light); }
	public List<Light> getLights() { return this.lights; } 
	public void addEntity(Entity entity) { entities.add(entity); }
	public void addAnimatedEntity(AnimatedEntity entity) { animatedEntities.add(entity); }
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
		
		for (AnimatedEntity entity : animatedEntities) 
			AnimatedModelRenderer.addAnimatedEntity(entity);
		AnimatedModelRenderer.render(this, camera, clippingPlaneDirection, 
				-1 * clippingPlaneDirection * clippingPlaneHeight);
		
		if (renderWaterPlane) 
			waterPlane.render(this, camera);	
	}
	
	public void load(String fileName) {
		Element sceneElement = getDocument(fileName).getDocumentElement();
		
		//water
		Element waterElement = (Element)sceneElement.getElementsByTagName("water").item(0);
		Element waterEnabledElement = (Element)waterElement.getElementsByTagName("enabled").item(0);
		Element waterHeightElement = (Element)waterElement.getElementsByTagName("water_height").item(0);
		
		boolean waterEnabled = waterEnabledElement.getTextContent().trim().toLowerCase().equals("true");
		float waterHeight = Float.parseFloat(waterHeightElement.getTextContent());
		if (waterEnabled)
			setWaterHeight(waterHeight);
		
		//lights
		Element lightsElement = (Element)sceneElement.getElementsByTagName("lights").item(0);
		NodeList lightList = lightsElement.getElementsByTagName("light");
		for (int i = 0; i < lightList.getLength(); i++) {
			Element lightElement = (Element)lightList.item(i);
			
			String name = lightElement.getElementsByTagName("name").item(0).getTextContent();
			
			Element positionElement = (Element)lightElement.getElementsByTagName("position").item(0);
			float xPosition = Float.parseFloat(positionElement.getElementsByTagName("x").item(0).getTextContent());
			float yPosition = Float.parseFloat(positionElement.getElementsByTagName("y").item(0).getTextContent());
			float zPosition = Float.parseFloat(positionElement.getElementsByTagName("z").item(0).getTextContent());
			
			Element colorElement = (Element)lightElement.getElementsByTagName("color").item(0);
			float red = Float.parseFloat(colorElement.getElementsByTagName("red").item(0).getTextContent());
			float green = Float.parseFloat(colorElement.getElementsByTagName("green").item(0).getTextContent());
			float blue = Float.parseFloat(colorElement.getElementsByTagName("blue").item(0).getTextContent());
			
			float strength = Float.parseFloat(lightElement.getElementsByTagName("strength").item(0).getTextContent());
			float attenuationFactor = Float.parseFloat(lightElement.getElementsByTagName("attenuation_factor").item(0).getTextContent());
			float radius = Float.parseFloat(lightElement.getElementsByTagName("radius").item(0).getTextContent());
			
			Light light = new Light(new Vector3f(xPosition, yPosition, zPosition), new Vector3f(red, green, blue));
			light.setName(name);
			light.setStrength(strength);
			light.setAttenuationFactor(attenuationFactor);
			light.setRadius(radius);
			addLight(light);
		}
		
		//terrain
		Element terrainElement = (Element)sceneElement.getElementsByTagName("terrain").item(0);
		
		Element terrainTexturePackElement = (Element)terrainElement.getElementsByTagName("terrain_texture_pack").item(0);
		String terrainTexture0 = terrainTexturePackElement.getElementsByTagName("texture0").item(0).getTextContent();
		String terrainTexture1 = terrainTexturePackElement.getElementsByTagName("texture1").item(0).getTextContent();
		String terrainTexture2 = terrainTexturePackElement.getElementsByTagName("texture2").item(0).getTextContent();
		String terrainTexture3 = terrainTexturePackElement.getElementsByTagName("texture3").item(0).getTextContent();
		
		Element terrainSizeElement = (Element)terrainElement.getElementsByTagName("size").item(0);
		int terrainXSize = Integer.parseInt(terrainSizeElement.getElementsByTagName("x").item(0).getTextContent());
		int terrainYSize = Integer.parseInt(terrainSizeElement.getElementsByTagName("y").item(0).getTextContent());
		
		String terrainSplatmap = terrainElement.getElementsByTagName("splatmap").item(0).getTextContent();
		String terrainHeightmap = terrainElement.getElementsByTagName("heightmap").item(0).getTextContent();
		String terrainNormalmap = terrainElement.getElementsByTagName("normalmap").item(0).getTextContent();
		
		TerrainTexturePack terrainTexturePack = new TerrainTexturePack(terrainTexture0, terrainTexture1, terrainTexture2,
				terrainTexture3);
		TerrainGroup terrainGroup = new TerrainGroup(terrainXSize, terrainYSize, terrainTexturePack, terrainSplatmap, 
				terrainHeightmap, terrainNormalmap);
		setTerrainGroup(terrainGroup);
	}
	
	private Document getDocument(String fileName) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setValidating(false);
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			return builder.parse(new InputSource(fileName));
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
		
		return null;
	}
}
