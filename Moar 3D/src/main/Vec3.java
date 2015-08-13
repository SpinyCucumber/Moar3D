package main;

import org.lwjgl.opengl.GL11;

public final class Vec3 {
	
	public static final Vec3 X_AXIS = new Vec3(1,0,0),
							Y_AXIS = new Vec3(0,1,0),
							Z_AXIS = new Vec3(0,0,1);
	
	public float x, y, z;
	
	public Vec3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vec3(Vec3 b) {
		this(b.x, b.y, b.z);
	}
	
	public static Vec3 fromAngles(float pitch, float yaw) {
		float x = (float) Math.sin(yaw), z = (float) Math.cos(yaw),
				y = (float) Math.sin(pitch), d = (float) Math.cos(pitch);
		return new Vec3(-d * x, y, d * z);
	}
	
	public static Vec3 fromArray(float[] array) {
		if(array.length != 3) throw new RuntimeException("Array size must be three!");
		return new Vec3(array[0], array[1], array[2]);
	}
	
	public Vec3 add(Vec3 b) {
		return new Vec3(x + b.x, y + b.y, z + b.z);
	}
	
	public Vec3 sub(Vec3 b) {
		return new Vec3(x - b.x, y - b.y, z - b.z);
	}
	
	public Vec3 scale(float s) {
		return new Vec3(x * s, y * s, z * s);
	}
	
	public Vec3 invScale(float s) {
		return new Vec3(x / s, y / s, z / s);
	}
	
	public Vec3 negate() {
		return new Vec3(-x, -y, -z);
	}
	
	public float dot(Vec3 b) {
		return x * b.x + y * b.y + z * b.z;
	}
	
	public Vec3 cross(Vec3 b) {
		return b.mul(ssMatrix());
	}
	
	public float length() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}
	
	public Vec3 normalize() {
		return invScale(length());
	}
	
	public Matrix3 ssMatrix() {
		return new Matrix3(new Vec3(0, z, -y),
				new Vec3(-z, 0, x),
				new Vec3(y, -x, 0));
	}
	
	public Vec3 mul(Matrix3 mat) {
		return new Vec3(dot(mat.x), dot(mat.y), dot(mat.z));
	}
	
	public void glVertex() {
		GL11.glVertex3f(x, y, z);
	}
	
	public void glNormal() {
		GL11.glNormal3f(x, y, z);
	}
	
	public String toString() {
		return "{" + x + ", " + y + ", " + z + "}";
	}
	
	public float[] toArray() {
		return new float[]{x, y, z};
	}
	
	public static void main(String[] args) {
		System.out.println(Vec3.fromAngles((float) Math.toRadians(0), (float) Math.toRadians(0)));
	}
	
}
