package demos.platform;

import java.awt.event.KeyEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import br.law123.core.Core;
import br.law123.core.Vector3;
import br.law123.particle.Particle;
import br.law123.particle.links.ParticleRod;

import com.sun.opengl.util.Animator;

import demos.MassAggregateApplication;
import demos.Show;

/**
 * The main demo class definition.
 */
class PlatformDemo extends MassAggregateApplication {

    Animator animator;

    private static final int ROD_COUNT = 15;

    private static final int BASE_MASS = 1;
    private static final int EXTRA_MASS = 10;

    ParticleRod[] rods;

    Vector3 massPos = new Vector3();
    Vector3 massDisplayPos = new Vector3();

    /**
     * Updates particle masses to take into account the mass
     * that's on the platform.
     */
    void updateAdditionalMass() {
        for (int i = 2; i < 6; i++) {
            particleArray[i].setMass(BASE_MASS);
        }

        // Find the coordinates of the mass as an index and proportion
        double xp = massPos.getX();
        if (xp < 0) xp = 0;
        if (xp > 1) xp = 1;

        double zp = massPos.getZ();
        if (zp < 0) zp = 0;
        if (zp > 1) zp = 1;

        // Calculate where to draw the mass
        massDisplayPos.clear();

        // Add the proportion to the correct masses
        particleArray[2].setMass(BASE_MASS + EXTRA_MASS * (1 - xp) * (1 - zp));
        massDisplayPos.addScaledVector(particleArray[2].getPosition(), (1 - xp) * (1 - zp));

        if (xp > 0) {
            particleArray[4].setMass(BASE_MASS + EXTRA_MASS * xp * (1 - zp));
            massDisplayPos.addScaledVector(particleArray[4].getPosition(), xp * (1 - zp));

            if (zp > 0) {
                particleArray[5].setMass(BASE_MASS + EXTRA_MASS * xp * zp);
                massDisplayPos.addScaledVector(particleArray[5].getPosition(), xp * zp);
            }
        }
        if (zp > 0) {
            particleArray[3].setMass(BASE_MASS + EXTRA_MASS * (1 - xp) * zp);
            massDisplayPos.addScaledVector(particleArray[3].getPosition(), (1 - xp) * zp);
        }
    }

