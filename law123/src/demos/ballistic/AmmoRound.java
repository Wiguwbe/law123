package demos.ballistic;

import javax.media.opengl.GL;

import br.law123.core.Vector3;
import br.law123.particle.Particle;

import com.sun.opengl.util.GLUT;

/**
 * Holds a single ammunition round record.
 */
class AmmoRound {

    Particle particle = new Particle();
    ShotType type;
    long startTime;

    /** Draws the round. */
    void render(GL gl, GLUT glut) {
        Vector3 position = new Vector3();
        position = particle.getPosition();

        gl.glColor3f(0, 0, 0);
        gl.glPushMatrix();
        gl.glTranslated(position.getX(), position.getY(), position.getZ());
        glut.glutSolidSphere(0.3f, 5, 4);
        gl.glPopMatrix();

        gl.glColor3f(0.75f, 0.75f, 0.75f);
        gl.glPushMatrix();
        gl.glTranslated(position.getX(), 0, position.getZ());
        gl.glScalef(1.0f, 0.1f, 1.0f);
        glut.glutSolidSphere(0.6f, 5, 4);
        gl.glPopMatrix();
    }
}
