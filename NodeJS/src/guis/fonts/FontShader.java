package guis.fonts;

import shaders.*;

public class FontShader extends ShaderProgram {
	private static final String VERTEX_FILE = "res\\shaders\\fontVertexShader.glsl";
	private static final String FRAGMENT_FILE = "res\\shaders\\fontFragmentShader.glsl";
	
	public UniformVec2 translation = new UniformVec2("translation");
	public UniformVec3 color = new UniformVec3("color");
	
	public FontShader() {
		super(VERTEX_FILE, FRAGMENT_FILE, "position", "textureCoords");
		super.storeAllUniformLocations(translation, color);
	}
}
