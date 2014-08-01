package br.law123.core;

/**
 * Holds a transform matrix, consisting of a rotation matrix and
 * a position. The matrix has 12 elements, it is assumed that the
 * remaining four are (0,0,0,1); producing a homogenous matrix.
 */
public class Matrix4 {

    /**
     * Holds the transform matrix data in array form.
     */
    private double data[] = new double[12];

    // ... Other Matrix4 code as before ...

    /**
     * Creates an identity matrix.
     */
    public Matrix4() {
        data[1] = data[2] = data[3] = data[4] = data[6] = data[7] = data[8] = data[9] = data[11] = 0;
        data[0] = data[5] = data[10] = 1;
    }
    
    public double[] getData() {
    	return data;
    }
    
    public void setData(double[] data) {
    	this.data = data;
    }
    
    public double getData(int i) {
        return data[i];
    }
    
    public void setData(int i, double data) {
        this.data[i] = data;
    }

    /**
     * Sets the matrix to be a diagonal matrix with the given coefficients.
     */
    void setDiagonal(double a, double b, double c) {
        data[0] = a;
        data[5] = b;
        data[10] = c;
    }

    /**
     * Returns a matrix which is this matrix multiplied by the given
     * other matrix.
     */
    public Matrix4 mult(Matrix4 o) {
        Matrix4 result = new Matrix4();
        result.data[0] = (o.data[0] * data[0]) + (o.data[4] * data[1]) + (o.data[8] * data[2]);
        result.data[4] = (o.data[0] * data[4]) + (o.data[4] * data[5]) + (o.data[8] * data[6]);
        result.data[8] = (o.data[0] * data[8]) + (o.data[4] * data[9]) + (o.data[8] * data[10]);

        result.data[1] = (o.data[1] * data[0]) + (o.data[5] * data[1]) + (o.data[9] * data[2]);
        result.data[5] = (o.data[1] * data[4]) + (o.data[5] * data[5]) + (o.data[9] * data[6]);
        result.data[9] = (o.data[1] * data[8]) + (o.data[5] * data[9]) + (o.data[9] * data[10]);

        result.data[2] = (o.data[2] * data[0]) + (o.data[6] * data[1]) + (o.data[10] * data[2]);
        result.data[6] = (o.data[2] * data[4]) + (o.data[6] * data[5]) + (o.data[10] * data[6]);
        result.data[10] = (o.data[2] * data[8]) + (o.data[6] * data[9]) + (o.data[10] * data[10]);

        result.data[3] = (o.data[3] * data[0]) + (o.data[7] * data[1]) + (o.data[11] * data[2]) + data[3];
        result.data[7] = (o.data[3] * data[4]) + (o.data[7] * data[5]) + (o.data[11] * data[6]) + data[7];
        result.data[11] = (o.data[3] * data[8]) + (o.data[7] * data[9]) + (o.data[11] * data[10]) + data[11];

        return result;
    }

    /**
     * Transform the given vector by this matrix.
     * 
     * @param vector The vector to transform.
     */
    public Vector3 mult(Vector3 vector) {
        return new Vector3(
                           vector.getX() * data[0] + 
                           vector.getY() * data[1] + 
                           vector.getZ() * data[2] + data[3],

                           vector.getX() * data[4] + 
                           vector.getY() * data[5] + 
                           vector.getZ() * data[6] + data[7],

                           vector.getX() * data[8] + 
                           vector.getY() * data[9] + 
                           vector.getZ() * data[10] + data[11]);
    }

    /**
     * Transform the given vector by this matrix.
     * 
     * @param vector The vector to transform.
     */
    public Vector3 transform(Vector3 vector) {
        return mult(vector);
    }

    /**
     * Returns the determinant of the matrix.
     */
    public double getDeterminant() {
        return -data[8] * data[5] * data[2] + data[4] * data[9] * data[2] + data[8] * data[1] * data[6] - data[0] * data[9] * data[6] - data[4] * data[1] * data[10] + data[0] * data[5] * data[10];
    }