    /** Creates a new demo object. */
    public PlatformDemo() {
        super(6);
        massPos = new Vector3(0, 0, 0.5f);

        // Create the masses and connections.
        particleArray[0].setPosition(0, 0, 1);
        particleArray[1].setPosition(0, 0, -1);
        particleArray[2].setPosition(-3, 2, 1);
        particleArray[3].setPosition(-3, 2, -1);
        particleArray[4].setPosition(4, 2, 1);
        particleArray[5].setPosition(4, 2, -1);
        for (int i = 0; i < 6; i++) {
            particleArray[i].setMass(BASE_MASS);
            particleArray[i].setVelocity(0, 0, 0);
            particleArray[i].setDamping(0.9f);
            particleArray[i].setAcceleration(Core.GRAVITY);
            particleArray[i].clearAccumulator();
        }

        rods = new ParticleRod[ROD_COUNT];

        for (int i = 0; i < rods.length; i++) {
            rods[i] = new ParticleRod();
        }

        rods[0].getParticle()[0] = particleArray[0];
        rods[0].getParticle()[1] = particleArray[1];
        rods[0].setLength(2);
        rods[1].getParticle()[0] = particleArray[2];
        rods[1].getParticle()[1] = particleArray[3];
        rods[1].setLength(2);
        rods[2].getParticle()[0] = particleArray[4];
        rods[2].getParticle()[1] = particleArray[5];
        rods[2].setLength(2);

        rods[3].getParticle()[0] = particleArray[2];
        rods[3].getParticle()[1] = particleArray[4];
        rods[3].setLength(7);
        rods[4].getParticle()[0] = particleArray[3];
        rods[4].getParticle()[1] = particleArray[5];
        rods[4].setLength(7);

        rods[5].getParticle()[0] = particleArray[0];
        rods[5].getParticle()[1] = particleArray[2];
        rods[5].setLength(3.606);
        rods[6].getParticle()[0] = particleArray[1];
        rods[6].getParticle()[1] = particleArray[3];
        rods[6].setLength(3.606);

        rods[7].getParticle()[0] = particleArray[0];
        rods[7].getParticle()[1] = particleArray[4];
        rods[7].setLength(4.472);
        rods[8].getParticle()[0] = particleArray[1];
        rods[8].getParticle()[1] = particleArray[5];
        rods[8].setLength(4.472);

        rods[9].getParticle()[0] = particleArray[0];
        rods[9].getParticle()[1] = particleArray[3];
        rods[9].setLength(4.123);
        rods[10].getParticle()[0] = particleArray[2];
        rods[10].getParticle()[1] = particleArray[5];
        rods[10].setLength(7.28);
        rods[11].getParticle()[0] = particleArray[4];
        rods[11].getParticle()[1] = particleArray[1];
        rods[11].setLength(4.899);
        rods[12].getParticle()[0] = particleArray[1];
        rods[12].getParticle()[1] = particleArray[2];
        rods[12].setLength(4.123);
        rods[13].getParticle()[0] = particleArray[3];
        rods[13].getParticle()[1] = particleArray[4];
        rods[13].setLength(7.28);
        rods[14].getParticle()[0] = particleArray[5];
        rods[14].getParticle()[1] = particleArray[0];
        rods[14].setLength(4.899);

        for (int i = 0; i < ROD_COUNT; i++) {
            world.getContactGenerators().add(rods[i]);
        }

        updateAdditionalMass();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        super.init(drawable);

        animator = new Animator(drawable);
        animator.start();
    }

    /** Returns the window title for the demo. */
    @Override
    protected String getTitle() {
        return "Pata-forma";
    }

    /** Display the particles. */
    @Override
    public void display(javax.media.opengl.GLAutoDrawable arg0) {
        update();
        super.display(arg0);

        getGl().glBegin(GL.GL_LINES);
        getGl().glColor3f(0, 0, 1);
        for (int i = 0; i < ROD_COUNT; i++) {
            Particle[] particles = rods[i].getParticle();
            Vector3 p0 = particles[0].getPosition();
            Vector3 p1 = particles[1].getPosition();
            getGl().glVertex3d(p0.getX(), p0.getY(), p0.getZ());
            getGl().glVertex3d(p1.getX(), p1.getY(), p1.getZ());
        }
        getGl().glEnd();

        getGl().glColor3f(1, 0, 0);
        getGl().glPushMatrix();
        getGl().glTranslated(massDisplayPos.getX(), massDisplayPos.getY() + 0.25f, massDisplayPos.getZ());
        getGlut().glutSolidSphere(0.25f, 20, 10);
        getGl().glPopMatrix();
    }

    /** Update the particle positions. */
    @Override
    protected void update() {
        super.update();

        updateAdditionalMass();
    }

    /** Handle a key press. */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'w':
            case 'W':
                massPos.setZ(massPos.getZ() + 0.1f);
                if (massPos.getZ() > 1.0f) massPos.setZ(1.0f);
                break;
            case 's':
            case 'S':
                massPos.setZ(massPos.getZ() - 0.1f);
                if (massPos.getZ() < 0.0f) massPos.setZ(0.0f);
                break;
            case 'a':
            case 'A':
                massPos.setX(massPos.getX() - 0.1f);
                if (massPos.getX() < 0.0f) massPos.setX(0.0f);
                break;
            case 'd':
            case 'D':
                massPos.setX(massPos.getX() + 0.1f);
                if (massPos.getX() > 1.0f) massPos.setX(1.0f);
                break;

            default:
                super.keyPressed(e);
        }
    }

    public static void main(String[] args) {
        new Show(new PlatformDemo()).setVisible(true);
    }

}
