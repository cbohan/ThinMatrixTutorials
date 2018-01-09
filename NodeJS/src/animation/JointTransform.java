package animation;

import org.joml.*;
import org.joml.Math;

public class JointTransform {
	Vector3f position;
	Quaternionf rotation;
	
	public JointTransform(Matrix4f matrix) {
		position = new Vector3f(matrix.m30(), matrix.m31(), matrix.m32());
		rotation = quaternionFromMatrix(matrix);
	}
	
	public JointTransform(Vector3f position, Quaternionf rotation) {
		this.position = position;
		this.rotation = rotation;
	}
	
	public static Quaternionf quaternionFromMatrix(Matrix4f matrix) {
		float w, x, y, z;
		float diagonal = matrix.m00() + matrix.m11() + matrix.m22();
		if (diagonal > 0) {
			float w4 = (float) (Math.sqrt(diagonal + 1f) * 2f);
			w = w4 / 4f;
			x = (matrix.m12() - matrix.m21()) / w4;
			y = (matrix.m20() - matrix.m02()) / w4;
			z = (matrix.m01() - matrix.m10()) / w4;
		} else if ((matrix.m00() > matrix.m11()) && (matrix.m00() > matrix.m22())) {
			float x4 = (float) (Math.sqrt(1f + matrix.m00() - matrix.m11() - matrix.m22()) * 2f);
			w = (matrix.m12() - matrix.m21()) / x4;
			x = x4 / 4f;
			y = (matrix.m10() + matrix.m01()) / x4;
			z = (matrix.m20() + matrix.m02()) / x4;
		} else if (matrix.m11() > matrix.m22()) {
			float y4 = (float) (Math.sqrt(1f + matrix.m11() - matrix.m00() - matrix.m22()) * 2f);
			w = (matrix.m20() - matrix.m02()) / y4;
			x = (matrix.m10() + matrix.m01()) / y4;
			y = y4 / 4f;
			z = (matrix.m21() + matrix.m12()) / y4;
		} else {
			float z4 = (float) (Math.sqrt(1f + matrix.m22() - matrix.m00() - matrix.m11()) * 2f);
			w = (matrix.m01() - matrix.m10()) / z4;
			x = (matrix.m20() + matrix.m02()) / z4;
			y = (matrix.m21() + matrix.m12()) / z4;
			z = z4 / 4f;
		}
		Quaternionf rot = new Quaternionf(x, y, z, w);
		return rot.normalize();
	}
	
	protected Matrix4f getLocalTransform() {
		Matrix4f matrix = new Matrix4f();
		matrix.translate(position, matrix);
		matrix.rotate(rotation, matrix);
		return matrix;
	}
	
	protected static JointTransform interpolate(JointTransform frame1, JointTransform frame2, float progression) {
		Vector3f interpolatedPosition = new Vector3f();
		frame1.position.lerp(frame2.position, progression, interpolatedPosition);
		
		Quaternionf interpolatedRotation = new Quaternionf();
		frame1.rotation.slerp(frame2.rotation, progression, interpolatedRotation);
		
		return new JointTransform(interpolatedPosition, interpolatedRotation);
	}
}
