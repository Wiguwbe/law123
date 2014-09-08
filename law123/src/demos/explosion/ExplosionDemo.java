package demos.explosion;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import br.law123.collide.CollisionData;
import br.law123.collide.CollisionDetector;
import br.law123.collide.CollisionPlane;
import br.law123.collide.IntersectionTests;
import br.law123.core.Matrix4;
import br.law123.core.Quaternion;
import br.law123.core.Vector3;
import br.law123.random.Random;

import com.sun.opengl.util.Animator;

import demos.RigidBodyApplication;
import demos.Show;

/**
 * The main demo class definition.
 */
class ExplosionDemo extends RigidBodyApplication {

    private Animator animator;
    private static final int OBJECTS = 1;

    // Holds a transform matrix for rendering objects
    // reflected in the floor.
    float[] floorMirror = { 1, 0, 0, 0, 0, -1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };

    boolean editMode, upMode;

    /**
     * Holds the number of boxes in the simulation.
     */
    private static final int boxes = OBJECTS;

    /** Holds the box data. */
    private Box[] boxData = new Box[boxes];

    /**
     * Holds the number of balls in the simulation.
     */
    private static int balls = OBJECTS;

    /** Holds the ball data. */
    private Ball[] ballData = new Ball[balls];

    static final float lightPosition[] = { 1, -1, 0, 0 };
    static final float lightPositionMirror[] = { 1, 1, 0, 0 };

