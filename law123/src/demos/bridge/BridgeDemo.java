package demos.bridge;

import java.awt.event.KeyEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import br.law123.core.Core;
import br.law123.core.Vector3;
import br.law123.particle.Particle;
import br.law123.particle.links.ParticleCable;
import br.law123.particle.links.ParticleCableConstraint;
import br.law123.particle.links.ParticleRod;

import com.sun.opengl.util.Animator;

import demos.MassAggregateApplication;
import demos.Show;

/**
 * The main demo class definition.
 */
class BridgeDemo extends MassAggregateApplication {

    private Animator animator;

    private static final int ROD_COUNT = 6;
    private static final int CABLE_COUNT = 10;
    private static final int SUPPORT_COUNT = 12;

    private static final int BASE_MASS = 1;
    private static final int EXTRA_MASS = 10;

    ParticleCableConstraint[] supports;
    ParticleCable[] cables;
    ParticleRod[] rods;

    Vector3 massPos;
    Vector3 massDisplayPos = new Vector3();

    /** Creates a new demo object. */
    public BridgeDemo() {
        super(12);
        //cables(0), supports(0), rods(0),
        massPos = new Vector3(0, 0, 0.5f);
        // Create the masses and connections.
        for (int i = 0; i < 12; i++) {
            int x = (i % 12) / 2;
            particleArray[i].setPosition((i / 2) * 2.0f - 5.0f, 4, (i % 2) * 2.0f - 1.0f);
            particleArray[i].setVelocity(0, 0, 0);
            particleArray[i].setDamping(0.9f);
            particleArray[i].setAcceleration(Core.GRAVITY);
            particleArray[i].clearAccumulator();
        }

        // Add the links
        cables = new ParticleCable[CABLE_COUNT];
        for (int i = 0; i < 10; i++) {
            cables[i] = new ParticleCable();
            cables[i].getParticle()[0] = particleArray[i];
            cables[i].getParticle()[1] = particleArray[i + 2];
            cables[i].setMaxLength(1.9f);
            cables[i].setRestitution(0.3f);
            world.getContactGenerators().add(cables[i]);
        }

        supports = new ParticleCableConstraint[SUPPORT_COUNT];
        for (int i = 0; i < SUPPORT_COUNT; i++) {
            supports[i] = new ParticleCableConstraint();
            supports[i].setParticle(particleArray[i]);
            supports[i].setAnchor(new Vector3((i / 2) * 2.2f - 5.5f, 6, (i % 2) * 1.6f - 0.8f));
            if (i < 6) supports[i].setMaxLength((i / 2) * 0.5f + 3.0f);
            else supports[i].setMaxLength(5.5f - (i / 2) * 0.5f);
            supports[i].setRestitution(0.5f);
            world.getContactGenerators().add(supports[i]);
        }

        rods = new ParticleRod[ROD_COUNT];
        for (int i = 0; i < 6; i++) {
            rods[i] = new ParticleRod();
            rods[i].getParticle()[0] = particleArray[i * 2];
            rods[i].getParticle()[1] = particleArray[i * 2 + 1];
            rods[i].setLength(2);
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

    /**
     * Updates particle masses to take into account the mass
     * that's crossing the bridge.
     */
    void updateAdditionalMass() {
        for (int i = 0; i < 12; i++) {
            particleArray[i].setMass(BASE_MASS);
        }

        // Find the coordinates of the mass as an index and proportion
        int x = (int) massPos.getX();
        double xp = massPos.getX() % 1.0f;
        if (x < 0) {
            x = 0;
            xp = 0;
        }
        if (x >= 5) {
            x = 5;
            xp = 0;
        }

        int z = (int) massPos.getZ();
        double zp = massPos.getZ() % 1.0f;
        if (z < 0) {
            z = 0;
            zp = 0;
        }
        if (z >= 1) {
            z = 1;
            zp = 0;
        }

        // Calculate where to draw the mass
        massDisplayPos.clear();

        // Add the proportion to the correct masses
        particleArray[x * 2 + z].setMass(BASE_MASS + EXTRA_MASS * (1 - xp) * (1 - zp));
        massDisplayPos.addScaledVector(particleArray[x * 2 + z].getPosition(), (1 - xp) * (1 - zp));

        if (xp > 0) {
            particleArray[x * 2 + z + 2].setMass(BASE_MASS + EXTRA_MASS * xp * (1 - zp));
            massDisplayPos.addScaledVector(particleArray[x * 2 + z + 2].getPosition(), xp * (1 - zp));

            if (zp > 0) {
                particleArray[x * 2 + z + 3].setMass(BASE_MASS + EXTRA_MASS * xp * zp);
                massDisplayPos.addScaledVector(particleArray[x * 2 + z + 3].getPosition(), xp * zp);
            }
        }
        if (zp > 0) {
            particleArray[x * 2 + z + 1].setMass(BASE_MASS + EXTRA_MASS * (1 - xp) * zp);
            massDisplayPos.addScaledVector(particleArray[x * 2 + z + 1].getPosition(), (1 - xp) * zp);
        }
    }

    /** Returns the window title for the demo. */
    @Override
    protected String getTitle() {
        return "Ponte memo";
    }

    /** Display the particles. */
    @Override
    public void display(javax.media.opengl.GLAutoDrawable arg0) {
        update();
        super.display(arg0);
        System.out.println("asda");

        getGl().glBegin(GL.GL_LINES);
        getGl().glColor3f(0, 0, 1);
        for (int i = 0; i < ROD_COUNT; i++) {
            Particle[] particles = rods[i].getParticle();
            Vector3 p0 = particles[0].getPosition();
            Vector3 p1 = particles[1].getPosition();
            getGl().glVertex3d(p0.getX(), p0.getY(), p0.getZ());
            getGl().glVertex3d(p1.getX(), p1.getY(), p1.getZ());
        }

        getGl().glColor3f(0, 1, 0);
        for (int i = 0; i < CABLE_COUNT; i++) {
            Particle[] particles = cables[i].getParticle();
            Vector3 p0 = particles[0].getPosition();
            Vector3 p1 = particles[1].getPosition();
            getGl().glVertex3d(p0.getX(), p0.getY(), p0.getZ());
            getGl().glVertex3d(p1.getX(), p1.getY(), p1.getZ());
        }

        getGl().glColor3f(0.7f, 0.7f, 0.7f);
        for (int i = 0; i < SUPPORT_COUNT; i++) {
            Vector3 p0 = supports[i].getParticle().getPosition();
            Vector3 p1 = supports[i].getAnchor();
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
        System.out.println("hehehe");
        switch (e.getKeyChar()) {
            case 's':
            case 'S':
                massPos.setZ(massPos.getZ() + 0.1f);
                if (massPos.getZ() > 1.0f) massPos.setZ(1.0f);
                break;
            case 'w':
            case 'W':
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
                if (massPos.getX() > 5.0f) massPos.setX(5.0f);
                break;

            default:
                super.keyPressed(e);
        }
    }

    public static void main(String[] args) {
        new Show(new BridgeDemo()).setVisible(true);
    }

}
