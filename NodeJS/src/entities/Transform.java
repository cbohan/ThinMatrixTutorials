package entities;

import org.joml.*;
import org.joml.Math;

public class Transform {
	private Matrix4f matrix;
	
	private float pitchCorrection;
	private float scaleCorrection;
	
	private float posX, posY, posZ;
	private float rotX, rotY, rotZ;
	private float scaleX, scaleY, scaleZ;
	
	public Transform() {
		pitchCorrection = 0;
		scaleCorrection = 1;
		
		posX = 0;
		posY = 0;
		posZ = 0;
		
		rotX = 0;
		rotY = 0;
		rotZ = 0;
		
		scaleX = 1;
		scaleY = 1;
		scaleZ = 1;
		
		matrix = new Matrix4f();
		matrix.identity();
	}
	
	public void setPitchCorrection(float correction) { pitchCorrection = correction; }
	public void setScaleCorrection(float correction) { scaleCorrection = correction; }
	
	public void move(float x, float y, float z) {
		posX += x;
		posY += y;
		posZ += z;
	}
	
	public void turn(float pitch, float yaw, float roll) {
		rotX += pitch;
		rotY += yaw;
		rotZ += roll;
	}
	
	public void setPosition(float x, float y, float z) {
		this.posX = x;
		this.posY = y;
		this.posZ = z;
	}
	
	public float getPosX() {
		return posX;
	}

	public void setPosX(float posX) {
		this.posX = posX;
	}

	public float getPosY() {
		return posY;
	}

	public void setPosY(float posY) {
		this.posY = posY;
	}

	public float getPosZ() {
		return posZ;
	}

	public void setPosZ(float posZ) {
		this.posZ = posZ;
	}

	public float getRotX() {
		return rotX;
	}

	public void setRotX(float rotX) {
		this.rotX = rotX;
	}

	public float getRotY() {
		return rotY;
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
	}

	public float getRotZ() {
		return rotZ;
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}
	
	public void setScale(float scale) {
		scaleX = scale;
		scaleY = scale;
		scaleZ = scale;
	}
	
	public float getScaleX() {
		return scaleX;
	}

	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}

	public float getScaleZ() {
		return scaleZ;
	}

	public void setScaleZ(float scaleZ) {
		this.scaleZ = scaleZ;
	}

	public Matrix4f getMatrix() {
		matrix.identity();
		
		matrix.translate(new Vector3f(posX, posY, posZ));
		matrix.rotate((float)Math.toRadians(rotX), new Vector3f(1, 0, 0));
		matrix.rotate((float)Math.toRadians(rotY), new Vector3f(0, 1, 0));
		matrix.rotate((float)Math.toRadians(rotZ), new Vector3f(0, 0, 1));
		matrix.rotate((float)Math.toRadians(pitchCorrection), new Vector3f(1, 0, 0));
		matrix.scale(new Vector3f(scaleX * scaleCorrection, scaleY * scaleCorrection, scaleZ * scaleCorrection));
				
		return matrix;
	}
}
