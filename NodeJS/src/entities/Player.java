package entities;

import org.lwjgl.glfw.GLFW;

import renderEngine.Window;
import terrains.TerrainGroup;

public class Player extends Entity {
	
	private static final float RUN_SPEED = 10f;
	private static final float TURN_SPEED = 150f;
	private static final float GRAVITY = -20f;
	private static final float JUMP_POWER = 13f;
		
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float ySpeed = 0;
	
	private boolean isInAir = false;
	
	public Player() {
		super("res\\capsule.obj", "res\\testTexture.png");
	}
	
	public void move(TerrainGroup terrain) {
		checkInputs();
		
		transform.turn(0, currentTurnSpeed * Window.getDeltaTime(), 0);
		
		float moveDistance = currentSpeed * Window.getDeltaTime();
		float xMoveDistance = (float)(moveDistance * Math.sin(Math.toRadians(transform.getRotY())));
		float zMoveDistance = (float)(moveDistance * Math.cos(Math.toRadians(transform.getRotY())));
		transform.move(xMoveDistance, 0, zMoveDistance);
		
		float terrainHeight = terrain.getHeight(transform.getPosX(), transform.getPosZ());
		
		ySpeed += GRAVITY * Window.getDeltaTime();
		transform.move(0, ySpeed * Window.getDeltaTime(), 0);
		if (transform.getPosY() < terrainHeight) {
			ySpeed = 0;
			isInAir = false;
			transform.setPosY(terrainHeight);
		}
	}
	
	private void checkInputs() {
		//Movement.
		if (Window.getKey(GLFW.GLFW_KEY_W))
			currentSpeed = RUN_SPEED;
		else if (Window.getKey(GLFW.GLFW_KEY_S))
			currentSpeed = -RUN_SPEED;
		else
			currentSpeed = 0;
		
		//Turning.
		if (Window.getKey(GLFW.GLFW_KEY_A))
			currentTurnSpeed = TURN_SPEED;
		else if (Window.getKey(GLFW.GLFW_KEY_D))
			currentTurnSpeed = -TURN_SPEED;
		else
			currentTurnSpeed = 0;
		
		if (Window.getKey(GLFW.GLFW_KEY_SPACE))
			jump();
	}
	
	private void jump() {
		if (isInAir == false) {
			ySpeed = JUMP_POWER;
			isInAir = true;
		}
	}
}