    /**
     * Sets the matrix to be the inverse of the given matrix.
     * 
     * @param m The matrix to invert and use to set this.
     */
    public void setInverse(Matrix4 m) {
        // Make sure the determinant is non-zero.
        double det = getDeterminant();
        if (det == 0) return;
        det = (1.0) / det;

        data[0] = (-m.data[9] * m.data[6] + m.data[5] * m.data[10]) * det;
        data[4] = (m.data[8] * m.data[6] - m.data[4] * m.data[10]) * det;
        data[8] = (-m.data[8] * m.data[5] + m.data[4] * m.data[9]) * det;

        data[1] = (m.data[9] * m.data[2] - m.data[1] * m.data[10]) * det;
        data[5] = (-m.data[8] * m.data[2] + m.data[0] * m.data[10]) * det;
        data[9] = (m.data[8] * m.data[1] - m.data[0] * m.data[9]) * det;

        data[2] = (-m.data[5] * m.data[2] + m.data[1] * m.data[6]) * det;
        data[6] = (+m.data[4] * m.data[2] - m.data[0] * m.data[6]) * det;
        data[10] = (-m.data[4] * m.data[1] + m.data[0] * m.data[5]) * det;

        data[3] = (m.data[9] * m.data[6] * m.data[3] - m.data[5] * m.data[10] * m.data[3] - m.data[9] * m.data[2] * m.data[7] + m.data[1] * m.data[10] * m.data[7] + m.data[5] * m.data[2] * m.data[11] - m.data[1] * m.data[6] * m.data[11]) * det;
        data[7] = (-m.data[8] * m.data[6] * m.data[3] + m.data[4] * m.data[10] * m.data[3] + m.data[8] * m.data[2] * m.data[7] - m.data[0] * m.data[10] * m.data[7] - m.data[4] * m.data[2] * m.data[11] + m.data[0] * m.data[6] * m.data[11]) * det;
        data[11] = (m.data[8] * m.data[5] * m.data[3] - m.data[4] * m.data[9] * m.data[3] - m.data[8] * m.data[1] * m.data[7] + m.data[0] * m.data[9] * m.data[7] + m.data[4] * m.data[1] * m.data[11] - m.data[0] * m.data[5] * m.data[11]) * det;
    }

    /** Returns a new matrix containing the inverse of this matrix. */
    public Matrix4 inverse() {
        Matrix4 result = new Matrix4();
        result.setInverse(this);
        return result;
    }

    /**
     * Inverts the matrix.
     */
    public void invert() {
        setInverse(this);
    }

    /**
     * Transform the given direction vector by this matrix.
     * 
     * @note When a direction is converted between frames of
     *       reference, there is no translation required.
     * 
     * @param vector The vector to transform.
     */
    public Vector3 transformDirection(Vector3 vector) {
        return new Vector3(vector.getX() * data[0] + vector.getY() * data[1] + vector.getZ() * data[2],

        vector.getX() * data[4] + vector.getY() * data[5] + vector.getZ() * data[6],

        vector.getX() * data[8] + vector.getY() * data[9] + vector.getZ() * data[10]);
    }

    /**
     * Transform the given direction vector by the
     * transformational inverse of this matrix.
     * 
     * @note This function relies on the fact that the inverse of
     *       a pure rotation matrix is its transpose. It separates the
     *       translational and rotation components, transposes the
     *       rotation, and multiplies out. If the matrix is not a
     *       scale and shear free transform matrix, then this function
     *       will not give correct results.
     * 
     * @note When a direction is converted between frames of
     *       reference, there is no translation required.
     * 
     * @param vector The vector to transform.
     */
    public Vector3 transformInverseDirection(Vector3 vector) {
        return new Vector3(vector.getX() * data[0] + vector.getY() * data[4] + vector.getZ() * data[8],

        vector.getX() * data[1] + vector.getY() * data[5] + vector.getZ() * data[9],

        vector.getX() * data[2] + vector.getY() * data[6] + vector.getZ() * data[10]);
    }

