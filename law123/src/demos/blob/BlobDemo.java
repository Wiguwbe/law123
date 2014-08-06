package demos.blob;

import java.awt.event.KeyEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import br.law123.core.Core;
import br.law123.core.Vector3;
import br.law123.particle.Particle;
import br.law123.particle.world.ParticleWorld;
import br.law123.random.Random;

import com.sun.opengl.util.Animator;

import demos.Application;
import demos.Show;
import demos.TimingData;

/**
 * The main demo class definition.
 */
class BlobDemo extends Application {

    private Animator a;

    private Particle[] blobs;

    private Platform[] platforms;

    private ParticleWorld world;

    private BlobForceGenerator blobForceGenerator = new BlobForceGenerator();

    /* The control for the x-axis. */
    float xAxis;

    /* The control for the y-axis. */
    float yAxis;

    @Override
    public void init(GLAutoDrawable drawable) {
        super.init(drawable);

        a = new Animator(drawable);
        a.start();
    }

    void reset() {
        Random r = new Random();
        Platform p = platforms[BlobUtils.PLATFORM_COUNT - 2];
        double fraction = 1.0 / BlobUtils.BLOB_COUNT;
        Vector3 delta = p.getEnd().sub(p.getStart());
        for (int i = 0; i < BlobUtils.BLOB_COUNT; i++) {
            int me = (i + BlobUtils.BLOB_COUNT / 2) % BlobUtils.BLOB_COUNT;
            blobs[i].setPosition(p.getStart().sum(delta).mult(me * 0.8f * fraction + 0.1f).sum(new Vector3(0, 1.0f + r.randomReal(), 0)));
            blobs[i].setVelocity(0, 0, 0);
            blobs[i].clearAccumulator();
        }
    }

    public BlobDemo() {
        xAxis = 0;
        yAxis = 0;
        world = new ParticleWorld(BlobUtils.PLATFORM_COUNT + BlobUtils.BLOB_COUNT, BlobUtils.PLATFORM_COUNT);
        // Create the blob storage
        blobs = new Particle[BlobUtils.BLOB_COUNT];
        Random r = new Random();

        // Create the force generator
        blobForceGenerator.particles = blobs;
        blobForceGenerator.maxAttraction = 20.0f;
        blobForceGenerator.maxReplusion = 10.0f;
        blobForceGenerator.minNaturalDistance = BlobUtils.BLOB_RADIUS * 0.75f;
        blobForceGenerator.maxNaturalDistance = BlobUtils.BLOB_RADIUS * 1.5f;
        blobForceGenerator.maxDistance = BlobUtils.BLOB_RADIUS * 2.5f;
        blobForceGenerator.maxFloat = 2;
        blobForceGenerator.floatHead = 8.0f;

        // Create the platforms
        platforms = new Platform[BlobUtils.PLATFORM_COUNT];
        for (int i = 0; i < BlobUtils.PLATFORM_COUNT; i++) {
            platforms[i] = new Platform();
            platforms[i].setStart(new Vector3(i % 2 * 10.0f - 5.0f, i * 4.0f + ((i % 2) == 1 ? 0.0f : 2.0f), 0));
            platforms[i].getStart().setX(platforms[i].getStart().getX() + r.randomBinomial(2.0f));
            platforms[i].getStart().setY(platforms[i].getStart().getY() + r.randomBinomial(2.0f));

            platforms[i].setEnd(new Vector3(i % 2 * 10.0f + 5.0f, i * 4.0f + ((i % 2) == 1 ? 2.0f : 0.0f), 0));
            platforms[i].getEnd().setX(platforms[i].getStart().getX() + r.randomBinomial(2.0f));
            platforms[i].getEnd().setY(platforms[i].getStart().getY() + r.randomBinomial(2.0f));

            // Make sure the platform knows which particles it 
            // should collide with.
            platforms[i].setParticles(blobs);
            world.getContactGenerators().add(platforms[i]);
        }

        // Create the blobs.
        Platform p = platforms[BlobUtils.PLATFORM_COUNT - 2];
        double fraction = 1.0 / BlobUtils.BLOB_COUNT;
        Vector3 delta = p.getEnd().sub(p.getStart());
        for (int i = 0; i < BlobUtils.BLOB_COUNT; i++) {
            int me = (i + BlobUtils.BLOB_COUNT / 2) % BlobUtils.BLOB_COUNT;
            blobs[i] = new Particle();
            blobs[i].setPosition(p.getStart().sum(delta).mult(me * 0.8f * fraction + 0.1f).sum(new Vector3(0, 1.0f + r.randomReal(), 0)));

            blobs[i].setVelocity(0, 0, 0);
            blobs[i].setDamping(0.2f);
            blobs[i].setAcceleration(new Vector3(Core.GRAVITY).mult(0.4f));
            blobs[i].setMass(1.0f);
            blobs[i].clearAccumulator();

            world.getParticles().add(blobs[i]);
            world.getForceRegistry().add(blobs[i], blobForceGenerator);
        }
    }

