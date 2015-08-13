package main;

import static org.lwjgl.opengl.GL11.*;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {
	
	private static final float MOUSE_SENSITIVITY = 0.3f;
	
	private static final float[] IDENTITY_MATRIX =
			new float[] {
				1.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 1.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 1.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 1.0f };
	
	private float fov, zNear, zFar, pitch, yaw, speed = 0.2f;
	public Vec3 position;
	private FloatBuffer matrix;
	private long window;
	
	/**
	 * x, y, z, fov, aspectRatio, zNear, zFar
	 */
	public Camera(long window, Vec3 position, float fov, float aspectRatio, float zNear, float zFar) {
		
		this.window = window;
		this.position = position;
		this.fov = fov;
		this.zNear = zNear;
		this.zFar = zFar;
		
		float sine, cotangent, deltaZ;
		float radians = fov / 2 * (float) Math.PI / 180;

		deltaZ = zFar - zNear;
		sine = (float) Math.sin(radians);

		if ((deltaZ == 0) || (sine == 0) || (aspectRatio == 0)) {
			return;
		}

		cotangent = (float) Math.cos(radians) / sine;

		matrix = BufferUtils.createFloatBuffer(16);
		int oldPos = matrix.position();
		matrix.put(IDENTITY_MATRIX);
		matrix.position(oldPos);

		matrix.put(0 * 4 + 0, cotangent / aspectRatio);
		matrix.put(1 * 4 + 1, cotangent);
		matrix.put(2 * 4 + 2, - (zFar + zNear) / deltaZ);
		matrix.put(2 * 4 + 3, -1);
		matrix.put(3 * 4 + 2, -2 * zNear * zFar / deltaZ);
		matrix.put(3 * 4 + 3, 0);
		
	}
	
	public void applyPerspectiveMatrix() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GL11.glMultMatrixf(matrix);
        glMatrixMode(GL_MODELVIEW);
	}
	
	public void applyOrthogonalMatrix() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(-fov, fov, -fov, fov, zNear, zFar);
        glMatrixMode(GL_MODELVIEW);
	}

	public void applyTransitions() {
        glRotatef(pitch, 1, 0, 0);
        glRotatef(yaw, 0, 1, 0);
        glTranslatef(position.x, position.y, position.z);
	}
	
	public void processMouse() {
		
		DoubleBuffer x = BufferUtils.createDoubleBuffer(1),
				y = BufferUtils.createDoubleBuffer(1);

	    glfwGetCursorPos(window, x, y);
	    x.rewind();
	    y.rewind();
	    float mouseX = (float) x.get(), mouseY = (float) y.get();
	    
	    pitch = MOUSE_SENSITIVITY * mouseY;
	    yaw = MOUSE_SENSITIVITY * mouseX;
		
	}
	
	public void processKeyboard() {
		if(isKeyDown(GLFW_KEY_W)) position = position.add(getLookVector());
		if(isKeyDown(GLFW_KEY_S)) position = position.add(getLookVector().negate());
		if(isKeyDown(GLFW_KEY_A)) position = position.add(getLookVector().cross(new Vec3(0, 1, 0)));
		if(isKeyDown(GLFW_KEY_D)) position = position.add(getLookVector().cross(new Vec3(0, 1, 0).negate()));
		if(isKeyDown(GLFW_KEY_LEFT_SHIFT)) position.y += speed;
		if(isKeyDown(GLFW_KEY_SPACE)) position.y -= speed;
	}
	
	private boolean isKeyDown(int key) {
		return glfwGetKey(window, key) == GLFW_PRESS;
	}
	
	private Vec3 getLookVector() {
		Vec3 vec = Vec3.fromAngles((float) Math.toRadians(pitch), (float) Math.toRadians(yaw)).scale(speed);
		return vec;
	}
	
	@Override
	public String toString() {
		return "[" + position.x + ", " + position.y + ", " + position.z + "]";
	}
	
}
