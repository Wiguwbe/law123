package br.law123.core;

/**
 * Holds a vector in 3 dimensions. Four data members are allocated to ensure
 * alignment in an array.
 * 
 * @note This class contains a lot of inline methods for basic mathematics. The
 *       implementations are included in the header file.
 */
public class Vector3 {
	/** Holds the value along the x axis. */
	private double x;

	/** Holds the value along the y axis. */
	private double y;

	/** Holds the value along the z axis. */
	private double z;

	/** Padding to ensure 4 word alignment. */
	private double pad;

	/** The default ructor creates a zero vector. */
	public Vector3() {
		this(0, 0, 0);
	}

	/**
	 * Constructor create a vector by another vector.
	 */
	public Vector3(Vector3 v) {
		this(v.x, v.y, v.z);
	}

	/**
	 * The explicit ructor creates a vector with the given components.
	 */
	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	static Vector3 GRAVITY;
	static Vector3 HIGH_GRAVITY;
	static Vector3 UP;
	static Vector3 RIGHT;
	static Vector3 OUT_OF_SCREEN;
	static Vector3 X;
	static Vector3 Y;
	static Vector3 Z;

	// ... Other Vector3 code as before ...

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public double get(int i) {
		if (i == 0)
			return x;
		if (i == 1)
			return y;
		return z;
	}

	public void set(int i, double value) {
		if (i == 0)
			x = value;
		else if (i == 1)
			y = value;
		else
			z = value;
	}

	/** Adds the given vector to this. */
	public void sumToMe(Vector3 v) {
		x += v.x;
		y += v.y;
		z += v.z;
	}

	/**
	 * Returns the value of the given vector added to this.
	 */
	public Vector3 sum(Vector3 v) {
		return new Vector3(x + v.x, y + v.y, z + v.z);
	}

	/** Subtracts the given vector from this. */
	public void subToMe(Vector3 v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
	}

	/**
	 * Returns the value of the given vector subtracted from this.
	 */
	public Vector3 sub(Vector3 v) {
		return new Vector3(x - v.x, y - v.y, z - v.z);
	}

	/** Multiplies this vector by the given scalar. */
	public void multToMe(double value) {
		x *= value;
		y *= value;
		z *= value;
	}

	/** Returns a copy of this vector scaled the given value. */
	public Vector3 mult(double value) {
		return new Vector3(x * value, y * value, z * value);
	}

	/**
	 * Calculates and returns a component-wise product of this vector with the
	 * given vector.
	 */
	public Vector3 componentProduct(Vector3 vector) {
		return new Vector3(x * vector.x, y * vector.y, z * vector.z);
	}

	/**
	 * Performs a component-wise product with the given vector and sets this
	 * vector to its result.
	 */
	public void componentProductUpdate(Vector3 vector) {
		x *= vector.x;
		y *= vector.y;
		z *= vector.z;
	}

	/**
	 * Calculates and returns the vector product of this vector with the given
	 * vector.
	 */
	public Vector3 vectorProduct(Vector3 vector) {
		return new Vector3(y * vector.z - z * vector.y, z * vector.x - x
				* vector.z, x * vector.y - y * vector.x);
	}

	/**
	 * Updates this vector to be the vector product of its current value and the
	 * given vector.
	 */
	public void restToMe(Vector3 vector) {
		Vector3 v = vectorProduct(vector);
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
		this.pad = v.pad;
	}

	/**
	 * Calculates and returns the vector product of this vector with the given
	 * vector.
	 */
	public Vector3 rest(Vector3 vector) {
		return new Vector3(y * vector.z - z * vector.y, z * vector.x - x
				* vector.z, x * vector.y - y * vector.x);
	}

	/**
	 * Calculates and returns the scalar product of this vector with the given
	 * vector.
	 */
	public double scalarProduct(Vector3 vector) {
		return x * vector.x + y * vector.y + z * vector.z;
	}

	/**
	 * Calculates and returns the scalar product of this vector with the given
	 * vector.
	 */
	public double mult(Vector3 vector) {
		return x * vector.x + y * vector.y + z * vector.z;
	}

	/**
	 * Adds the given vector to this, scaled by the given amount.
	 */
	public void addScaledVector(Vector3 vector, double scale) {
		x += vector.x * scale;
		y += vector.y * scale;
		z += vector.z * scale;
	}

	/** Gets the magnitude of this vector. */
	public double magnitude() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	/** Gets the squared magnitude of this vector. */
	public double squareMagnitude() {
		return x * x + y * y + z * z;
	}

	/** Limits the size of the vector to the given maximum. */
	public void trim(double size) {
		if (squareMagnitude() > size * size) {
			normalise();
			x *= size;
			y *= size;
			z *= size;
		}
	}

	/** Turns a non-zero vector into a vector of unit length. */
	public void normalise() {
		double l = magnitude();
		if (l > 0) {
			multToMe(1 / l); // TODO verificar
		}
	}

	/** Returns the normalised version of a vector. */
	public Vector3 unit() {
		Vector3 result = new Vector3(this);
		result.normalise();
		return result;
	}

	/** Checks if the two vectors have identical components. */
	public boolean isEquals(Vector3 other) {
		return x == other.x && y == other.y && z == other.z;
	}

	/** Checks if the two vectors have non-identical components. */
	public boolean isNotEquals(Vector3 other) {
		return !isEquals(other);
	}

	/**
	 * Checks if this vector is component-by-component less than the other.
	 * 
	 * @note This does not behave like a single-value comparison: !(a < b) does
	 *       not imply (b >= a).
	 */
	public boolean lessThan(Vector3 other) {
		return x < other.x && y < other.y && z < other.z;
	}

	/**
	 * Checks if this vector is component-by-component less than the other.
	 * 
	 * @note This does not behave like a single-value comparison: !(a < b) does
	 *       not imply (b >= a).
	 */
	public boolean greaterThan(Vector3 other) {
		return x > other.x && y > other.y && z > other.z;
	}

	/**
	 * Checks if this vector is component-by-component less than the other.
	 * 
	 * @note This does not behave like a single-value comparison: !(a <= b) does
	 *       not imply (b > a).
	 */
	public boolean lessEqualsThan(Vector3 other) {
		return x <= other.x && y <= other.y && z <= other.z;
	}

	/**
	 * Checks if this vector is component-by-component less than the other.
	 * 
	 * @note This does not behave like a single-value comparison: !(a <= b) does
	 *       not imply (b > a).
	 */
	public boolean greaterEqualsThan(Vector3 other) {
		return x >= other.x && y >= other.y && z >= other.z;
	}

	/** Zero all the components of the vector. */
	public void clear() {
		x = y = z = 0;
	}

	/** Flips all the components of the vector. */
	void invert() {
		x = -x;
		y = -y;
		z = -z;
	}

}