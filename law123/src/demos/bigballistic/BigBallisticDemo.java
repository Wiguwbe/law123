package demos.bigballistic;

import java.awt.event.KeyEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import br.law123.collide.CollisionDetector;
import br.law123.collide.CollisionPlane;
import br.law123.core.Vector3;

import com.sun.opengl.util.Animator;

import demos.RigidBodyApplication;
import demos.Show;
import demos.TimingData;
import demos.ballistic.ShotType;

/**
 * The main demo class definition.
 */
class BigBallisticDemo extends RigidBodyApplication {

    private Animator animator;

    private final static float lightPosition[] = { -1, 1, 0, 0 };

    /**
     * Holds the maximum number of rounds that can be
     * fired.
     */
    private final static int ammoRounds = 256;

    /** Holds the particle data. */
    private AmmoRound[] ammo = new AmmoRound[ammoRounds];

    /**
     * Holds the number of boxes in the simulation.
     */
    private final static int boxes = 20;

    /** Holds the box data. */
    private Box[] boxData = new Box[boxes];

    /** Holds the current shot type. */
    ShotType currentShotType;

    /** Resets the position of all the boxes and primes the explosion. */
    @Override
    protected void reset() {
        // Make all shots unused
        for (int i = 0; i < ammo.length; i++) {
            ammo[i] = new AmmoRound();
            ammo[i].type = ShotType.UNUSED;
        }

        // Initialise the box
        double z = 20.0f;
        for (int i = 0; i < boxData.length; i++) {
            boxData[i] = new Box();
            boxData[i].setState(z);
            z += 10.0f;
        }
    }

    /** Build the contacts for the current situation. */
    @Override
    protected void generateContacts() {
        // Create the ground plane data
        CollisionPlane plane = new CollisionPlane();
        plane.setDirection(new Vector3(0, 1, 0));
        plane.setOffset(0);

        // Set up the collision data structure
        getcData().reset(maxContacts);
        getcData().setFriction(0.9);
        getcData().setRestitution(0.1);
        getcData().setTolerance(0.1);

        // Check ground plane collisions
        for (Box box : boxData) {
            if (!getcData().hasMoreContacts()) {
                return;
            }
            CollisionDetector.boxAndHalfSpace(box, plane, getcData());

            // Check for collisions with each shot
            for (AmmoRound shot : ammo) {
                if (shot.type != ShotType.UNUSED) {
                    if (!getcData().hasMoreContacts()) return;

                    // When we get a collision, remove the shot
                    if (CollisionDetector.boxAndSphere(box, shot, getcData())) {
                        shot.type = ShotType.UNUSED;
                    }
                }
            }
        }

        // NB We aren't checking box-box collisions.
    }

    /** Processes the objects in the simulation forward in time. */
    @Override
    protected void updateObjects(double duration) {
        // Update the physics of each particle in turn
        for (AmmoRound shot : ammo) {
            if (shot.type != ShotType.UNUSED) {
                // Run the physics
                shot.getBody().integrate(duration);
                shot.calculateInternals();

                // Check if the particle is now invalid
                if (shot.getBody().getPosition().getY() < 0.0f || shot.startTime + 5000 < TimingData.get().getLastFrameTimestamp() || shot.getBody().getPosition().getZ() > 200.0f) {
                    // We simply set the shot type to be unused, so the
                    // memory it occupies can be reused by another shot.
                    shot.type = ShotType.UNUSED;
                }
            }
        }

        // Update the boxes
        for (Box box : boxData) {
            // Run the physics
            box.getBody().integrate(duration);
            box.calculateInternals();
        }
    }

    /** Dispatches a round. */
    public void fire() {
        // Find the first available round.
        AmmoRound shot = null;
        for (AmmoRound a : ammo) {
            if (a.type == ShotType.UNUSED) {
                shot = a;
                break;
            }
        }

        // If we didn't find a round, then exit - we can't fire.
        if (shot == null) {
            return;
        }
        // Set the shot
        shot.setState(currentShotType);

    }

