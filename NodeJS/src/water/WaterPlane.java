package water;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import entities.*;
import models.*;
import textures.*;
import window.Window;

public class WaterPlane {
	private final String WATER_DUDV_MAP_LOCATION = "res\\water\\waterDUDV.png";
	private final String WATER_NORMAL_MAP_LOCATION = "res\\water\\waterNormalMap.png";
	private final float WAVE_SPEED = .04f;
	
	private static WaterShader shader = new WaterShader();
	
	private RawModel rawModel;
	private Transform transform;
	private WaterFrameBuffers waterFBOs;
	private Texture waterDUDVMap;
	private Texture waterNormalMap;
	private float moveFactor;
	
	public WaterPlane(WaterFrameBuffers waterFBOs) {
		rawModel = ModelLoader.loadToVAO(
			new float[] { 	-2000f, 0f, -2000f, 
							2000f, 0f, 2000f,
							-2000f, 0f, 2000f, 
							2000f, 0f, -2000f }, 
			new float[] { 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f },
			new float[] { 0f, 0f, 1f, 1f, 0f, 1f, 1f, 0f },
			new int[] { 1, 0, 2, 0, 1, 3 });
		
		transform = new Transform();
		this.waterFBOs = waterFBOs;
		waterDUDVMap = TextureLoader.loadTexture(WATER_DUDV_MAP_LOCATION);
		waterNormalMap = TextureLoader.loadTexture(WATER_NORMAL_MAP_LOCATION);
		moveFactor = 0;
	}
	
	public Transform getTransform() { return transform; }
	
	public void render(Scene scene, Camera camera) {
		shader.start();
		shader.connectTextureUnits();
		waterFBOs.getReflectionTexture().bind(0);
		waterFBOs.getRefractionTexture().bind(1);
		waterDUDVMap.bind(2);
		waterNormalMap.bind(3);
		waterFBOs.getRefractionDepthTexture().bind(4);
		moveFactor += WAVE_SPEED * Window.getDeltaTime();
		moveFactor %= 1;
		shader.moveFactor.loadFloat(moveFactor);
		shader.loadLights(scene.getLights());
		shader.viewMatrix.loadMatrix(camera.getViewMatrix());
		shader.projectionMatrix.loadMatrix(camera.getProjectionMatrix());
		shader.skyColor.loadVec3(camera.getSkyRed(), camera.getSkyGreen(), camera.getSkyBlue());
		shader.fogDensity.loadFloat(camera.getFogDensity());
		shader.fogGradient.loadFloat(camera.getFogGradient());
		shader.nearPlane.loadFloat(camera.getNearPlane());
		shader.farPlane.loadFloat(camera.getFarPlane());
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		glBindVertexArray(rawModel.getVaoId());
		for (int i = 0; i < 3; i++)
			glEnableVertexAttribArray(i);

		shader.transformationMatrix.loadMatrix(transform.getMatrix());
		glDrawElements(GL_TRIANGLES, rawModel.getVertexCount(), GL_UNSIGNED_INT, 0);

		
		for (int i = 0; i < 3; i++)
			glDisableVertexAttribArray(i);
		glBindVertexArray(0);
		
		glDisable(GL_BLEND);
		
		shader.stop();
	}
}
