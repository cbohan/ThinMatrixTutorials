package entities;

import window.Window;

public class PlayerCamera extends Camera {
	protected Player player;
	
	protected float distanceFromPlayer = 20f;
	protected float yawAngle = 0;
	protected float yOffset = 4f;
	
	public PlayerCamera(Player player) {
		super();
		
		this.player = player;

		setSkyColor(.8f, .97f, 1f);
		setFogDensity(.0005f);
		setFogGradient(1f);
		
		transform.setRotX(25f);
	}
	
	public void update() {
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		
		calculateCameraPosition();
	}
	
	private void calculateZoom() {
		distanceFromPlayer -= (float) (Window.getMouseDeltaScroll() * 0.5f);
	}
	
	private void calculatePitch() {
		if (Window.getLeftMouseDown() || Window.getRightMouseDown()) 
			transform.turn((float)(Window.getMouseDeltaY() * -.1f), 0, 0);
	}
	
	private void calculateAngleAroundPlayer() {
		if (Window.getLeftMouseDown()) 
			yawAngle -= Window.getMouseDeltaX() * 0.3f;
		
		if (Window.getRightMouseDown()){
			if (yawAngle != 0)
				player.getTransform().setRotY(player.getTransform().getRotY() + yawAngle);
			player.getTransform().setRotY((float) (player.getTransform().getRotY() - (Window.getMouseDeltaX() * 0.3f)));
			yawAngle = 0f;
		}
	}
	
	private void calculateCameraPosition() {
		float horizontalDistance = (float) (distanceFromPlayer * Math.cos(Math.toRadians(transform.getRotX())));
		float verticalDistance = (float) (distanceFromPlayer * Math.sin(Math.toRadians(transform.getRotX())));
		
		transform.setPosY(player.getTransform().getPosY() + verticalDistance + yOffset);
		
		float theta = player.getTransform().getRotY() + yawAngle;
		float xOffset = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float zOffset = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
		
		transform.setPosX(player.getTransform().getPosX() - xOffset);
		transform.setPosZ(player.getTransform().getPosZ() - zOffset);
		transform.setRotY(180 - theta);
	}
}
