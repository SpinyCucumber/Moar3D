package main;


public final class Matrix3 {
	
	public static final Matrix3 IDENTITY = fromArray(1, 0, 0,
													0, 1, 0,
													0, 0, 1);
	public static final float FULL_ROT = (float) Math.PI * 2;
	
	//Columns, stored in vector format for convenience
	public Vec3 x, y, z;

	public Matrix3(Vec3 x, Vec3 y, Vec3 z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public static Matrix3 fromDArray(float[][] ary) {
		if(ary.length != 3) throw new RuntimeException("Array size must be 3!");
		return new Matrix3(Vec3.fromArray(ary[0]),
						Vec3.fromArray(ary[1]),
						Vec3.fromArray(ary[2]));
	}
	
	public static Matrix3 fromArray(float...ary) {
		if(ary.length != 9) throw new RuntimeException("Array size must be 9!");
		float[][] dAry = new float[3][3];
		for(int i = 0; i < 9; i++) {
			int r = (int) Math.floor((float) i / 3f);
			dAry[r][i - r * 3] = ary[i];
		}
		return fromDArray(dAry);
	}
	
	public Matrix3 scale(float s) {
		return new Matrix3(x.scale(s), y.scale(s), z.scale(s));
	}
	
	public Matrix3 add(Matrix3 b) {
		return new Matrix3(x.add(b.x), y.add(b.y), z.add(b.z));
	}
	
	public Matrix3 mul(Matrix3 b) {
		float[][] newAry = new float[3][3], aAry = toDArray(), bAry = b.toDArray();
		for(int r = 0; r < 3; r++) { for(int c = 0; c < 3; c++) {
			for(int k = 0; k < 3; k++) { newAry[r][c] += (aAry[r][k] * bAry[k][c]); }
		} }
		return fromDArray(newAry);
	}
	
	public Matrix3 sqrd() {
		return mul(this);
	}
	
	public float[][] toDArray() {
		float[][] columns = new float[3][3];
		columns[0] = x.toArray();
		columns[1] = y.toArray();
		columns[2] = z.toArray();
		return columns;
	}
	
	public float[] toArray() {
		float[][] dAry = toDArray();
		float[] ary = new float[9];
		for(int r = 0; r < 3; r++) { for(int c = 0; c < 3; c++) {
			ary[r * 3 + c] = dAry[r][c];
		} }
		return ary;
	}
	
	@Override
	public String toString() {
		String line = System.lineSeparator();
		return line + "[ " + x + line + y + line + z + " ]" + line;
	}
	
	public static Matrix3 fromAA(Vec3 axis, float angle) {
		float s = (float) Math.sin(angle), t = (1 - (float) Math.cos(angle));
		Matrix3 ss = axis.ssMatrix();
		return IDENTITY.add(ss.scale(s)).add(ss.sqrd().scale(t));
	}
	
	public static void main(String[] args) {
		Matrix3 rot = fromAA(new Vec3(1,1,0).normalize(), (float) Math.toRadians(180));
		Vec3 point = new Vec3(3,0,0);
		System.out.println(point.mul(rot));
	}
	
}
