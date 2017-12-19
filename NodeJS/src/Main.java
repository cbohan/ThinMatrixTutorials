import java.util.*;
import org.joml.Vector3f;
import entities.*;
import guis.*;
import models.*;
import networking.*;
import terrains.*;
import textures.*;
import window.*;

public class Main {
	private static SocketManager socketManager;
	
	private static long lastMessageSent = 0;
	private static long dataSendDelay = 50;
		
	public static void main(String[] args) {
		//Init.
		Window.init(1280, 720);
		socketManager = new SocketManager("127.0.0.1", 2000);
		
		Scene scene = new Scene();
		float waterHeight = 15f;
		scene.getWaterPlane().getTransform().move(0f, waterHeight, 0f);
		
		Light sun = new Light(new Vector3f(50000f, 50000f, 0f), new Vector3f(.6f, .65f, .5f));
		sun.setStrength(4f);
		sun.setAttenuationFactor(0f);
		sun.setRadius(1000000f);
		scene.addLight(sun);
		
		Player player = new Player();
		player.getTransform().setPosZ(-55f);
		scene.addEntity(player);
		
		PlayerCamera camera = new PlayerCamera(player);
		
		TerrainGroup terrainGroup = new TerrainGroup(4, 4, new TerrainTexturePack("res\\grass1.png", "res\\grass1.png", 
				"res\\dirt1.png", "res\\stone1.png"), "res\\terrain\\splatmap", "res\\terrain\\heightmap", 
				"res\\terrain\\normalmap");
		scene.setTerrainGroup(terrainGroup);
		
		GuiTexture bag = new GuiTexture("res\\bag_icon.png", .94375f, -.9f, .05625f, .1f);
		GuiRenderer.addGUI(bag);
		
		for (int i = 0; i < 3000; i++) {
			Entity grass = new Entity("res\\grass_bill.obj", "res\\plant_bill_atlas_1.png", (int)(Math.random()*4), 2);
			grass.getTransform().setScale(2.5f);
			float xPos = (float)(Math.random() - .5f) * 300f;
			float zPos = (float)(Math.random() - .5f) * 300f;
			float yPos = terrainGroup.getHeight(xPos, zPos)-.5f;
			grass.getTransform().setPosition(xPos, yPos, zPos);
			grass.getTexturedModel().getTexture().setHasTransparency(true);
			grass.getTexturedModel().getTexture().setOverrideNormals(true);
			scene.addEntity(grass);
		}
		
		List<Entity> otherPlayers = new ArrayList<Entity>();
		
		while (Window.shouldClose() == false) {
			Window.clearBuffer(camera);
			Window.pollEvents();
			
			player.move(terrainGroup);
			camera.update();
			
			System.out.println(socketManager.getPlayerNetTransforms().length);
						
			doNetStuff(otherPlayers, player);
			
			scene.getWaterFBOs().bindReflectionFBO();
			float cameraReflectMoveDistance = 2 * (camera.getTransform().getPosY() - waterHeight);
			camera.getTransform().move(0, -cameraReflectMoveDistance, 0);
			camera.getTransform().setRotX(-camera.getTransform().getRotX());
			scene.render(camera, 1f, waterHeight - .05f, false);
			camera.getTransform().move(0, cameraReflectMoveDistance, 0);
			camera.getTransform().setRotX(-camera.getTransform().getRotX());
			
			scene.getWaterFBOs().bindRefractionFBO();
			scene.render(camera, -1f, waterHeight + .05f, false);
			
			scene.getWaterFBOs().unbindCurrentFBO();
			scene.render(camera, 1f, -999999f, true);
			
			GuiRenderer.render();
			
			Window.swapBuffers();
		}
		
		socketManager.closeConnection();
		scene.getWaterFBOs().cleanUp();
		TextureLoader.cleanUp();
		Window.cleanUp();
		ModelLoader.cleanUp();
		GuiRenderer.cleanUp();
	}
	
	private static void doNetStuff(List<Entity> otherPlayers, Player player) {
		NetTransform[] playerNetTransforms = socketManager.getPlayerNetTransforms();
		while (playerNetTransforms.length > otherPlayers.size())
			otherPlayers.add(new Player());
		
		int currentOtherPlayer = 0;
		for(NetTransform transform : playerNetTransforms) {	
			if (transform.getID() == socketManager.getOurId())
				continue;
			
			otherPlayers.get(currentOtherPlayer).getTransform().setPosition(transform.getPosX(), transform.getPosY(), transform.getPosZ());
			EntityRenderer.addEntity(otherPlayers.get(currentOtherPlayer));
			currentOtherPlayer++;
		}
		
		
		if ((System.currentTimeMillis() - lastMessageSent) > dataSendDelay) {
			socketManager.sendPosition(player.getTransform().getPosX(), player.getTransform().getPosY(), player.getTransform().getPosZ());
			lastMessageSent = System.currentTimeMillis();
		}
		
		socketManager.readData();
	}
}