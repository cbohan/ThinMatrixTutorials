package animation;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entities.Camera;
import entities.Scene;
import models.RawModel;

public class AnimatedModelRenderer {
	private static AnimatedModelShader shader = new AnimatedModelShader();
	
	private static Map<AnimatedModel, List<AnimatedEntity>> entities = new HashMap<AnimatedModel, List<AnimatedEntity>>();
	
	public static void addAnimatedEntity(AnimatedEntity entity) {
		if(entities.containsKey(entity.getAnimatedModel()) == false)
			entities.put(entity.getAnimatedModel(), new ArrayList<AnimatedEntity>());
		entities.get(entity.getAnimatedModel()).add(entity);
	}
	
	public static void render(Scene scene, Camera camera, float clippingPlaneDirection, float clippingPlaneHeight) {
		shader.start();
		shader.clipPlane.loadVec4(0, clippingPlaneDirection, 0, clippingPlaneHeight);
		shader.loadLights(scene.getLights());
		shader.viewMatrix.loadMatrix(camera.getViewMatrix());
		shader.projectionMatrix.loadMatrix(camera.getProjectionMatrix());
		shader.skyColor.loadVec3(camera.getSkyRed(), camera.getSkyGreen(), camera.getSkyBlue());
		shader.fogDensity.loadFloat(camera.getFogDensity());
		shader.fogGradient.loadFloat(camera.getFogGradient());
		
		for (AnimatedModel model : entities.keySet()){
			shader.loadJointTransforms(model.getJointTransforms());
			shader.shineDamping.loadFloat(model.getShineDamping());
			shader.reflectivity.loadFloat(model.getReflectivity());
			RawModel rawModel = model.getRawModel();
			glBindVertexArray(rawModel.getVaoId());
			for (int i = 0; i < 5; i++)
				glEnableVertexAttribArray(i);
			model.getTexture().bind(0);
			
			if (model.getTexture().getHasTransparency()) 
				glDisable(GL_CULL_FACE);
						
			List<AnimatedEntity> batch = entities.get(model);
			for (AnimatedEntity entity: batch) {
				shader.transformationMatrix.loadMatrix(entity.getTransform().getMatrix());
				shader.textureNumberOfRows.loadFloat(1);
				shader.textureOffset.loadVec2(0, 0);
				glDrawElements(GL_TRIANGLES, rawModel.getVertexCount(), GL_UNSIGNED_INT, 0);
			}
			
			if (model.getTexture().getHasTransparency()) {
				glEnable(GL_CULL_FACE);
				glCullFace(GL_BACK);
			}
			
			for (int i = 0; i < 5; i++)
				glDisableVertexAttribArray(i);
			glBindVertexArray(0);
		}
		
		shader.stop();
		entities.clear();
	}
}