    @Override
    public void display(GLAutoDrawable arg0) {
        update();

        // Update the transform matrices of each box in turn
        for (int i = 0; i < boxData.length; i++) {
            boxData[i].calculateInternals();
            boxData[i].setOverlapping(false);
        }

        // Update the transform matrices of each ball in turn
        for (int i = 0; i < ballData.length; i++) {
            // Run the physics
            ballData[i].calculateInternals();
        }

        // Clear the viewport and set the camera direction
        super.display(arg0);

        // Render each element in turn as a shadow
        getGl().glEnable(GL.GL_DEPTH_TEST);
        getGl().glEnable(GL.GL_LIGHTING);
        getGl().glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, lightPosition, 0);
        getGl().glPushMatrix();
        getGl().glMultMatrixf(floorMirror, 0);
        getGl().glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE);
        getGl().glEnable(GL.GL_COLOR_MATERIAL);
        for (Box box : boxData) {
            box.render(getGl(), getGlut());
        }
        for (Ball ball : ballData) {
            ball.render(getGl(), getGlut());
        }
        getGl().glPopMatrix();
        getGl().glDisable(GL.GL_LIGHTING);
        getGl().glDisable(GL.GL_COLOR_MATERIAL);

        // Draw some scale circles
        getGl().glColor3f(0.75f, 0.75f, 0.75f);
        for (int i = 1; i < 20; i++) {
            getGl().glBegin(GL.GL_LINE_LOOP);
            for (int j = 0; j < 32; j++) {
                float theta = 3.1415926f * j / 16.0f;
                getGl().glVertex3d(i * Math.cos(theta), 0.0f, i * Math.sin(theta));
            }
            getGl().glEnd();
        }
        getGl().glBegin(GL.GL_LINES);
        getGl().glVertex3f(-20, 0, 0);
        getGl().glVertex3f(20, 0, 0);
        getGl().glVertex3f(0, 0, -20);
        getGl().glVertex3f(0, 0, 20);
        getGl().glEnd();

        // Render each shadow in turn
        getGl().glEnable(GL.GL_BLEND);
        getGl().glColor4f(0, 0, 0, 0.1f);
        getGl().glDisable(GL.GL_DEPTH_TEST);
        getGl().glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        for (Box box : boxData) {
            box.renderShadow(getGl(), getGlut());
        }
        for (Ball ball : ballData) {
            ball.renderShadow(getGl(), getGlut());
        }
        getGl().glDisable(GL.GL_BLEND);

        // Render the boxes themselves
        getGl().glEnable(GL.GL_DEPTH_TEST);
        getGl().glEnable(GL.GL_LIGHTING);
        getGl().glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, lightPositionMirror, 0);
        getGl().glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE);
        getGl().glEnable(GL.GL_COLOR_MATERIAL);
        for (Box box : boxData) {
            box.render(getGl(), getGlut());
        }
        for (Ball ball : ballData) {
            ball.render(getGl(), getGlut());
        }
        getGl().glDisable(GL.GL_COLOR_MATERIAL);
        getGl().glDisable(GL.GL_LIGHTING);
        getGl().glDisable(GL.GL_DEPTH_TEST);

        // Finish the frame, rendering any additional information
        drawDebug();
    }

    /** Detonates the explosion. */
    void fire() {
        Vector3 pos = ballData[0].getBody().getPosition();
        pos.normalise();

        ballData[0].getBody().addForce(pos.mult(-1000.0f));
    }

    /** Resets the position of all the boxes and primes the explosion. */
    @Override
    protected void reset() {
        int k = 0;
        boxData[k++].setState(new Vector3(0, 3, 0), new Quaternion(), new Vector3(4, 1, 1), new Vector3(0, 1, 0));

        if (boxes > 1) {
            boxData[k++].setState(new Vector3(0, 4.75, 2), new Quaternion(1.0, 0.1, 0.05, 0.01), new Vector3(1, 1, 4), new Vector3(0, 1, 0));
        }

        // Create the random objects
        Random random = new Random();
        for (int i = k; i < boxes; i++) {
            boxData[i].random(random);
        }

        for (Ball ball : ballData) {
            ball.random(random);
        }

        getcData().getContacts().clear();
    }

    /** Processes the contact generation code. */
    @Override
    protected void generateContacts() {
        // Note that this method makes a lot of use of early returns to avoid
        // processing lots of potential contacts that it hasn't got room to
        // store.

        // Create the ground plane data
        CollisionPlane plane = new CollisionPlane();
        plane.setDirection(new Vector3(0, 1, 0));
        plane.setOffset(0);

        // Perform exhaustive collision detection
        Matrix4 transform = new Matrix4();
        Matrix4 otherTransform = new Matrix4();
        Vector3 position = new Vector3();
        Vector3 otherPosition = new Vector3();

        int iBox = 1;
        int iBall = 1;
        for (Box box : boxData) {
            // Check for collisions with the ground plane
            if (!getcData().hasMoreContacts()) {
                return;
            }
            CollisionDetector.boxAndHalfSpace(box, plane, getcData());

            // Check for collisions with each other box
            for (int i = iBox++; i < boxes; i++) {
                if (!getcData().hasMoreContacts()) return;
                CollisionDetector.boxAndBox(box, boxData[i], getcData());

                if (IntersectionTests.boxAndBox(box, boxData[i])) {
                    box.setOverlapping(true);
                    boxData[i].setOverlapping(true);
                }
            }

            // Check for collisions with each ball
            for (int i = iBall++; i < balls; i++) {
                if (!getcData().hasMoreContacts()) {
                    return;
                }
                CollisionDetector.boxAndSphere(box, ballData[i], getcData());
            }
        }

        iBall = 1;
        for (Ball ball : ballData) {
            // Check for collisions with the ground plane
            if (!getcData().hasMoreContacts()) {
                return;
            }
            CollisionDetector.sphereAndHalfSpace(ball, plane, getcData());

            for (int i = iBall++; i < balls; i++) {
                // Check for collisions with the ground plane
                if (!getcData().hasMoreContacts()) {
                    return;
                }
                CollisionDetector.sphereAndSphere(ball, ballData[i], getcData());
            }
        }
    }

    /** Processes the objects in the simulation forward in time. */
    @Override
    protected void updateObjects(double duration) {
        // Update the physics of each box in turn
        for (Box box : boxData) {
            // Run the physics
            box.getBody().integrate(duration);
            box.calculateInternals();
            box.setOverlapping(false);
        }

        // Update the physics of each ball in turn
        for (Ball ball : ballData) {
            // Run the physics
            ball.getBody().integrate(duration);
            ball.calculateInternals();
        }
    }

    /** Creates a new demo object. */
    ExplosionDemo() {
        // Set up the collision data structure
        super(new CollisionData(0.9, 0.6, 0.1, maxContacts));
        editMode = false;
        upMode = false;

        for (int i = 0; i < boxData.length; i++) {
            boxData[i] = new Box();
        }

        for (int i = 0; i < ballData.length; i++) {
            ballData[i] = new Ball();
        }
        // Reset the position of the boxes
        reset();
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

    /** Returns the window title for the demo. */
    @Override
    protected String getTitle() {
        return "kabuuuum";
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (editMode) {
            boxData[0].getBody().setPosition(boxData[0].getBody().getPosition().sum(new Vector3((e.getX() - last_x) * 0.125f, 0, (e.getY() - last_y) * 0.125f)));
            boxData[0].getBody().calculateDerivedData();
        } else if (upMode) {
            boxData[0].getBody().setPosition(boxData[0].getBody().getPosition().sum(new Vector3(0, (e.getY() - last_y) * 0.125f, 0)));
            boxData[0].getBody().calculateDerivedData();
        } else {
            super.mouseClicked(e);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'e':
            case 'E':
                editMode = !editMode;
                upMode = false;
                return;

            case 't':
            case 'T':
                upMode = !upMode;
                editMode = false;
                return;

            case 'w':
            case 'W':
                for (Box box : boxData)
                    box.getBody().setAwake();
                for (Ball ball : ballData)
                    ball.getBody().setAwake();
                return;
        }

        super.keyPressed(e);
    }

    public static void main(String[] args) {
        new Show(new ExplosionDemo()).setVisible(true);
    }

}