    @Override
    protected String getTitle() {
        return "BlOB";
    }

    /** Display the particles. */
    @Override
    public void display(GLAutoDrawable arg0) {
        update();

        super.display(arg0);
        Vector3 pos = blobs[0].getPosition();

        // Clear the view port and set the camera direction
        getGl().glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        getGl().glLoadIdentity();
        getGlu().gluLookAt(pos.getX(), pos.getY(), 6.0, pos.getX(), pos.getY(), 0.0, 0.0, 1.0, 0.0);

        getGl().glColor3f(0, 0, 0);

        getGl().glBegin(GL.GL_LINES);
        getGl().glColor3f(0, 0, 1);
        for (int i = 0; i < BlobUtils.PLATFORM_COUNT; i++) {
            final Vector3 p0 = platforms[i].getStart();
            final Vector3 p1 = platforms[i].getEnd();
            getGl().glVertex3d(p0.getX(), p0.getY(), p0.getZ());
            getGl().glVertex3d(p1.getX(), p1.getY(), p1.getZ());
        }
        getGl().glEnd();

        getGl().glColor3f(1, 0, 0);
        for (int i = 0; i < BlobUtils.BLOB_COUNT; i++) {
            final Vector3 p = blobs[i].getPosition();
            getGl().glPushMatrix();
            getGl().glTranslated(p.getX(), p.getY(), p.getZ());
            getGlut().glutSolidSphere(BlobUtils.BLOB_RADIUS, 12, 12);
            getGl().glPopMatrix();
        }

        Vector3 p = blobs[0].getPosition();
        Vector3 v = blobs[0].getVelocity().mult(0.05f);
        v.trim(BlobUtils.BLOB_RADIUS * 0.5f);
        p = p.sum(v);
        getGl().glPushMatrix();
        getGl().glTranslated(p.getX() - BlobUtils.BLOB_RADIUS * 0.2f, p.getY(), BlobUtils.BLOB_RADIUS);
        getGl().glColor3f(1, 1, 1);
        getGlut().glutSolidSphere(BlobUtils.BLOB_RADIUS * 0.2f, 8, 8);
        getGl().glTranslatef(0, 0, BlobUtils.BLOB_RADIUS * 0.2f);
        getGl().glColor3f(0, 0, 0);
        getGlut().glutSolidSphere(BlobUtils.BLOB_RADIUS * 0.1f, 8, 8);
        getGl().glTranslatef(BlobUtils.BLOB_RADIUS * 0.4f, 0, -BlobUtils.BLOB_RADIUS * 0.2f);
        getGl().glColor3f(1, 1, 1);
        getGlut().glutSolidSphere(BlobUtils.BLOB_RADIUS * 0.2f, 8, 8);
        getGl().glTranslatef(0, 0, BlobUtils.BLOB_RADIUS * 0.2f);
        getGl().glColor3f(0, 0, 0);
        getGlut().glutSolidSphere(BlobUtils.BLOB_RADIUS * 0.1f, 8, 8);
        getGl().glPopMatrix();
    }

    @Override
    protected void update() {
        // Clear accumulators
        world.startFrame();

        // Find the duration of the last frame in seconds
        float duration = TimingData.get().getLastFrameDuration() * 0.001f;
        if (duration <= 0.0f) return;

        // Recenter the axes
        xAxis *= Math.pow(0.1f, duration);
        yAxis *= Math.pow(0.1f, duration);

        // Move the controlled blob
        blobs[0].addForce(new Vector3(xAxis, yAxis, 0).mult(10.0f));

        // Run the simulation
        world.runPhysics(duration);

        // Bring all the particles back to 2d
        Vector3 position = new Vector3();
        for (int i = 0; i < BlobUtils.BLOB_COUNT; i++) {
            blobs[i].getPosition(position);
            position.setZ(0.0f);
            blobs[i].setPosition(position);
        }

        super.update();
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
        switch (arg0.getKeyChar()) {
            case 'w':
            case 'W':
                yAxis = 1.0f;
                break;
            case 's':
            case 'S':
                yAxis = -1.0f;
                break;
            case 'a':
            case 'A':
                xAxis = -1.0f;
                break;
            case 'd':
            case 'D':
                xAxis = 1.0f;
                break;
            case 'r':
            case 'R':
                reset();
                break;
        }
    }

    public static void main(String[] args) {
        new Show(new BlobDemo()).setVisible(true);
    }

}
