package water;

import java.util.List;

import org.joml.*;

import entities.Light;
import shaders.ShaderProgram;
import shaders.UniformFloat;
import shaders.UniformFloatArray;
import shaders.UniformInt;
import shaders.UniformMatrix;
import shaders.UniformVec3;
import shaders.UniformVec3Array;


public class WaterShader extends ShaderProgram {
	private static final int MAX_LIGHTS = 16;
	
	private static final String VERTEX_FILE = "res\\shaders\\waterVertexShader.glsl";
	private static final String FRAGMENT_FILE = "res\\shaders\\waterFragmentShader.glsl";
	
	public UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
	public UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
	public UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
	
	public UniformVec3Array lightPosition = new UniformVec3Array("lightPosition", MAX_LIGHTS);
	public UniformVec3Array lightColor = new UniformVec3Array("lightColor", MAX_LIGHTS);
	public UniformFloatArray lightAttenuationFactor = new UniformFloatArray("lightAttenuationFactor", MAX_LIGHTS);
	
	public UniformVec3 skyColor = new UniformVec3("skyColor");
	public UniformFloat fogDensity = new UniformFloat("fogDensity");
	public UniformFloat fogGradient = new UniformFloat("fogGradient");
	
	public UniformFloat moveFactor = new UniformFloat("moveFactor");
	public UniformFloat nearPlane = new UniformFloat("nearPlane");
	public UniformFloat farPlane = new UniformFloat("farPlane");
	
	private UniformInt reflectionTexture = new UniformInt("reflectionTexture");
	private UniformInt refractionTexture = new UniformInt("refractionTexture");
	private UniformInt waterDUDVMap = new UniformInt("waterDUDVMap");
	private UniformInt waterNormalMap = new UniformInt("waterNormalMap");
	private UniformInt waterDepthMap = new UniformInt("waterDepthMap");
	
	public WaterShader() {
		super(VERTEX_FILE, FRAGMENT_FILE, "position", "textureCoords", "normals");
		super.storeAllUniformLocations(transformationMatrix, projectionMatrix, viewMatrix, lightPosition, lightColor,
				lightAttenuationFactor, skyColor, fogDensity, fogGradient, moveFactor, nearPlane, farPlane, 
				reflectionTexture, refractionTexture, waterDUDVMap, waterNormalMap, waterDepthMap);
	}
	
	public void connectTextureUnits() {
		reflectionTexture.loadInt(0);
		refractionTexture.loadInt(1);
		waterDUDVMap.loadInt(2);
		waterNormalMap.loadInt(3);
		waterDepthMap.loadInt(4);
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
