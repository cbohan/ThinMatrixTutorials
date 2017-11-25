package networking;

public class NetTransform {
	private int id;
	private boolean alive;
	private float x, y, z;
	
	public NetTransform(int id) {
		this.id = id;
		this.alive = true;
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public void setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y; 
		this.z = z;
	}
	
	public void setAlive(boolean alive) { this.alive = alive; }
	public boolean getAlive() { return this.alive; }
	
	public int getID() { return id; }
	public float getPosX() { return x; }
	public float getPosY() { return y; }
	public float getPosZ() { return z; }
}
