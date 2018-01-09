package animation;

import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;

public class Joint {
	private List<Joint> children = new ArrayList<Joint>();
	
	private int id;
	private String name;
	
	private Matrix4f animatedTransform = new Matrix4f();
	private Matrix4f localBindTransform; //original transform of bone relative to parent
	private Matrix4f inverseBindTransform = new Matrix4f();
	
	public Joint(int id, String name, Matrix4f localBindTransform) {
		this.id = id;
		this.name = name;
		this.localBindTransform = localBindTransform;
	}
	
	public int getId() { return id; }
	public String getName() { return name; }
	
	public List<Joint> getChildren() { return children; }
	public void addChild(Joint child) { children.add(child); }
	public int getTreeSize() {
		int treeSize = 1;
		for (Joint child : children)
			treeSize += child.getTreeSize();
		return treeSize;
	}
	
	public Matrix4f getAnimatedTransform() { return animatedTransform; }
	public void setAnimatedTransform(Matrix4f animatedTransform) { this.animatedTransform = animatedTransform; }
	
	public Matrix4f getInverseBindTransform() { return inverseBindTransform; }
	public void calculateInverseBindTransform(Matrix4f parentBindTransform) {
		Matrix4f bindTransform = new Matrix4f();
		parentBindTransform.mul(localBindTransform, bindTransform);
		bindTransform.invert(inverseBindTransform);
		for (Joint child : children) 
			child.calculateInverseBindTransform(bindTransform);
	}
}
