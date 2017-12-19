package water;

import java.util.List;

import org.joml.*;

import entities.Light;
import shaders.ShaderProgram;


public class WaterShader extends ShaderProgram {
	private static final int MAX_LIGHTS = 16;
	
	private static final String VERTEX_FILE = "src/water/waterVertexShader.glsl";
	private static final String FRAGMENT_FILE = "src/water/waterFragmentShader.glsl";
	
	private int transformationMatrixLocation;
	private int projectionMatrixLocation;
	private int viewMatrixLocation;
	private int lightPositionLocation[];
	private int lightColorLocation[];
	private int lightAttenuationFactorLocation[];
	private int skyColorLocation;
	private int fogDensityLocation;
	private int fogGradientLocation;
	private int reflectionTextureLocation;
	private int refractionTextureLocation;
	private int waterDUDVMapLocation;
	private int waterNormalMapLocation;
	private int waterDepthMapLocation;
	private int moveFactorLocation;
	private int nearPlaneLocation;
	private int farPlaneLocation;
	
	public WaterShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normals");
	}
	
	protected void getAllUniformLocations() {
		transformationMatrixLocation = super.getUniformLocation("transformationMatrix");
		projectionMatrixLocation = super.getUniformLocation("projectionMatrix");
		viewMatrixLocation = super.getUniformLocation("viewMatrix");
		skyColorLocation = super.getUniformLocation("skyColor");
		fogDensityLocation = super.getUniformLocation("fogDensity");
		fogGradientLocation = super.getUniformLocation("fogGradient");
		reflectionTextureLocation = super.getUniformLocation("reflectionTexture");
		refractionTextureLocation = super.getUniformLocation("refractionTexture");
		waterDUDVMapLocation = super.getUniformLocation("waterDUDVMap");
		waterNormalMapLocation = super.getUniformLocation("waterNormalMap");
		waterDepthMapLocation = super.getUniformLocation("waterDepthMap");
		moveFactorLocation = super.getUniformLocation("moveFactor");
		nearPlaneLocation = super.getUniformLocation("nearPlane");
		farPlaneLocation = super.getUniformLocation("farPlane");
		
		lightPositionLocation = new int[MAX_LIGHTS];
		lightColorLocation = new int[MAX_LIGHTS];
		lightAttenuationFactorLocation = new int[MAX_LIGHTS];
		for(int i = 0; i < MAX_LIGHTS; i++) {
			lightPositionLocation[i] = super.getUniformLocation("lightPosition["+i+"]");
			lightColorLocation[i] = super.getUniformLocation("lightColor["+i+"]");
			lightAttenuationFactorLocation[i] = super.getUniformLocation("lightAttenuationFactor["+i+"]");
		}
	}
	
	public void connectTextureUnits() {
		super.loadInt(reflectionTextureLocation, 0);
		super.loadInt(refractionTextureLocation, 1);
		super.loadInt(waterDUDVMapLocation, 2);
		super.loadInt(waterNormalMapLocation, 3);
		super.loadInt(waterDepthMapLocation, 4);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix) { super.loadMatrix(transformationMatrixLocation, matrix); }
	public void loadProjectionMatrix(Matrix4f matrix) { super.loadMatrix(projectionMatrixLocation, matrix); }
	public void loadViewMatrix(Matrix4f matrix) { super.loadMatrix(viewMatrixLocation, matrix); }
	
	public void loadLights(List<Light> lights){
		for (int i = 0; i < MAX_LIGHTS; i++) {
			if (i < lights.size()){
				loadLightPosition(lights.get(i).getPosition(), i);
				loadLightColor(lights.get(i).getColorTimesStrength(), i);
				loadLightAttenuationFactor(lights.get(i).getAttenuationFactor(), i);
			}else{
				loadLightPosition(new Vector3f(0, 0, 0), i);
				loadLightColor(new Vector3f(0, 0, 0), i);
				loadLightAttenuationFactor(1, i);
			}
		}
	}
	private void loadLightPosition(Vector3f lightPosition, int i) { super.loadVec3(lightPositionLocation[i], lightPosition); }
	private void loadLightColor(Vector3f lightColor, int i) { super.loadVec3(lightColorLocation[i], lightColor); }
	private void loadLightAttenuationFactor(float attenuation, int i) {super.loadFloat(lightAttenuationFactorLocation[i], attenuation);}
	public void loadSkyColor(float r, float g, float b) { super.loadVec3(skyColorLocation, new Vector3f(r, g, b)); };
	public void loadFogDensity(float density) { super.loadFloat(fogDensityLocation, density); }
	public void loadFogGradient(float gradient) { super.loadFloat(fogGradientLocation, gradient); }	
	public void loadMoveFactor(float factor) { super.loadFloat(moveFactorLocation, factor); }
	public void loadCameraPlanes(float nearPlane, float farPlane) {
		super.loadFloat(nearPlaneLocation, nearPlane);
		super.loadFloat(farPlaneLocation, farPlane);
	}
}
