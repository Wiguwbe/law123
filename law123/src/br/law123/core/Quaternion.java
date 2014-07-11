package br.law123.core;


/**
 * Holds a three degree of freedom orientation.
 * 
 * Quaternions have several mathematical properties that make them useful for
 * representing orientations, but require four items of data to hold the three
 * degrees of freedom. These four items of data can be viewed as the
 * coefficients of a complex number with three imaginary parts. The mathematics
 * of the quaternion is then defined and is roughly correspondent to the math of
 * 3D rotations. A quaternion is only a valid rotation if it is normalised: i.e.
 * it has a length of 1.
 * 
 * @note Angular velocity and acceleration can be correctly represented as
 *       vectors. Quaternions are only needed for orientation.
 */
public class Quaternion {

    /**
     * Holds the double component of the quaternion.
     */
    private double r;

    /**
     * Holds the first complex component of the quaternion.
     */
    private double i;

    /**
     * Holds the second complex component of the quaternion.
     */
    private double j;

    /**
     * Holds the third complex component of the quaternion.
     */
    private double k;

    /**
     * Holds the quaternion data in array form.
     */
    private double data[] = new double[4];

    // ... other Quaternion code as before ...

    /**
     * The default ructor creates a quaternion representing a zero rotation.
     */
    public Quaternion() {
        this.r = 1;
        this.i = 0;
        this.j = 0;
        this.k = 0;
    }

    public Quaternion(Quaternion q) {
        this.r = q.r;
        this.i = q.i;
        this.j = q.j;
        this.k = q.k;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    public double getI() {
        return i;
    }

    public void setI(double i) {
        this.i = i;
    }

    public double getJ() {
        return j;
    }

    public void setJ(double j) {
        this.j = j;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    /**
     * The explicit ructor creates a quaternion with the given components.
     * 
     * @param r
     *            The double component of the rigid body's orientation
     *            quaternion.
     * 
     * @param i
     *            The first complex component of the rigid body's orientation
     *            quaternion.
     * 
     * @param j
     *            The second complex component of the rigid body's orientation
     *            quaternion.
     * 
     * @param k
     *            The third complex component of the rigid body's orientation
     *            quaternion.
     * 
     * @note The given orientation does not need to be normalised, and can be
     *       zero. This function will not alter the given values, or normalise
     *       the quaternion. To normalise the quaternion (and make a zero
     *       quaternion a legal rotation), use the normalise function.
     * 
     * @see normalise
     */
    public Quaternion(double r, double i, double j, double k) {
        this.r = r;
        this.i = i;
        this.j = j;
        this.k = k;
    }

    /**
     * Normalises the quaternion to unit length, making it a valid orientation
     * quaternion.
     */
    public void normalise() {
        double d = r * r + i * i + j * j + k * k;

        // Check for zero length quaternion, and use the no-rotation
        // quaternion in that case.
        if (d < 0) { // TODO verificar se não é < Definitions.double_epsilon
            r = 1;
            return;
        }

        d = (1.0) / Math.sqrt(d);
        r *= d;
        i *= d;
        j *= d;
        k *= d;
    }

    /**
     * Multiplies the quaternion by the given quaternion.
     * 
     * @param multiplier
     *            The quaternion by which to multiply.
     */
    public void multToMe(Quaternion multiplier) {
        Quaternion q = new Quaternion(this);
        r = q.r * multiplier.r - q.i * multiplier.i - q.j * multiplier.j - q.k * multiplier.k;
        i = q.r * multiplier.i + q.i * multiplier.r + q.j * multiplier.k - q.k * multiplier.j;
        j = q.r * multiplier.j + q.j * multiplier.r + q.k * multiplier.i - q.i * multiplier.k;
        k = q.r * multiplier.k + q.k * multiplier.r + q.i * multiplier.j - q.j * multiplier.i;
    }

    /**
     * Adds the given vector to this, scaled by the given amount. This is used
     * to update the orientation quaternion by a rotation and time.
     * 
     * @param vector
     *            The vector to add.
     * 
     * @param scale
     *            The amount of the vector to add.
     */
    public void addScaledVector(Vector3 vector, double scale) {
        Quaternion q = new Quaternion(0, vector.getX() * scale, vector.getY() * scale, vector.getZ() * scale);
        q.multToMe(this);
        r += q.r * (0.5);
        i += q.i * (0.5);
        j += q.j * (0.5);
        k += q.k * (0.5);
    }

    public void rotateByVector(Vector3 vector) {
        Quaternion q = new Quaternion(0, vector.getX(), vector.getY(), vector.getZ());
        multToMe(q);
    }

    @Override
    public String toString() {
        return "r:" + r + " i: " + i + " j: " + j + " k:" + k;
    }
}
