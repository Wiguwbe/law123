package demos;

import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import br.law123.core.Vector3;
import br.law123.particle.Particle;
import br.law123.particle.world.GroundContacts;
import br.law123.particle.world.ParticleWorld;

/**
 * This application adds additional functionality used in the mass-aggregate demos.
 */
public class MassAggregateApplication extends Application {

    protected ParticleWorld world;
    protected Particle[] particleArray;
    private GroundContacts groundContactGenerator = new GroundContacts();

    public MassAggregateApplication(int particleCount) {
        this.world = new ParticleWorld(particleCount * 10);
        particleArray = new Particle[particleCount];
        for (int i = 0; i < particleCount; i++) {
            particleArray[i] = new Particle();
            world.getParticles().add(particleArray[i]);
        }

        groundContactGenerator.init(world.getParticles());
        world.getContactGenerators().add(groundContactGenerator);
    }

    //virtual ~MassAggregateApplication();

    /** Update the particle positions. */
    @Override
    protected void update() {
        // Clear accumulators
        world.startFrame();

        // Find the duration of the last frame in seconds
        float duration = TimingData.get().getLastFrameDuration() * 0.001f;
        if (duration <= 0.0f) return;

        // Run the simulation
        world.runPhysics(duration);

        super.update();
    }

    @Override
    public void display(GLAutoDrawable arg0) {
        // Clear the view port and set the camera direction
        super.display(arg0);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        glu.gluLookAt(0.0, 3.5, 8.0, 0.0, 3.5, 0.0, 0.0, 1.0, 0.0);

        gl.glColor3f(0, 0, 0);

        List<Particle> particles = world.getParticles();
        for (Particle particle : particles) {
            Vector3 pos = particle.getPosition();
            gl.glPushMatrix();
            gl.glTranslated(pos.getX(), pos.getY(), pos.getZ());
            glut.glutSolidSphere(0.1f, 20, 10);
            gl.glPopMatrix();
        }
    }

}
