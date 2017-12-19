package shaders;

import java.util.List;

import org.joml.*;

import entities.Light;


public class StaticShader extends ShaderProgram {
	private static final int MAX_LIGHTS = 16;
	
	private static final String VERTEX_FILE = "src/shaders/vertexShader.glsl";
	private static final String FRAGMENT_FILE = "src/shaders/fragmentShader.glsl";
	
	private int transformationMatrixLocation;
	private int projectionMatrixLocation;
	private int viewMatrixLocation;
	private int lightPositionLocation[];
	private int lightColorLocation[];
	private int lightAttenuationFactorLocation[];
	private int shineDampingLocation;
	private int reflectivityLocation;
	private int overrideNormalsLocation;
	private int skyColorLocation;
	private int fogDensityLocation;
	private int fogGradientLocation;
	private int textureNumberOfRowsLocation;
	private int textureOffsetLocation;
	private int clipPlaneLocation;
	
	public StaticShader() {
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
		shineDampingLocation = super.getUniformLocation("shineDamping");
		reflectivityLocation = super.getUniformLocation("reflectivity");
		overrideNormalsLocation = super.getUniformLocation("overrideNormals");
		skyColorLocation = super.getUniformLocation("skyColor");
		fogDensityLocation = super.getUniformLocation("fogDensity");
		fogGradientLocation = super.getUniformLocation("fogGradient");
		textureNumberOfRowsLocation = super.getUniformLocation("textureNumberOfRows");
		textureOffsetLocation = super.getUniformLocation("textureOffset");
		clipPlaneLocation = super.getUniformLocation("clipPlane");
		
		lightPositionLocation = new int[MAX_LIGHTS];
		lightColorLocation = new int[MAX_LIGHTS];
		lightAttenuationFactorLocation = new int[MAX_LIGHTS];
		for(int i = 0; i < MAX_LIGHTS; i++) {
			lightPositionLocation[i] = super.getUniformLocation("lightPosition["+i+"]");
			lightColorLocation[i] = super.getUniformLocation("lightColor["+i+"]");
			lightAttenuationFactorLocation[i] = super.getUniformLocation("lightAttenuationFactor["+i+"]");
		}
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
	
	public void loadSpecularValues(float shineDamping, float reflectivity) {
		super.loadFloat(shineDampingLocation, shineDamping);
		super.loadFloat(reflectivityLocation, reflectivity);
	}
	
	public void loadOverrideNormals(boolean override) { super.loadFloat(overrideNormalsLocation, (override ? 1f : 0f));}
	
	public void loadSkyColor(float r, float g, float b) { super.loadVec3(skyColorLocation, new Vector3f(r, g, b)); };
	public void loadFogDensity(float density) { super.loadFloat(fogDensityLocation, density); }
	public void loadFogGradient(float gradient) { super.loadFloat(fogGradientLocation, gradient); }
	
	public void loadTextureInfo(float textureNumberOfRows, float textureOffsetX, float textureOffsetY) {
		super.loadFloat(textureNumberOfRowsLocation, textureNumberOfRows);
		super.loadVec2(textureOffsetLocation, new Vector2f(textureOffsetX, textureOffsetY));
	}
	
	public void loadClipPlane(float a, float b, float c, float d) { super.loadVec4(clipPlaneLocation, new Vector4f(a, b, c, d)); }
}
