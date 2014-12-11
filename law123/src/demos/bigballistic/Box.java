package demos.bigballistic;

import javax.media.opengl.GL;

import br.law123.collide.CollisionBox;
import br.law123.core.Matrix3;
import br.law123.core.Vector3;
import br.law123.rigidbody.RigidBody;

import com.sun.opengl.util.GLUT;

class Box extends CollisionBox {

    Box() {
        setBody(new RigidBody());
    }

    /** Draws the box, excluding its shadow. */
    void render(GL gl, GLUT glut) {
        // Get the OpenGL transformation
        float[] mat = new float[16];
        getBody().getGLTransform(mat);

        gl.glPushMatrix();
        gl.glMultMatrixf(mat, 0);
        gl.glScaled(getHalfSize().getX() * 2, getHalfSize().getY() * 2, getHalfSize().getZ() * 2);
        
        glut.glutSolidCube(1.0f);
        gl.glPopMatrix();
    }

    /** Sets the box to a specific location. */
    void setState(double y, double z) {
        getBody().setPosition(0, y, z);
        getBody().setOrientation(1, 0, 0, 0);
        getBody().setVelocity(0, 0, 0);
        getBody().setRotation(new Vector3(1, 0, 0));
        setHalfSize(new Vector3(5, 5, 5));

        double mass = getHalfSize().getX() * getHalfSize().getY() * getHalfSize().getZ() * 8.0f;
        getBody().setMass(mass);

        Matrix3 tensor = new Matrix3();
        tensor.setBlockInertiaTensor(getHalfSize(), mass);
        getBody().setInertiaTensor(tensor);

        getBody().setLinearDamping(0.95f);
        getBody().setAngularDamping(0.8f);
        getBody().clearAccumulators();
        getBody().setAcceleration(0, -10.0f, 0);

        getBody().setCanSleep(false);
        getBody().setAwake();

        getBody().calculateDerivedData();
        calculateInternals();
    }
}
