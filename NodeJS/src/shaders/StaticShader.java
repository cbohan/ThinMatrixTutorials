package shaders;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import entities.Light;


public class StaticShader extends ShaderProgram {
	private static final String VERTEX_FILE = "src/shaders/vertexShader.glsl";
	private static final String FRAGMENT_FILE = "src/shaders/fragmentShader.glsl";
	
	private int transformationMatrixLocation;
	private int projectionMatrixLocation;
	private int viewMatrixLocation;
	
	private int lightPositionLocation;
	private int lightColorLocation;
	
	private int shineDampingLocation;
	private int reflectivityLocation;
	
	private int overrideNormalsLocation;
	
	private int skyColorLocation;
	private int fogDensityLocation;
	private int fogGradientLocation;
	
	private int textureNumberOfRowsLocation;
	private int textureOffsetLocation;
	
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
		lightPositionLocation = super.getUniformLocation("lightPosition");
		lightColorLocation = super.getUniformLocation("lightColor");
		shineDampingLocation = super.getUniformLocation("shineDamping");
		reflectivityLocation = super.getUniformLocation("reflectivity");
		overrideNormalsLocation = super.getUniformLocation("overrideNormals");
		skyColorLocation = super.getUniformLocation("skyColor");
		fogDensityLocation = super.getUniformLocation("fogDensity");
		fogGradientLocation = super.getUniformLocation("fogGradient");
		textureNumberOfRowsLocation = super.getUniformLocation("textureNumberOfRows");
		textureOffsetLocation = super.getUniformLocation("textureOffset");
	}
	
	public void loadTransformationMatrix(Matrix4f matrix) { super.loadMatrix(transformationMatrixLocation, matrix); }
	public void loadProjectionMatrix(Matrix4f matrix) { super.loadMatrix(projectionMatrixLocation, matrix); }
	public void loadViewMatrix(Matrix4f matrix) { super.loadMatrix(viewMatrixLocation, matrix); }
	
	public void loadLight(Light light){
		loadLightPosition(light.getPosition());
		loadLightColor(light.getColor());
	}
	private void loadLightPosition(Vector3f lightPosition) { super.loadVec3(lightPositionLocation, lightPosition); }
	private void loadLightColor(Vector3f lightColor) { super.loadVec3(lightColorLocation, lightColor); }
	
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
}
