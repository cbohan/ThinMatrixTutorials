package shaders;

import java.util.List;

import org.joml.*;

import entities.Light;


public class StaticShader extends ShaderProgram {
	private static final int MAX_LIGHTS = 16;
	
	private static final String VERTEX_FILE = "res\\shaders\\vertexShader.glsl";
	private static final String FRAGMENT_FILE = "res\\shaders\\fragmentShader.glsl";
	
	public UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
	public UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
	public UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
	
	public UniformVec3Array lightPosition = new UniformVec3Array("lightPosition", MAX_LIGHTS);
	public UniformVec3Array lightColor = new UniformVec3Array("lightColor", MAX_LIGHTS);
	public UniformFloatArray lightAttenuationFactor = new UniformFloatArray("lightAttenuationFactor", MAX_LIGHTS);
	public UniformFloat shineDamping = new UniformFloat("shineDamping");
	public UniformFloat reflectivity = new UniformFloat("reflectivity");
	
	public UniformFloat overrideNormals = new UniformFloat("overrideNormals");
	public UniformVec3 skyColor = new UniformVec3("skyColor");
	public UniformFloat fogDensity = new UniformFloat("fogDensity");
	public UniformFloat fogGradient = new UniformFloat("fogGradient");
	public UniformFloat textureNumberOfRows = new UniformFloat("textureNumberOfRows");
	public UniformVec2 textureOffset = new UniformVec2("textureOffset");
	public UniformVec4 clipPlane = new UniformVec4("clipPlane");
	
	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE, "position", "textureCoords", "normals");
		super.storeAllUniformLocations(transformationMatrix, projectionMatrix, viewMatrix, lightPosition, lightColor, 
				lightAttenuationFactor, shineDamping, reflectivity, overrideNormals, skyColor, fogDensity, fogGradient,
				textureNumberOfRows, textureOffset, clipPlane);
	}
	
	public void loadLights(List<Light> lights){
		for (int i = 0; i < MAX_LIGHTS; i++) {
			if (i < lights.size()){
				lightPosition.loadVec3(i, lights.get(i).getPosition());
				lightColor.loadVec3(i, lights.get(i).getColorTimesStrength());
				lightAttenuationFactor.loadFloat(i, lights.get(i).getAttenuationFactor());
			}else{
				lightPosition.loadVec3(i, new Vector3f(0, 0, 0));
				lightColor.loadVec3(i, new Vector3f(0, 0, 0));
				lightAttenuationFactor.loadFloat(i, 0);
			}
		}
	}
}
