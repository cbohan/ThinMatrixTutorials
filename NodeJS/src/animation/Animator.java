package animation;

import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;

import window.Window;

public class Animator {
	private AnimatedModel entity;
	
	private Animation currentAnimation;
	private float animationTime;
	
	public Animator(AnimatedModel entity) {
		this.entity = entity;
	}
	
	public void doAnimation(Animation animation) {
		animationTime = 0;
		currentAnimation = animation;
	}
	
	public void update(float speed) {
		if (currentAnimation == null)
			return;
		
		increaseAnimationTime(speed);
		Map<String, Matrix4f> currentPose = calculateCurrentAnimationPose();
		applyPoseToJoints(currentPose, entity.getRootJoint(), new Matrix4f());
	}
	
	private void increaseAnimationTime(float speed) {
		animationTime += Window.getDeltaTime() * speed;
		animationTime %= currentAnimation.getLength();
	}
	
	private Map<String, Matrix4f> calculateCurrentAnimationPose() {
		KeyFrame[] frames = getPreviousAndNextFrames();
		float progression = calculateProgression(frames[0], frames[1]);
		return interpolatePoses(frames[0], frames[1], progression);
	}
	
	private void applyPoseToJoints(Map<String, Matrix4f> currentPose, Joint joint, Matrix4f parentTransform) {
		Matrix4f currentLocalTransform = currentPose.get(joint.getName());
		Matrix4f currentTransform = new Matrix4f();
		parentTransform.mul(currentLocalTransform, currentTransform);
		
		for(Joint childJoint : joint.getChildren()) 
			applyPoseToJoints(currentPose, childJoint, currentTransform);
		
		Matrix4f currentTransform2 = new Matrix4f();
		currentTransform.mul(joint.getInverseBindTransform(), currentTransform2);
		joint.setAnimatedTransform(currentTransform2);
	}
	
	private KeyFrame[] getPreviousAndNextFrames() {
		KeyFrame[] allFrames = currentAnimation.getKeyFrames();
		KeyFrame previousFrame = allFrames[0];
		KeyFrame nextFrame = allFrames[0];
		for (int i = 1; i < allFrames.length; i++) {
			nextFrame = allFrames[i];
			if (nextFrame.getTimeStamp() > animationTime)
				break;
			previousFrame = allFrames[i];
		}
		return new KeyFrame[] { previousFrame, nextFrame };
	}
	
	private float calculateProgression(KeyFrame previousFrame, KeyFrame nextFrame) {
		float totalTime = nextFrame.getTimeStamp() - previousFrame.getTimeStamp();
		float currentTime = animationTime - previousFrame.getTimeStamp();
		return currentTime / totalTime;
	}
	
	private Map<String, Matrix4f> interpolatePoses(KeyFrame previousFrame, KeyFrame nextFrame, float progression) {
		Map<String, Matrix4f> currentPose = new HashMap<String, Matrix4f>();
		for (String jointName : previousFrame.getPose().keySet()) {
			JointTransform previousTransform = previousFrame.getPose().get(jointName);
			JointTransform nextTransform = nextFrame.getPose().get(jointName);
			JointTransform currentTransform = JointTransform.interpolate(previousTransform, nextTransform, progression);
			currentPose.put(jointName, currentTransform.getLocalTransform());
		}
		return currentPose;
	}
}
