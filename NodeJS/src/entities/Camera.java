package entities;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import renderEngine.Window;

public class Camera {
	protected float fov = 70f;
	protected float nearPlane = 0.1f;
	protected float farPlane = 2000f;
	
	protected Matrix4f projectionMatrix;
	protected Matrix4f viewMatrix;
	
	protected Transform transform;
	
	protected float skyRed;
	protected float skyGreen;
	protected float skyBlue;
	protected float fogDensity;
	protected float fogGradient;
	
	public Camera() {
		createProjectionMatrix();
		viewMatrix = new Matrix4f();
		viewMatrix.identity();
		transform = new Transform();
		
		skyRed = 0;
		skyGreen = 0;
		skyBlue = 0;
		
		fogDensity = 0.007f;
		fogGradient = 1.5f;
	}
	
	private void createProjectionMatrix() {
		float aspectRatio = (float) Window.getWidth() / (float) Window.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(fov/2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = farPlane - nearPlane;
        
        projectionMatrix = new Matrix4f();
        projectionMatrix.identity();
        projectionMatrix.m00(x_scale);
        projectionMatrix.m11(y_scale);
        projectionMatrix.m22(-((farPlane + nearPlane) / frustum_length));
        projectionMatrix.m23(-1);
        projectionMatrix.m32(-((2 * nearPlane * farPlane) / frustum_length));
        projectionMatrix.m33(0);
	}
	
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
	
	public Matrix4f getViewMatrix() {
		viewMatrix.identity();
		viewMatrix.rotate((float)Math.toRadians(transform.getRotX()), new Vector3f(1, 0, 0));
		viewMatrix.rotate((float)Math.toRadians(transform.getRotY()), new Vector3f(0, 1, 0));
		viewMatrix.rotate((float)Math.toRadians(transform.getRotZ()), new Vector3f(0, 0, 1));
		viewMatrix.translate(new Vector3f(-transform.getPosX(), -transform.getPosY(), -transform.getPosZ()));
		
		return viewMatrix;
	}
	
	public Transform getTransform() {
		return transform;
	}
	
	public void setSkyColor(float r, float g, float b) {
		skyRed = r;
		skyGreen = g;
		skyBlue = b;
	}
	public float getSkyRed() { return skyRed; }
	public float getSkyGreen() { return skyGreen; }
	public float getSkyBlue() { return skyBlue; }
	public void setFogDensity(float density) { fogDensity = density; }
	public void setFogGradient(float gradient) { fogGradient = gradient; }
	public float getFogDensity() { return fogDensity; }
	public float getFogGradient() { return fogGradient; }
}
