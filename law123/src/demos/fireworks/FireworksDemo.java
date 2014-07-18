package demos.fireworks;

import java.awt.event.KeyEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import br.law123.core.Vector3;

import com.sun.opengl.util.Animator;

import demos.Application;
import demos.Show;
import demos.TimingData;

/**
 * The main demo class definition.
 */
class FireworksDemo extends Application {

    private Animator animator;

    /**
     * Holds the maximum number of fireworks that can be in use.
     */
    private static final int maxFireworks = 1024;

    /** Holds the firework data. */
    private Firework[] fireworks = new Firework[maxFireworks];

    /** Holds the index of the next firework slot to use. */
    private int nextFirework;

    /** And the number of rules. */
    private static final int ruleCount = 9;

    /** Holds the set of rules. */
    FireworkRule[] rules = new FireworkRule[ruleCount];

    /** Dispatches a firework from the origin. */
    void create(int type, Firework parent) {
        // Get the rule needed to create this firework
        FireworkRule rule = rules[type - 1];

        // Create the firework
        rule.create(fireworks[nextFirework], parent);

        // Increment the index for the next firework
        nextFirework = (nextFirework + 1) % maxFireworks;
    }

    /** Dispatches the given number of fireworks from the given parent. */
    void create(int type, int number, Firework parent) {
        for (int i = 0; i < number; i++) {
            create(type, parent);
        }
    }

    /** Creates the rules. */
    void initFireworkRules() {
        // Go through the firework types and create their rules.

        for (int i = 0; i < rules.length; i++) {
            rules[i] = new FireworkRule();
        }

        rules[0].init(2);
        rules[0].setParameters(1, // type
                               0.5f, 1.4f, // age range
                               new Vector3(-5, 25, -5), // min velocity
                               new Vector3(5, 28, 5), // max velocity
                               0.1f // damping
        );
        rules[0].getPayloads()[0].set(3, 5);
        rules[0].getPayloads()[1].set(5, 5);

        rules[1].init(1);
        rules[1].setParameters(2, // type
                               0.5f, 1.0f, // age range
                               new Vector3(-5, 10, -5), // min velocity
                               new Vector3(5, 20, 5), // max velocity
                               0.8f // damping
        );
        rules[1].getPayloads()[0].set(4, 2);

        rules[2].init(0);
        rules[2].setParameters(3, // type
                               0.5f, 1.5f, // age range
                               new Vector3(-5, -5, -5), // min velocity
                               new Vector3(5, 5, 5), // max velocity
                               0.1f // damping
        );

        rules[3].init(0);
        rules[3].setParameters(4, // type
                               0.25f, 0.5f, // age range
                               new Vector3(-20, 5, -5), // min velocity
                               new Vector3(20, 5, 5), // max velocity
                               0.2f // damping
        );

        rules[4].init(1);
        rules[4].setParameters(5, // type
                               0.5f, 1.0f, // age range
                               new Vector3(-20, 2, -5), // min velocity
                               new Vector3(20, 18, 5), // max velocity
                               0.01f // damping
        );
        rules[4].getPayloads()[0].set(3, 5);

        rules[5].init(0);
        rules[5].setParameters(6, // type
                               3, 5, // age range
                               new Vector3(-5, 5, -5), // min velocity
                               new Vector3(5, 10, 5), // max velocity
                               0.95f // damping
        );

        rules[6].init(1);
        rules[6].setParameters(7, // type
                               4, 5, // age range
                               new Vector3(-5, 50, -5), // min velocity
                               new Vector3(5, 60, 5), // max velocity
                               0.01f // damping
        );
        rules[6].getPayloads()[0].set(8, 10);

        rules[7].init(0);
        rules[7].setParameters(8, // type
                               0.25f, 0.5f, // age range
                               new Vector3(-1, -1, -1), // min velocity
                               new Vector3(1, 1, 1), // max velocity
                               0.01f // damping
        );

        rules[8].init(0);
        rules[8].setParameters(9, // type
                               3, 5, // age range
                               new Vector3(-15, 10, -5), // min velocity
                               new Vector3(15, 15, 5), // max velocity
                               0.95f // damping
        );
        // ... and so on for other firework types ...
    }

    /** Creates a new demo object. */
    // Method definitions
    public FireworksDemo() {
        this.nextFirework = 0;
        // Make all shots unused
        for (int i = 0; i < fireworks.length; i++) {
            fireworks[i] = new Firework();
            fireworks[i].setType(0);
        }

        // Create the firework types
        initFireworkRules();
    }

