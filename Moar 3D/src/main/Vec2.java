package main;

import org.lwjgl.opengl.GL11;

public class Vec2 {
	
	public float x, y;

	public Vec2(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vec2 sub(Vec2 b) {
		return new Vec2(x - b.x, y - b.y);
	}
	
	public void glTexCoord() {
		GL11.glTexCoord2f(x, y);
	}
	
}
