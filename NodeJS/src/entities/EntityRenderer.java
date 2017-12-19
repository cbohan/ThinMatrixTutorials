package entities;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.*;
import models.*;
import shaders.StaticShader;

public class EntityRenderer {	
	private static StaticShader shader = new StaticShader();
	
	private static Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	
	public static void addEntity(Entity entity) {
		if(entities.containsKey(entity.getTexturedModel()) == false)
			entities.put(entity.getTexturedModel(), new ArrayList<Entity>());
		entities.get(entity.getTexturedModel()).add(entity);
	}
	
	public static void render(Scene scene, Camera camera, float clippingPlaneDirection, float clippingPlaneHeight) {
		shader.start();
		shader.loadClipPlane(0, clippingPlaneDirection, 0, clippingPlaneHeight);
		shader.loadLights(scene.getLights());
		shader.loadViewMatrix(camera.getViewMatrix());
		shader.loadProjectionMatrix(camera.getProjectionMatrix());
		shader.loadSkyColor(camera.getSkyRed(), camera.getSkyGreen(), camera.getSkyBlue());
		shader.loadFogDensity(camera.getFogDensity());
		shader.loadFogGradient(camera.getFogGradient());
		
		for (TexturedModel model : entities.keySet()){
			shader.loadSpecularValues(model.getShineDamping(), model.getReflectivity());
			RawModel rawModel = model.getRawModel();
			glBindVertexArray(rawModel.getVaoId());
			for (int i = 0; i < 3; i++)
				glEnableVertexAttribArray(i);
			model.getTexture().bind(0);
			
			if (model.getTexture().getHasTransparency()) 
				glDisable(GL_CULL_FACE);
			
			shader.loadOverrideNormals(model.getTexture().getOverrideNormals());
			
			List<Entity> batch = entities.get(model);
			for (Entity entity: batch) {
				shader.loadTransformationMatrix(entity.getTransform().getMatrix());
				shader.loadTextureInfo(entity.getTexturedModel().getTexture().getNumberOfRows(), 
						entity.getTextureXOffset(), entity.getTextureYOffset());
				glDrawElements(GL_TRIANGLES, rawModel.getVertexCount(), GL_UNSIGNED_INT, 0);
			}
			
			if (model.getTexture().getHasTransparency()) {
				glEnable(GL_CULL_FACE);
				glCullFace(GL_BACK);
			}
			
			for (int i = 0; i < 3; i++)
				glDisableVertexAttribArray(i);
			glBindVertexArray(0);
		}
		
		shader.stop();
		entities.clear();
	}
}