    /** Sets up the graphic rendering. */
    @Override
    public void init(GLAutoDrawable drawable) {
        super.init(drawable);
        // But override the clear color
        getGl().glClearColor(0.0f, 0.0f, 0.1f, 1.0f);

        animator = new Animator(drawable);
        animator.start();
    }

    /** Returns the window title for the demo. */
    @Override
    protected String getTitle() {
        return "pá pá pááá pum";
    }

    /** Update the particle positions. */
    @Override
    protected void update() {
        // Find the duration of the last frame in seconds
        float duration = TimingData.get().getLastFrameDuration() * 0.001f;
        if (duration <= 0.0f) return;

        for (Firework firework : fireworks) {
            // Check if we need to process this firework.
            if (firework.getType() > 0) {
                // Does it need removing?
                if (firework.update(duration)) {
                    // Find the appropriate rule
                    FireworkRule rule = rules[firework.getType() - 1];

                    // Delete the current firework (this doesn't affect its
                    // position and velocity for passing to the create function,
                    // just whether or not it is processed for rendering or
                    // physics.
                    firework.setType(0);

                    // Add the payload
                    for (int i = 0; i < rule.getPayloadCount(); i++) {
                        Payload payload = rule.getPayloads()[i];
                        create(payload.getType(), payload.getCount(), firework);
                    }
                }
            }
        }

        super.update();
    }

    private static final double size = 0.1f;

    @Override
    public void display(GLAutoDrawable arg0) {
        update();

        // Clear the viewport and set the camera direction
        getGl().glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        getGl().glLoadIdentity();
        getGlu().gluLookAt(0.0, 4.0, 10.0, 0.0, 4.0, 0.0, 0.0, 1.0, 0.0);

        // Render each firework in turn
        getGl().glBegin(GL.GL_QUADS);
        for (Firework firework : fireworks) {
            // Check if we need to process this firework.
            if (firework.getType() > 0) {
                switch (firework.getType()) {
                    case 1:
                        getGl().glColor3f(1, 0, 0);
                        break;
                    case 2:
                        getGl().glColor3f(1, 0.5f, 0);
                        break;
                    case 3:
                        getGl().glColor3f(1, 1, 0);
                        break;
                    case 4:
                        getGl().glColor3f(0, 1, 0);
                        break;
                    case 5:
                        getGl().glColor3f(0, 1, 1);
                        break;
                    case 6:
                        getGl().glColor3f(0.4f, 0.4f, 1);
                        break;
                    case 7:
                        getGl().glColor3f(1, 0, 1);
                        break;
                    case 8:
                        getGl().glColor3f(1, 1, 1);
                        break;
                    case 9:
                        getGl().glColor3f(1, 0.5f, 0.5f);
                        break;
                }
                ;

                Vector3 pos = firework.getPosition();
                getGl().glVertex3d(pos.getX() - size, pos.getY() - size, pos.getZ());
                getGl().glVertex3d(pos.getX() + size, pos.getY() - size, pos.getZ());
                getGl().glVertex3d(pos.getX() + size, pos.getY() + size, pos.getZ());
                getGl().glVertex3d(pos.getX() - size, pos.getY() + size, pos.getZ());

                // Render the firework's reflection
                getGl().glVertex3d(pos.getX() - size, -pos.getY() - size, pos.getZ());
                getGl().glVertex3d(pos.getX() + size, -pos.getY() - size, pos.getZ());
                getGl().glVertex3d(pos.getX() + size, -pos.getY() + size, pos.getZ());
                getGl().glVertex3d(pos.getX() - size, -pos.getY() + size, pos.getZ());
            }
        }
        getGl().glEnd();
    }

    /** Handle a keypress. */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyChar()) {
            case '1':
                create(1, 1, null);
                break;
            case '2':
                create(2, 1, null);
                break;
            case '3':
                create(3, 1, null);
                break;
            case '4':
                create(4, 1, null);
                break;
            case '5':
                create(5, 1, null);
                break;
            case '6':
                create(6, 1, null);
                break;
            case '7':
                create(7, 1, null);
                break;
            case '8':
                create(8, 1, null);
                break;
            case '9':
                create(9, 1, null);
                break;
        }
    }

    public static void main(String[] args) {
        new Show(new FireworksDemo()).setVisible(true);
    }

}
