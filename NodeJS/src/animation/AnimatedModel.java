package animation;

import org.joml.Matrix4f;

import models.RawModel;
import textures.Texture;

public class AnimatedModel {
	private RawModel model;
	private Texture texture;
	
	private Joint rootJoint;
	private int jointCount;
	
	private Animator animator;
	
	private float shineDamping = 1;
	private float reflectivity = 0;
	
	public AnimatedModel(RawModel model, Texture texture, Joint rootJoint, int jointCount) {
		this.model = model;
		this.texture = texture;
		this.rootJoint = rootJoint;
		this.jointCount = jointCount;
		animator = new Animator(this);
		Matrix4f mat = new Matrix4f();
		rootJoint.calculateInverseBindTransform(mat.identity());
	}
	
	public RawModel getRawModel() { return model; }
	public Texture getTexture() { return texture; }
	public Joint getRootJoint() { return rootJoint; }
	public int getJointCount() { return jointCount; }
	public Animator getAnimator() { return animator; }
	
	public void doAnimation(Animation animation) { animator.doAnimation(animation); }
	
	public void update() { animator.update(1f); }
	public void update(float speed) { animator.update(speed); }
	
	public Matrix4f[] getJointTransforms() {
		Matrix4f[] jointMatrices = new Matrix4f[jointCount];
		addJointsToArray(rootJoint, jointMatrices);
		
		return jointMatrices;
	}
	
	private void addJointsToArray(Joint headJoint, Matrix4f[] jointMatrices) {
		jointMatrices[headJoint.getId()] = headJoint.getAnimatedTransform();
		for (Joint child : headJoint.getChildren())
			addJointsToArray(child, jointMatrices);
	}
	
	public float getShineDamping() { return shineDamping; }
	public void setShineDamping(float damping) { shineDamping = damping; }
	public float getReflectivity() { return reflectivity; }
	public void setReflectivity(float reflectivity) { this.reflectivity = reflectivity; }	
}
