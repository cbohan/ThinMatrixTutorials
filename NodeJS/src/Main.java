import entities.*;
import guis.*;
import guis.fonts.*;
import models.*;
import textures.*;
import window.*;

public class Main {	
	public static void main(String[] args) {
		//Init.
		Window.init(1920, 1080);
		
		//Text
		TextMaster.init();
		
		Scene scene = new Scene();
		scene.load("res\\scenes\\scene.xml");
		
		Player player = new Player();
		player.getTransform().setPosZ(-55f);
		scene.addAnimatedEntity(player.getAnimatedEntity());
		
		PlayerCamera camera = new PlayerCamera(player);
		
		GuiButton button = new GuiButton("bonjour", .1f, .1f, .06f, .035f);
		button.setOnClickCallback(new GuiButton.ButtonClickCallback() {
			public void onClick() {
				System.out.println("We clicked the button");
			}
		});
		button.show();
		
		for (int i = 0; i < 200; i++)
		{
			float x = (float)Math.random() * 100f - 50f;
			float z = (float)Math.random() * 100f - 50f;
			float y = scene.getTerrainGroup().getHeight(x, z);
			
			Entity fern = new Entity("res\\foliage\\ferns\\fern.obj", "res\\foliage\\ferns\\fern.png", true);
			fern.getTexturedModel().getTexture().setHasTransparency(false);
			fern.getTransform().setPosition(x, y, z);
			scene.addEntity(fern);
		}
				
		while (Window.shouldClose() == false) {
			Window.clearBuffer(camera);
			Window.pollEvents();
			
			player.move(scene.getTerrainGroup());
			camera.update();
												
			scene.getWaterFBOs().bindReflectionFBO();
			float cameraReflectMoveDistance = 2 * (camera.getTransform().getPosY() - scene.getWaterHeight());
			camera.getTransform().move(0, -cameraReflectMoveDistance, 0);
			camera.getTransform().setRotX(-camera.getTransform().getRotX());
			scene.render(camera, 1f, scene.getWaterHeight() - .05f, false);
			camera.getTransform().move(0, cameraReflectMoveDistance, 0);
			camera.getTransform().setRotX(-camera.getTransform().getRotX());
			
			scene.getWaterFBOs().bindRefractionFBO();
			scene.render(camera, -1f, scene.getWaterHeight() + .05f, false);
			
			scene.getWaterFBOs().unbindCurrentFBO();
			scene.render(camera, 1f, -999999f, true);
			
			GuiButton.update();
			GuiRenderer.render();
			TextMaster.render();
			
			Window.swapBuffers();
		}
		
		//socketManager.closeConnection();
		scene.getWaterFBOs().cleanUp();
		TextureLoader.cleanUp();
		Window.cleanUp();
		ModelLoader.cleanUp();
		GuiRenderer.cleanUp();
		TextMaster.cleanUp();
	}
}