package networking;

import renderEngine.Window;

public class NetTransform {
	private int id;
	private boolean alive;
	private boolean firstPosition;
	private float lastX, lastY, lastZ;
	private float nextX, nextY, nextZ;
	private double lastTime, nextTime;
	private long lastUpdateTime;
	
	public NetTransform(int id) {
		this.id = id;
		alive = true;
		
		firstPosition = true;
		lastX = 0;
		lastY = 0;
		lastZ = 0;
		nextX = 0;
		nextY = 0;
		nextZ = 0;
	}
	
	public void setPosition(float x, float y, float z, int time) {
		if (firstPosition) {
			lastX = x;
			lastY = y;
			lastZ = z;
			nextX = x;
			nextY = y;
			nextZ = z;
			firstPosition = false;
			lastTime = time;
			nextTime = time;
			lastUpdateTime = Window.getCurrentTime();
		} else {
			lastX = nextX;
			lastY = nextY;
			lastZ = nextZ;
			nextX = x;
			nextY = y;
			nextZ = z;
			lastTime = nextTime;
			nextTime = time;
			lastUpdateTime = Window.getCurrentTime();
		}	
	}
	
	public void setAlive(boolean alive) { this.alive = alive; }
	public boolean getAlive() { return this.alive; }
	
	public int getID() { return id; }
	public float getPosX() { return interpolateByTime(lastX, nextX); }
	public float getPosY() { return interpolateByTime(lastY, nextY); }
	public float getPosZ() { return interpolateByTime(lastZ, nextZ); }
	
	private float interpolateByTime(float x, float y) {
		float xContribution = x * (1 - getFractionOfTimeBetweenUpdates());
		float yContribution = y * getFractionOfTimeBetweenUpdates();
		return xContribution + yContribution;
	}
	private float getFractionOfTimeBetweenUpdates() {
		float updateDuration = (float) (nextTime - lastTime);
		if (updateDuration < 1) { updateDuration = 1; }
		float timeSinceLastUpdate = Window.getCurrentTime() - lastUpdateTime;
		return timeSinceLastUpdate / updateDuration;
	}
}
