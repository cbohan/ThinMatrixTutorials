package guis;
  
import shaders.*;
 
public class GuiShader extends ShaderProgram{
     
    private static final String VERTEX_FILE = "res\\shaders\\guiVertexShader.glsl";
    private static final String FRAGMENT_FILE = "res\\shaders\\guiFragmentShader.glsl";
     
    public UniformVec2 translation = new UniformVec2("translation");
    public UniformVec2 scale = new UniformVec2("scale");
    public UniformVec3 color = new UniformVec3("color");
    public UniformFloat useColor = new UniformFloat("useColor");
 
    public GuiShader() {
        super(VERTEX_FILE, FRAGMENT_FILE, "position");
        super.storeAllUniformLocations(translation, scale, color, useColor);
    }
}