    /** Creates a new demo object. */
    public BigBallisticDemo() {
        this.currentShotType = ShotType.LASER;
        setPauseSimulation(false);
        reset();
    }

    /** Returns the window title for the demo. */
    @Override
    public String getTitle() {
        return "Mega Balls";
    }

    /** Sets up the rendering. */
    @Override
    public void init(GLAutoDrawable drawable) {
        super.init(drawable);

        float lightAmbient[] = { 0.8f, 0.8f, 0.8f, 1.0f };
        float lightDiffuse[] = { 0.9f, 0.95f, 1.0f, 1.0f };

        getGl().glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, lightAmbient, 0);
        getGl().glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, lightDiffuse, 0);

        getGl().glEnable(GL.GL_LIGHT0);

        animator = new Animator(drawable);
        animator.start();
    }

    /** Display world. */
    @Override
    public void display(GLAutoDrawable arg0) {
        update();
        // Clear the viewport and set the camera direction
        getGl().glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        getGl().glLoadIdentity();
        getGlu().gluLookAt(-25.0, 8.0, 5.0, 0.0, 5.0, 22.0, 0.0, 1.0, 0.0);

        // Draw a sphere at the firing point, and add a shadow projected
        // onto the ground plane.
        getGl().glColor3f(0.0f, 0.0f, 0.0f);
        getGl().glPushMatrix();
        getGl().glTranslatef(0.0f, 1.5f, 0.0f);
        getGlut().glutSolidSphere(0.1f, 5, 5);
        getGl().glTranslatef(0.0f, -1.5f, 0.0f);
        getGl().glColor3f(0.75f, 0.75f, 0.75f);
        getGl().glScalef(1.0f, 0.1f, 1.0f);
        getGlut().glutSolidSphere(0.1f, 5, 5);
        getGl().glPopMatrix();

        // Draw some scale lines
        getGl().glColor3f(0.75f, 0.75f, 0.75f);
        getGl().glBegin(GL.GL_LINES);
        for (int i = 0; i < 200; i += 10) {
            getGl().glVertex3f(-5.0f, 0.0f, i);
            getGl().glVertex3f(5.0f, 0.0f, i);
        }
        getGl().glEnd();

        // Render each particle in turn
        getGl().glColor3f(1, 0, 0);
        for (AmmoRound shot : ammo) {
            if (shot.type != ShotType.UNUSED) {
                shot.render(getGl(), getGlut());
            }
        }

        // Render the box
        getGl().glEnable(GL.GL_DEPTH_TEST);
        getGl().glEnable(GL.GL_LIGHTING);
        getGl().glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, lightPosition, 0);
        getGl().glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE);
        getGl().glEnable(GL.GL_COLOR_MATERIAL);
        getGl().glColor3f(1, 0, 0);
        for (Box box : boxData) {
            box.render(getGl(), getGlut());
        }
        getGl().glDisable(GL.GL_COLOR_MATERIAL);
        getGl().glDisable(GL.GL_LIGHTING);
        getGl().glDisable(GL.GL_DEPTH_TEST);

    }

    /** Handle a mouse click. */
    @Override
    public void mouseClicked(java.awt.event.MouseEvent arg0) {
        System.out.println("fire");
        update();
        fire();
        glDrawable.display();
    }

    /** Handle a keypress. */
    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("keypressed");
        switch (e.getKeyChar()) {
            case '1':
                currentShotType = ShotType.PISTOL;
                break;
            case '2':
                currentShotType = ShotType.ARTILLERY;
                break;
            case '3':
                currentShotType = ShotType.FIREBALL;
                break;
            case '4':
                currentShotType = ShotType.LASER;
                break;
            case 'r':
            case 'R':
                reset();
                break;
        }
    }

    public static void main(String[] args) {
        new Show(new BigBallisticDemo()).setVisible(true);
    }
}
