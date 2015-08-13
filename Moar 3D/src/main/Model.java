package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Model {
	
	public static class Face {
		
		private List<Integer> vertexIndicies, texCoordIndicies;
		private Vec3 normal;
		
		public Face(List<Integer> vertexIndicies, List<Integer> texCoordIndicies, Vec3 normal) {
			this.vertexIndicies = vertexIndicies;
			this.texCoordIndicies = texCoordIndicies;
			this.normal = normal;
		}

		public Face(List<Integer> vertexIndicies, List<Integer> texCoordIndicies) {
			this.vertexIndicies = vertexIndicies;
			this.texCoordIndicies = texCoordIndicies;
		}
		
	}
	
	private List<Vec2> texCoords;
	private List<Vec3> vertices;
	private List<Face> faces;
	
	public void draw() {
		for(Face face : faces) {
			glBegin(GL_POLYGON);
			face.normal.glNormal();
			for(int i = 0; i < face.vertexIndicies.size(); i++) {
				if(i < face.texCoordIndicies.size())
					texCoords.get(face.texCoordIndicies.get(i)).glTexCoord();
				vertices.get(face.vertexIndicies.get(i)).glVertex();	
			}
			glEnd();
		}
	}
	
	public int createList() {
		int list = glGenLists(1);
		glNewList(list, GL_COMPILE);
		draw();
		glEndList();
		return list;
	}
	
	//Only avaliable in list form
	public int createPointList() {
		int list = glGenLists(1);
		glNewList(list, GL_COMPILE);
		glBegin(GL_POINTS);
		for(Vec3 vertex : vertices) vertex.glVertex();
		glEnd();
		glEndList();
		return list;
	}
	
	public void calculateNormals() {
		for(Face face : faces) {
			Vec3 a = vertices.get(face.vertexIndicies.get(0)),
					ab = vertices.get(face.vertexIndicies.get(1)).sub(a),
					ac = vertices.get(face.vertexIndicies.get(2)).sub(a);
			face.normal = ab.cross(ac).normalize();
		}
	}
	
	public void negateNormals() {
		for(Face face : faces) face.normal = face.normal.negate();
	}
	
	public Model(List<Vec2> texCoords, List<Vec3> vertices,
			List<Face> faces) {
		this.texCoords = texCoords;
		this.vertices = vertices;
		this.faces = faces;
	}

	public static Model fromOBJ(String file) throws IOException {
		List<Vec3> vertices = new ArrayList<Vec3>();
		List<Vec3> normals = new ArrayList<Vec3>();
		List<Vec2> texCoords = new ArrayList<Vec2>();
		List<Face> faces = new ArrayList<Face>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		while((line = reader.readLine()) != null) {
			String[] split = line.split(" ");
			if(split.length == 0) continue;
			switch(split[0]) {
				case "v" : vertices.add(new Vec3(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3])));
				break;
				case "vn" : normals.add(new Vec3(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3])));
				break;
				case "vt" : texCoords.add(new Vec2(Float.parseFloat(split[1]), 1 - Float.parseFloat(split[2])));
				break;
				case "f" : {
					int normalIndex = 0;
					List<Integer> vertexIndicies = new ArrayList<Integer>(), textureIndicies = new ArrayList<Integer>();
					for(int i = 1; i < split.length; i++) {
						String s = split[i];
						boolean hasTexCoord = !s.contains("//");
						String[] indicies = s.split(hasTexCoord ? "/" : "//");
						vertexIndicies.add(Integer.parseInt(indicies[0]) - 1);
						normalIndex = Integer.parseInt(indicies[hasTexCoord ?  2 : 1]) - 1;
						if(hasTexCoord) textureIndicies.add(Integer.parseInt(indicies[1]) - 1);
					}
					faces.add(new Face(vertexIndicies, textureIndicies, normals.get(normalIndex)));
				}
				break;
			}
		}
		reader.close();
		return new Model(texCoords, vertices, faces);
	}
	
}
