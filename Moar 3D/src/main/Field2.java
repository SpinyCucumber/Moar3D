package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.Model.Face;

public class Field2 {
	
	private static List<Vec2> texCoords = Arrays.asList(new Vec2(0, 0), new Vec2(1, 0), new Vec2(1, 1), new Vec2(0, 1));
	private static List<Integer> texIndices1 = Arrays.asList(new Integer[]{0, 1, 3}),
								texIndices2 = Arrays.asList(new Integer[]{3, 1, 2});
	
	public interface GenFunction {
		float gen(int x, int y);
	}
	
	public interface Filter {
		float apply(Field2 field, int x, int y);
	}
	
	private float[][] values;
	private int width, height;
	
	public Field2(int width, int height) {
		values = new float[width][height];
		this.width = width;
		this.height = height;
	}
	
	public Field2(int width, int height, GenFunction generator) {
		this(width, height);
		for(int x = 0; x < width; x++) { for(int y = 0; y < height; y++) {
			values[x][y] = generator.gen(x, y); } }
	}
	
	public float get(int x, int y) {
		return values[x % width][y % height];
	}
	
	public float sum() {
		float sum = 0;
		for(int x = 0; x < width; x++) { for(int y = 0; y < height; y++) {
			sum += values[x][y]; } }
		return sum;
	}
	
	public void scale(float s) {
		for(int x = 0; x < width; x++) { for(int y = 0; y < height; y++) {
			values[x][y] *= s; } }
	}
	
	public void add(float s) {
		for(int x = 0; x < width; x++) { for(int y = 0; y < height; y++) {
			values[x][y] += s; } }
	}
	
	public void normalize() {
		scale(1f / sum());
	}
	
	public void applyFilter(Filter filter) {
		for(int x = 0; x < width; x++) { for(int y = 0; y < height; y++) {
			values[x][y] = filter.apply(this, x, y); } }
	}
	
	public Model flatModel() {
		List<Vec3> vertices = new ArrayList<Vec3>();
		for(int x = 0; x < width; x++) { for(int y = 0; y < height; y++) {
			vertices.add(new Vec3(x, values[x][y], y)); } }
		return wrap(vertices);
	}
	
	public Model sphereModel() {
		Matrix3 pitchMat = Matrix3.fromAA(Vec3.X_AXIS, Matrix3.FULL_ROT / width),
				yawMat = Matrix3.fromAA(Vec3.Y_AXIS, Matrix3.FULL_ROT / height);
		Vec3 axis = new Vec3(0, 0, 1);
		System.out.println(pitchMat);
		List<Vec3> vertices = new ArrayList<Vec3>();
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				vertices.add(axis.scale(values[x][y]));
				axis = axis.mul(yawMat);
			}
			System.out.println(axis);
			axis = axis.mul(pitchMat);
		}
		return wrap(vertices);
	}
	
	private Model wrap(List<Vec3> vertices) {
		List<Face> faces = new ArrayList<Face>();
		for(int x = 0; x < width - 1; x++) { for(int y = 0; y < height - 1; y++) {
			int ll = x * width + y, ul = ll + width, lr = ll + 1, ur = ul + 1;
			faces.add(new Face(Arrays.asList(new Integer[]{ul, ur, ll}), texIndices1));
			faces.add(new Face(Arrays.asList(new Integer[]{ur, ll, lr}), texIndices2));
		} }
		Model model = new Model(texCoords, vertices, faces);
		model.calculateNormals();
		return model;
	}
	
	public static final GenFunction RAND = new GenFunction() {
		public float gen(int x, int y) { return (float) Math.random(); } };
	
}
