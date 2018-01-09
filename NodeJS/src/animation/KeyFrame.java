package animation;

import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;

public class KeyFrame {
	private float timeStamp;
	private Map<String, JointTransform> pose;
	
	public KeyFrame(float timeStamp) {
		this.timeStamp = timeStamp;
		this.pose = new HashMap<String, JointTransform>();
	}
	
	public KeyFrame(float timeStamp, Map<String, JointTransform> pose) {
		this.timeStamp = timeStamp;
		this.pose = pose;
	}
	
	public float getTimeStamp() { return timeStamp; }
	public Map<String, JointTransform> getPose() { return pose; }
	public void addToPose(String jointName, Matrix4f matrix) {
		JointTransform jointTransform = new JointTransform(matrix);
		pose.put(jointName, jointTransform);
	}
}
