import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import entities.*;
import models.*;
import networking.*;
import renderEngine.*;
import terrains.*;
import textures.*;

public class Main {
	private static SocketManager socketManager;
	
	private static long lastMessageSent = 0;
	private static long dataSendDelay = 50;
		
	public static void main(String[] args) {
		//Init.
		Window.init(1280, 720);
		socketManager = new SocketManager("127.0.0.1", 2000);
		
		Scene scene = new Scene();
		Light light = new Light(new Vector3f(5000f, 10000f, 0f), new Vector3f(1f, 1f, 1f));
		scene.setLight(light);
		
		Player player = new Player();
		player.getTransform().setPosZ(0);
		scene.addEntity(player);
		
		PlayerCamera camera = new PlayerCamera(player);
		
		TerrainGroup terrainGroup = new TerrainGroup(3, 3, new TerrainTexturePack("res\\grass1.png", "res\\dirt1.png", 
				"res\\stone_brick1.png", "res\\stone1.png"), TextureLoader.loadTexture("res\\terrain_0_0_splat.png"),
				"res\\terrain_0_0_height.png");
		scene.setTerrainGroup(terrainGroup);
		
		for (int i = 0; i < 3000; i++) {
			Entity grass = new Entity("res\\grass_bill.obj", "res\\plant_bill_atlas_1.png", (int)(Math.random()*4), 2);
			grass.getTransform().setScale(2f);
			float xPos = (float)(Math.random() - .5f) * 300f;
			float zPos = (float)(Math.random() - .5f) * 300f;
			float yPos = terrainGroup.getHeight(xPos, zPos);
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
			
			doNetStuff(otherPlayers, player);
			
			scene.render(camera);
			
			Window.swapBuffers();
		}
		
		socketManager.closeConnection();
		TextureLoader.cleanUp();
		Window.cleanUp();
		ModelLoader.cleanUp();
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