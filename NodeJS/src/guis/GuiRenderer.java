package guis;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import models.*;

public class GuiRenderer {
	private static final RawModel quad = ModelLoader.loadToVAO(new float[] {-1, 1,  -1, -1,  1, 1,  1, -1});;
	private static List<GuiTexture> guis = new ArrayList<GuiTexture>();
	private static GuiShader shader = new GuiShader();
	private static Matrix4f matrix = new Matrix4f();
	
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
		
		for(GuiTexture gui : guis) {
			gui.getTexture().bind(0);
			
			matrix.identity();
			matrix.translate(new Vector3f(gui.getPosition().x, gui.getPosition().y, 0f));
			matrix.scale(new Vector3f(gui.getScale().x, gui.getScale().y, 1f));
			shader.loadTransformation(matrix);
			
			glDrawArrays(GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		
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
