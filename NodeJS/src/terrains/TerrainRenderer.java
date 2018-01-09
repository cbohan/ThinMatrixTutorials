package terrains;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.*;
import entities.*;
import shaders.*;

public class TerrainRenderer {
	private static TerrainShader shader = new TerrainShader();
	private static List<Terrain> terrains = new ArrayList<Terrain>();
	
	public static void addTerrain(Terrain terrain) {
		terrains.add(terrain);
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
		
		for(Terrain terrain:terrains) {
			shader.shineDamping.loadFloat(1);
			shader.reflectivity.loadFloat(0);
			glBindVertexArray(terrain.getModel().getVaoId());
			for (int i = 0; i < 3; i++)
				glEnableVertexAttribArray(i);
			
			shader.connectTextureUnits();
			terrain.getTexturePack().getBackgroundTexture().bind(0);
			terrain.getTexturePack().getRTexture().bind(1);
			terrain.getTexturePack().getGTexture().bind(2);
			terrain.getTexturePack().getBTexture().bind(3);
			terrain.getSplatMap().bind(4);
			
			shader.transformationMatrix.loadMatrix(terrain.getTransform().getMatrix());
			glDrawElements(GL_TRIANGLES, terrain.getModel().getVertexCount(), GL_UNSIGNED_INT, 0);
			
			for (int i = 0; i < 3; i++)
				glDisableVertexAttribArray(i);
			glBindVertexArray(0);
		}
		
		shader.stop();
		terrains.clear();
	}
}
