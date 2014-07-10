package demos.bigballistic;

import javax.media.opengl.GL;

import br.law123.collide.CollisionSphere;
import br.law123.core.Matrix3;
import br.law123.rigidbody.RigidBody;

import com.sun.opengl.util.GLUT;

import demos.TimingData;
import demos.ballistic.ShotType;

class AmmoRound extends CollisionSphere {
    ShotType type;
    long startTime;

    AmmoRound() {
        setBody(new RigidBody());
    }

    /** Draws the box, excluding its shadow. */
    void render(GL gl, GLUT glut) {
        // Get the OpenGL transformation

        float[] mat = new float[16];
        getBody().getGLTransform(mat);

        gl.glPushMatrix();
        gl.glMultMatrixf(mat, 0);
        glut.glutSolidSphere(getRadius(), 20, 20);
        gl.glPopMatrix();
    }

    /** Sets the box to a specific location. */
    void setState(ShotType shotType)
    {
        type = shotType;

        // Set the properties of the particle
        switch(type)
        {
        case PISTOL:
            getBody().setMass(1.5f);
            getBody().setVelocity(0.0f, 0.0f, 20.0f);
            getBody().setAcceleration(0.0f, -0.5f, 0.0f);
            getBody().setDamping(0.99f, 0.8f);
            setRadius(0.2f);
            break;

        case ARTILLERY:
            getBody().setMass(200.0f); // 200.0kg
            getBody().setVelocity(0.0f, 30.0f, 40.0f); // 50m/s
            getBody().setAcceleration(0.0f, -21.0f, 0.0f);
            getBody().setDamping(0.99f, 0.8f);
            setRadius(0.4f);
            break;

        case FIREBALL:
            getBody().setMass(4.0f); // 4.0kg - mostly blast damage
            getBody().setVelocity(0.0f, -0.5f, 10.0); // 10m/s
            getBody().setAcceleration(0.0f, 0.3f, 0.0f); // Floats up
            getBody().setDamping(0.9f, 0.8f);
            setRadius(0.6f);
            break;

        case LASER:
            // Note that this is the kind of laser bolt seen in films,
            // not a doubleistic laser beam!
            getBody().setMass(0.1f); // 0.1kg - almost no weight
            getBody().setVelocity(0.0f, 0.0f, 100.0f); // 100m/s
            getBody().setAcceleration(0.0f, 0.0f, 0.0f); // No gravity
            getBody().setDamping(0.99f, 0.8f);
            setRadius(0.2f);
            break;
        }

        getBody().setCanSleep(false);
        getBody().setAwake();

        Matrix3 tensor = new Matrix3();
        double coeff = 0.4f*getBody().getMass()*getRadius()*getRadius();
        tensor.setInertiaTensorCoeffs(coeff,coeff,coeff);
        getBody().setInertiaTensor(tensor);

        // Set the data common to all particle types
        getBody().setPosition(0.0f, 1.5f, 0.0f);
        startTime = TimingData.get().getLastFrameTimestamp();

        // Clear the force accumulators
        getBody().calculateDerivedData();
        calculateInternals();
    }
}