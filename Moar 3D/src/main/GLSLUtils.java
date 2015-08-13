package main;

import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

public final class GLSLUtils {
	
	public static class GLSLProgram {
		
		private Map<String, Integer> uniforms = new HashMap<String, Integer>();
		
		int id;
		
		public GLSLProgram(int...shaders) {
			id = glCreateProgram();
			for(int shader : shaders) glAttachShader(id, shader);
			glLinkProgram(id);
		}
		
		private int getUniformLocation(String name) {
			Integer loc = uniforms.get(name);
			if(loc == null) {
				loc = glGetUniformLocation(id, name);
				uniforms.put(name, loc);
			}
			return loc;
		}
		
		public void use() {
			glUseProgram(id);
		}
		
		public void delete() {
			glDeleteProgram(id);
		}
		
		public void setUniform(String name, float value) {
			glUniform1f(getUniformLocation(name), value);
		}
		
		public void setUniform(String name, float x, float y, float z) {
			glUniform3f(getUniformLocation(name), x, y, z);
		}
		
		public void setUniform(String name, Vec3 vec) {
			setUniform(name, vec.x, vec.y, vec.z);
		}
		
	}
	
	private static int loadShader(int type, String location) throws IOException {
		int id = glCreateShader(type);
		StringBuilder shaderSource = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(location));
        String line;
        while ((line = reader.readLine()) != null) shaderSource.append(line).append('\n');
        reader.close();
        glShaderSource(id, shaderSource);
        glCompileShader(id);
        if(glGetShaderi(id, GL_COMPILE_STATUS) == GL11.GL_FALSE) throw new IOException(location + " " + glGetShaderInfoLog(id, 1024));
        return id;
	}
	
	public static GLSLProgram fromVertexFragmentPair(String vertexLocation, String fragmentLocation) throws IOException {
		return new GLSLProgram(loadShader(GL_VERTEX_SHADER, vertexLocation), loadShader(GL_FRAGMENT_SHADER, fragmentLocation));
	}
	
	public static void useNoShader() {
		glUseProgram(0);
	}
	
}
