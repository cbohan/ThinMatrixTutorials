import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.glfw.*;

import entities.*;
import models.*;
import networking.*;
import renderEngine.*;
import terrains.*;
import textures.*;

public class Main {
	private static SocketManager socketManager;
	private static ModelLoader modelLoader;
	private static TerrainRenderer terrainRenderer;
	private static EntityRenderer entityRenderer;
	
	private static long lastMessageSent = 0;
	private static long dataSendDelay = 50;
		
	public static void main(String[] args) {
		
		Window.init(1280, 720);
		init();
		
		Camera camera = new Camera();
		camera.getTransform().setPosition(0, 5f, 15f);
		camera.setSkyColor(.8f, .97f, 1f);
		camera.setFogDensity(.0005f);
		camera.setFogGradient(1f);
		
		Light light = new Light(new Vector3f(-5f, 10f, 0f), new Vector3f(1f, 1f, 1f));
		
		Scene scene = new Scene();
		scene.setLight(light);
		
		Entity entity = new Entity("res\\capsule.obj", "res\\testTexture.png");
		entity.getTexturedModel().setShineDamping(16f);
		entity.getTexturedModel().setReflectivity(.2f);
		entity.getTransform().setPosZ(-15f);
		
		TerrainGroup terrainGroup = new TerrainGroup(3, 3, modelLoader, 
			new TerrainTexturePack("res\\grass1.png", "res\\dirt1.png", "res\\stone_brick1.png", "res\\stone1.png"), TextureLoader.loadTexture("res\\terrain_0_0_splat.png"));
		
		Entity grass = new Entity("res\\grass_bill.obj", "res\\grass_bill1.png");
		grass.getTransform().setPosition(0f, 0f, -5f);
		grass.getTexturedModel().getTexture().setHasTransparency(true);
		grass.getTexturedModel().getTexture().setOverrideNormals(true);
		
		List<Entity> otherPlayers = new ArrayList<Entity>();

		while (Window.shouldClose() == false) {
			Window.clearBuffer(camera);
			Window.pollEvents();
			
			moveCamera(camera);
			
			terrainGroup.addToRenderer(terrainRenderer);
			terrainRenderer.render(scene, camera);
						
			entity.getTransform().move(.01f, 0, 0);
			entityRenderer.addEntity(entity);
			
			entityRenderer.addEntity(grass);
			
			doNetStuff(otherPlayers, entity, entity.getTexturedModel());
			
			entityRenderer.render(scene, camera);
			
			Window.swapBuffers();
			
			
		}
		
		socketManager.closeConnection();
		TextureLoader.cleanUp();
		Window.cleanUp();
		modelLoader.cleanUp();
	}
	
	public static void init(){
		socketManager = new SocketManager("127.0.0.1", 2000);
		modelLoader = new ModelLoader();
		entityRenderer = new EntityRenderer();
		terrainRenderer = new TerrainRenderer();
	}
	
	private static void doNetStuff(List<Entity> otherPlayers, Entity entity, TexturedModel texturedModel) {
		NetTransform[] playerNetTransforms = socketManager.getPlayerNetTransforms();
		while (playerNetTransforms.length > otherPlayers.size())
			otherPlayers.add(new Entity(texturedModel));
		
		int currentOtherPlayer = 0;
		for(NetTransform transform : playerNetTransforms) {	
				if (transform.getID() == socketManager.getOurId())
				continue;
			
			otherPlayers.get(currentOtherPlayer).getTransform().setPosition(transform.getPosX(), transform.getPosY(), transform.getPosZ());
			entityRenderer.addEntity(otherPlayers.get(currentOtherPlayer));
			currentOtherPlayer++;
		}
		
		
		if ((System.currentTimeMillis() - lastMessageSent) > dataSendDelay) {
			socketManager.sendPosition(entity.getTransform().getPosX(), entity.getTransform().getPosY(), entity.getTransform().getPosZ());
			lastMessageSent = System.currentTimeMillis();
		}
		
		socketManager.readData();
	}
	
	private static void moveCamera(Camera camera) {
		float speed = 3f;
		float turnSpeed = 5f;
		
		if (Window.getKey(GLFW.GLFW_KEY_LEFT_SHIFT))
			speed *= 10;
		
		if (Window.getKey(GLFW.GLFW_KEY_W))
			camera.getTransform().move(0, 0, -.02f * speed);
		if (Window.getKey(GLFW.GLFW_KEY_S))
			camera.getTransform().move(0, 0, .02f * speed);
		if (Window.getKey(GLFW.GLFW_KEY_A))
			camera.getTransform().move(-.02f * speed, 0, 0);
		if (Window.getKey(GLFW.GLFW_KEY_D))
			camera.getTransform().move(.02f * speed, 0, 0);
		if (Window.getKey(GLFW.GLFW_KEY_Q))
			camera.getTransform().move(0, .02f * speed, 0);
		if (Window.getKey(GLFW.GLFW_KEY_E))
			camera.getTransform().move(0, -.02f * speed, 0);
		
		if (Window.getKey(GLFW.GLFW_KEY_UP))
			camera.getTransform().turn(-.5f * turnSpeed, 0, 0);
		if (Window.getKey(GLFW.GLFW_KEY_DOWN))
			camera.getTransform().turn(.5f * turnSpeed, 0, 0);
		if (Window.getKey(GLFW.GLFW_KEY_LEFT))
			camera.getTransform().turn(0, -.5f * turnSpeed, 0);
		if (Window.getKey(GLFW.GLFW_KEY_RIGHT))
			camera.getTransform().turn(0, .5f * turnSpeed, 0);
	}
}
