package animation;

public class Animation {
	private float length; //in seconds
	private KeyFrame[] keyFrames;
	
	public Animation(float length, KeyFrame[] keyFrames) {
		this.length = length;
		this.keyFrames = keyFrames;
	}
	
	public float getLength() { return length; }
	public KeyFrame[] getKeyFrames() { return keyFrames; }
}
