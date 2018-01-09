package shaders;

import java.util.List;

import org.joml.Vector3f;

import entities.Light;

public class TerrainShader extends ShaderProgram {
	private static final int MAX_LIGHTS = 16;
	
	private static final String VERTEX_FILE = "res\\shaders\\terrainVertexShader.glsl";
	private static final String FRAGMENT_FILE = "res\\shaders\\terrainFragmentShader.glsl";
	
	public UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
	public UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
	public UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
	
	public UniformVec3Array lightPosition = new UniformVec3Array("lightPosition", MAX_LIGHTS);
	public UniformVec3Array lightColor = new UniformVec3Array("lightColor", MAX_LIGHTS);
	public UniformFloatArray lightAttenuationFactor = new UniformFloatArray("lightAttenuationFactor", MAX_LIGHTS);
	public UniformFloat shineDamping = new UniformFloat("shineDamping");
	public UniformFloat reflectivity = new UniformFloat("reflectivity");
	
	public UniformVec3 skyColor = new UniformVec3("skyColor");
	public UniformFloat fogDensity = new UniformFloat("fogDensity");
	public UniformFloat fogGradient = new UniformFloat("fogGradient");
	public UniformVec4 clipPlane = new UniformVec4("clipPlane");
	
	private UniformInt backgroundTexture = new UniformInt("backgroundTexture");
	private UniformInt rTexture = new UniformInt("rTexture");
	private UniformInt gTexture = new UniformInt("gTexture");
	private UniformInt bTexture = new UniformInt("bTexture");
	private UniformInt splatMap = new UniformInt("splatMap");
	
	public TerrainShader() {
		super(VERTEX_FILE, FRAGMENT_FILE, "position", "textureCoords", "normals");
		super.storeAllUniformLocations(transformationMatrix, projectionMatrix, viewMatrix, lightPosition, lightColor, 
				lightAttenuationFactor, shineDamping, reflectivity, skyColor, fogDensity, fogGradient, clipPlane, 
				backgroundTexture, rTexture, gTexture, bTexture, splatMap);
	}
	
	public void connectTextureUnits() {
		backgroundTexture.loadInt(0);
		rTexture.loadInt(1);
		gTexture.loadInt(2);
		bTexture.loadInt(3);
		splatMap.loadInt(4);
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