    /**
     * Transform the given vector by the transformational inverse
     * of this matrix.
     * 
     * @note This function relies on the fact that the inverse of
     *       a pure rotation matrix is its transpose. It separates the
     *       translational and rotation components, transposes the
     *       rotation, and multiplies out. If the matrix is not a
     *       scale and shear free transform matrix, then this function
     *       will not give correct results.
     * 
     * @param vector The vector to transform.
     */
    public Vector3 transformInverse(Vector3 vector) {
        Vector3 tmp = vector;
        tmp.setX(tmp.getX() - data[3]);
        tmp.setY(tmp.getY() - data[7]);
        tmp.setZ(tmp.getZ() - data[11]);
        return new Vector3(tmp.getX() * data[0] + tmp.getY() * data[4] + tmp.getZ() * data[8],

        tmp.getX() * data[1] + tmp.getY() * data[5] + tmp.getZ() * data[9],

        tmp.getX() * data[2] + tmp.getY() * data[6] + tmp.getZ() * data[10]);
    }

    /**
     * Gets a vector representing one axis (i.e. one column) in the matrix.
     * 
     * @param i The row to return. Row 3 corresponds to the position
     *            of the transform matrix.
     * 
     * @return The vector.
     */
    public Vector3 getAxisVector(int i) {
        return new Vector3(data[i], data[i + 4], data[i + 8]);
    }

    /**
     * Sets this matrix to be the rotation matrix corresponding to
     * the given quaternion.
     */
    public void setOrientationAndPos(Quaternion q, Vector3 pos) {
        data[0] = 1 - (2 * q.getJ() * q.getJ() + 2 * q.getK() * q.getK());
        data[1] = 2 * q.getI() * q.getJ() + 2 * q.getK() * q.getR();
        data[2] = 2 * q.getI() * q.getK() - 2 * q.getJ() * q.getR();
        data[3] = pos.getX();

        data[4] = 2 * q.getI() * q.getJ() - 2 * q.getK() * q.getR();
        data[5] = 1 - (2 * q.getI() * q.getI() + 2 * q.getK() * q.getK());
        data[6] = 2 * q.getJ() * q.getK() + 2 * q.getI() * q.getR();
        data[7] = pos.getY();

        data[8] = 2 * q.getI() * q.getK() + 2 * q.getJ() * q.getR();
        data[9] = 2 * q.getJ() * q.getK() - 2 * q.getI() * q.getR();
        data[10] = 1 - (2 * q.getI() * q.getI() + 2 * q.getJ() * q.getJ());
        data[11] = pos.getZ();
    }

    /**
     * Fills the given array with this transform matrix, so it is
     * usable as an open-gl transform matrix. OpenGL uses a column
     * major format, so that the values are transposed as they are
     * written.
     */
    public void fillGLArray(float[] array) {
        array[0] = (float) data[0];
        array[1] = (float) data[4];
        array[2] = (float) data[8];
        array[3] = 0;

        array[4] = (float) data[1];
        array[5] = (float) data[5];
        array[6] = (float) data[9];
        array[7] = 0;

        array[8] = (float) data[2];
        array[9] = (float) data[6];
        array[10] = (float) data[10];
        array[11] = 0;

        array[12] = (float) data[3];
        array[13] = (float) data[7];
        array[14] = (float) data[11];
        array[15] = 1;
    }

    @Override
    public String toString() {
        return "[" + data[0] + ", " + data[1] + ", " + data[2] + ", " + data[3] + ", " + data[4] + ", " + data[5] + ", " + data[6] + ", " + data[7] + ", " + data[8] + ", " + data[9] + ", " + data[10] + ", " + data[11] + ", " + "]";
    }
}
