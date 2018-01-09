package entities;

import org.lwjgl.glfw.GLFW;

import animation.AnimatedEntity;
import animation.AnimatedModel;
import animation.Animation;
import animation.ColladaAnimationLoader;
import animation.ColladaModelLoader;
import animation.Joint;
import models.RawModel;
import terrains.TerrainGroup;
import textures.TextureLoader;
import window.Window;

public class Player {
	
	private static final float RUN_SPEED = 10f;
	private static final float TURN_SPEED = 150f;
	private static final float GRAVITY = -20f;
	private static final float JUMP_POWER = 13f;
		
	private AnimatedEntity entity;
	private Animation run;
	private Animation walk;
	private Animation idle;
	private Animation currentAnimation;
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float ySpeed = 0;
	
	private boolean isInAir = false;
	
	public Player() {
		System.out.println("Loading player model");
		RawModel rawCharacter = ColladaModelLoader.load("res\\characters\\character_male_run.dae");
		System.out.println("Loading player skeleton");
		Joint characterRoot = ColladaAnimationLoader.loadSkeleton("res\\characters\\character_male_run.dae");
		System.out.println("Loading player run cycle");		
		run = ColladaAnimationLoader.loadAnimation("res\\characters\\character_male_run.dae");
		System.out.println("Loading player walk cycle");
		//walk = ColladaAnimationLoader.loadAnimation("res\\characters\\character_male_walk.dae");
		System.out.println("Loading player idle");
		idle = ColladaAnimationLoader.loadAnimation("res\\characters\\character_male_idle.dae");
		entity = new AnimatedEntity(new AnimatedModel(rawCharacter, 
				TextureLoader.loadTexture("res\\characters\\character_male_albedo.png"), characterRoot, characterRoot.getTreeSize()));
		entity.getAnimatedModel().doAnimation(run);		
		entity.getTransform().setPosition(0f, 20f, -50f);
		entity.getTransform().setPitchCorrection(-90f);
		entity.getTransform().setScaleCorrection(2f);
		
		setAnimation(idle);
	}
	
	public Transform getTransform() { return entity.getTransform(); }
	
	public void move(TerrainGroup terrain) {
		checkInputs();
		
		entity.getTransform().turn(0, currentTurnSpeed * Window.getDeltaTime(), 0);
		
		float moveDistance = currentSpeed * Window.getDeltaTime();
		float xMoveDistance = (float)(moveDistance * Math.sin(Math.toRadians(entity.getTransform().getRotY())));
		float zMoveDistance = (float)(moveDistance * Math.cos(Math.toRadians(entity.getTransform().getRotY())));
		entity.getTransform().move(xMoveDistance, 0, zMoveDistance);
		
		float terrainHeight = terrain.getHeight(entity.getTransform().getPosX(), entity.getTransform().getPosZ());
		
		ySpeed += GRAVITY * Window.getDeltaTime();
		entity.getTransform().move(0, ySpeed * Window.getDeltaTime(), 0);
		if (entity.getTransform().getPosY() < terrainHeight) {
			ySpeed = 0;
			isInAir = false;
			entity.getTransform().setPosY(terrainHeight);
		}
		
		if (currentSpeed > 0 && currentAnimation != run) 
			setAnimation(run);
		else if (currentSpeed == 0 && currentAnimation != idle) 
			setAnimation(idle);
		
		entity.getAnimatedModel().update();
	}
	
	public AnimatedEntity getAnimatedEntity() { return entity; }
	
	private void setAnimation(Animation animation) {
		currentAnimation = animation;
		entity.getAnimatedModel().doAnimation(animation);
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
