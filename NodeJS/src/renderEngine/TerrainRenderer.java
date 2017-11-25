package renderEngine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.*;
import entities.*;
import shaders.*;
import terrains.Terrain;

public class TerrainRenderer {
	private TerrainShader shader = new TerrainShader();
	List<Terrain> terrains = new ArrayList<Terrain>();
	
	public void addTerrain(Terrain terrain) {
		terrains.add(terrain);
	}
	
	public void render(Scene scene, Camera camera) {
		shader.start();
		shader.loadLight(scene.getLight());
		shader.loadViewMatrix(camera.getViewMatrix());
		shader.loadProjectionMatrix(camera.getProjectionMatrix());
		shader.loadSkyColor(camera.getSkyRed(), camera.getSkyGreen(), camera.getSkyBlue());
		shader.loadFogDensity(camera.getFogDensity());
		shader.loadFogGradient(camera.getFogGradient());
		
		for(Terrain terrain:terrains) {
			shader.loadSpecularValues(1, 0);
			glBindVertexArray(terrain.getModel().getVaoId());
			for (int i = 0; i < 3; i++)
				glEnableVertexAttribArray(i);
			
			shader.connectTextureUnits();
			terrain.getTexturePack().getBackgroundTexture().bind(0);
			terrain.getTexturePack().getRTexture().bind(1);
			terrain.getTexturePack().getGTexture().bind(2);
			terrain.getTexturePack().getBTexture().bind(3);
			terrain.getSplatMap().bind(4);
			
			shader.loadTransformationMatrix(terrain.getTransform().getMatrix());
			glDrawElements(GL_TRIANGLES, terrain.getModel().getVertexCount(), GL_UNSIGNED_INT, 0);
			
			for (int i = 0; i < 3; i++)
				glDisableVertexAttribArray(i);
			glBindVertexArray(0);
		}
		
		shader.stop();
		terrains.clear();
	}
}
