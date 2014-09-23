package demos.explosion;

import javax.media.opengl.GL;

import br.law123.collide.CollisionSphere;
import br.law123.core.Matrix3;
import br.law123.core.Quaternion;
import br.law123.core.Vector3;
import br.law123.random.Random;
import br.law123.rigidbody.RigidBody;

import com.sun.opengl.util.GLUT;

class Ball extends CollisionSphere {

    final static Vector3 minPos = new Vector3(-5, 5, -5);
    final static Vector3 maxPos = new Vector3(5, 10, 5);

    public Ball() {
        setBody(new RigidBody());
    }

    /** Draws the box, excluding its shadow. */
    void render(GL gl, GLUT glut) {
        // Get the OpenGL transformation
        float[] mat = new float[16];
        getBody().getGLTransform(mat);

        if (getBody().getAwake()) {

            gl.glColor3f(1.0f, 0.7f, 0.7f);
        } else {
            gl.glColor3f(0.7f, 0.7f, 1.0f);
        }

        gl.glPushMatrix();
        gl.glMultMatrixf(mat, 0);
        glut.glutSolidSphere(getRadius(), 20, 20);
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
        glut.glutSolidSphere(getRadius(), 20, 20);
        gl.glPopMatrix();
    }

    /** Sets the box to a specific location. */
    void setState(Vector3 position, Quaternion orientation, float d, Vector3 velocity) {
        getBody().setPosition(position);
        getBody().setOrientation(orientation);
        getBody().setVelocity(velocity);
        getBody().setRotation(new Vector3(20, -10, 0));
        setRadius(d);

        float mass = 4.0f * 0.3333f * 3.1415f * d * d * d;
        getBody().setMass(mass);

        Matrix3 tensor = new Matrix3();
        float coeff = 0.4f * mass * d * d;
        tensor.setInertiaTensorCoeffs(coeff, coeff, coeff);
        getBody().setInertiaTensor(tensor);

        getBody().setLinearDamping(0.95f);
        getBody().setAngularDamping(0.8f);
        getBody().clearAccumulators();
        getBody().setAcceleration(0, -10.0f, 0);

        //body.setCanSleep(false);
        getBody().setAwake();

        getBody().calculateDerivedData();
    }

    /** Positions the box at a random location. */
    void random(Random random) {
        setState(random.randomVector(minPos, maxPos), random.randomQuaternion(), random.randomReal(0.5f, 1.5f), new Vector3());
    }
    
    @Override
    public void collisionDetection(double duration) {
        Vector3 rotation = getBody().getRotation();
        getBody().setRotation(rotation.getX() * -1, rotation.getY() * -1, rotation.getZ() * -1);
    }

}
