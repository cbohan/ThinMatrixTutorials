package guis;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import models.*;

public class GuiRenderer {
	private static final RawModel quad = ModelLoader.loadToVAO(new float[] {0, 1, 0, 0, 1, 1, 1, 0});;
	private static List<GuiTexture> guis = new ArrayList<GuiTexture>();
	private static GuiShader shader = new GuiShader();
	
	public static void addGUI(GuiTexture gui) {
		guis.add(gui);
	}
	
	public static void removeGUI(GuiTexture gui) {
		guis.remove(gui);
	}
	
	public static void render() {
		shader.start();		
		glBindVertexArray(quad.getVaoId());
		glEnableVertexAttribArray(0);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);
		
		for(GuiTexture gui : guis) {
			if (gui.getTexture() != null)
				gui.getTexture().bind(0);
			
			shader.translation.loadVec2(gui.getPosition());
			shader.scale.loadVec2(gui.getScale());
			shader.color.loadVec3(gui.getColor());
			shader.useColor.loadFloat(gui.getUseColor());
			
			glDrawArrays(GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		
		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
		glDisable(GL_BLEND);
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
		shader.stop();
	}
	
	public static void cleanUp() {
		shader.cleanUp();
	}
}
