package demos.explosion;

import javax.media.opengl.GL;

import br.law123.collide.CollisionBox;
import br.law123.core.Matrix3;
import br.law123.core.Quaternion;
import br.law123.core.Vector3;
import br.law123.random.Random;
import br.law123.rigidbody.RigidBody;

import com.sun.opengl.util.GLUT;

class Box extends CollisionBox {

    static final Vector3 minPos = new Vector3(-5, 5, -5);
    static final Vector3 maxPos = new Vector3(5, 10, 5);
    static final Vector3 minSize = new Vector3(0.5f, 0.5f, 0.5f);
    static final Vector3 maxSize = new Vector3(4.5f, 1.5f, 1.5f);

    private boolean isOverlapping;

    public boolean isOverlapping() {
        return isOverlapping;
    }

    public void setOverlapping(boolean isOverlapping) {
        this.isOverlapping = isOverlapping;
    }

    Box() {
        setBody(new RigidBody());
    }

    /** Draws the box, excluding its shadow. */
    void render(GL gl, GLUT glut) {
        // Get the OpenGL transformation
        float[] mat = new float[16];
        getBody().getGLTransform(mat);

        if (isOverlapping) gl.glColor3f(0.7f, 1.0f, 0.7f);
        else if (getBody().getAwake()) gl.glColor3f(1.0f, 0.7f, 0.7f);
        else gl.glColor3f(0.7f, 0.7f, 1.0f);

        gl.glPushMatrix();
        gl.glMultMatrixf(mat, 0);
        gl.glScaled(getHalfSize().getX() * 2, getHalfSize().getY() * 2, getHalfSize().getZ() * 2);
        glut.glutSolidCube(1.0f);
        gl.glPopMatrix();
    }

    /** Draws the ground plane shadow for the box. */
    void renderShadow(GL gl, GLUT glut) {
        // Get the OpenGL transformation
        float[] mat = new float[16];
        getBody().getGLTransform(mat);

        gl.glPushMatrix();
        gl.glScalef(1.0f, 0, 1.0f);
        gl.glMultMatrixf(mat, 0);
        gl.glScaled(getHalfSize().getX() * 2, getHalfSize().getY() * 2, getHalfSize().getZ() * 2);
        glut.glutSolidCube(1.0f);
        gl.glPopMatrix();
    }

    /** Sets the box to a specific location. */
    void setState(Vector3 position, Quaternion orientation, Vector3 extents, Vector3 velocity) {
        getBody().setPosition(position);
        getBody().setOrientation(orientation);
        getBody().setVelocity(velocity);
        getBody().setRotation(new Vector3(0, 0, 0));
        setHalfSize(extents);

        double mass = getHalfSize().getX() * getHalfSize().getY() * getHalfSize().getZ() * 8.0f;
        getBody().setMass(mass);

        Matrix3 tensor = new Matrix3();
        tensor.setBlockInertiaTensor(getHalfSize(), mass);
        getBody().setInertiaTensor(tensor);

        getBody().setLinearDamping(0.95f);
        getBody().setAngularDamping(0.8f);
        getBody().clearAccumulators();
        getBody().setAcceleration(0, -10.0f, 0);

        getBody().setAwake();

        getBody().calculateDerivedData();
    }

    /** Positions the box at a random location. */
    void random(Random random) {
        setState(random.randomVector(minPos, maxPos), random.randomQuaternion(), random.randomVector(minSize, maxSize), new Vector3());
    }
}
