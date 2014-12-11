package demos;

import java.awt.event.KeyEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import br.law123.collide.CollisionData;
import br.law123.core.Vector3;
import br.law123.rigidbody.contact.ContactResolver;

/**
 * This application adds additional functionality used in many of the
 * demos. This includes the ability to track contacts (for rigid bodies)
 * and move the camera around.
 */
public abstract class RigidBodyApplication extends Application {

    /** Holds the maximum number of contacts. */
    protected static final int maxContacts = 256;

    /** Holds the collision data structure for collision detection. */
    private final CollisionData cData;

    /** Holds the contact resolver. */
    private ContactResolver resolver;

    /** Holds the camera angle. */
    private float theta;

    /** Holds the camera elevation. */
    private float phi;

    /** Holds the position of the mouse at the last frame of a drag. */
    protected int last_x, last_y;

    /** True if the contacts should be rendered. */
    private boolean renderDebugInfo;

    /** True if the simulation is paused. */
    private boolean pauseSimulation;

    /** Pauses the simulation after the next frame automatically */
    private boolean autoPauseSimulation;

    public CollisionData getcData() {
        return cData;
    }

    public void setPauseSimulation(boolean pauseSimulation) {
        this.pauseSimulation = pauseSimulation;
    }

    /**
     * Finishes drawing the frame, adding debugging information
     * as needed.
     */
    protected void drawDebug() {
        if (!renderDebugInfo) return;

        // Recalculate the contacts, so they are current (in case we're
        // paused, for example).
        generateContacts();

        // Render the contacts, if required
        gl.glBegin(GL.GL_LINES);
        for (int i = 0; i < cData.collectContacts().size(); i++) {
            // Interbody contacts are in green, floor contacts are red.
            if (cData.collectContacts().get(i).getBody()[1] != null) {
                gl.glColor3f(0, 1, 0);
            } else {
                gl.glColor3f(1, 0, 0);
            }

            Vector3 vec = cData.collectContacts().get(i).getContactPoint();
            gl.glVertex3d(vec.getX(), vec.getY(), vec.getZ());

            vec.sumToMe(cData.collectContacts().get(i).getContactNormal());
            gl.glVertex3d(vec.getX(), vec.getY(), vec.getZ());
        }

        gl.glEnd();
    }

    /**
     * Creates a new application object.
     */
    public RigidBodyApplication(CollisionData data) {
        this.theta = 0.0f;
        this.phi = 15.0f;
        this.resolver = new ContactResolver(maxContacts * 8);

        this.renderDebugInfo = false;
        this.pauseSimulation = true;
        this.autoPauseSimulation = false;
        this.cData = data;
    }

    /** Display the application. */

    @Override
    public void display(GLAutoDrawable arg0) {
        super.display(arg0);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        glu.gluLookAt(18.0f, 0, 0, 0, 0, 0, 0, 1.0f, 0);
        gl.glRotatef(-phi, 0, 0, 1);
        gl.glRotatef(theta, 0, 1, 0);
        gl.glTranslatef(0, -5.0f, 0);
    }

    /** Update the objects. */
    @Override
    public void update() {
        // Find the duration of the last frame in seconds
        float duration = TimingData.get().getLastFrameDuration() * 0.001f;
        if (duration <= 0.0f) return;
        else if (duration > 0.05f) duration = 0.05f;

        // Exit immediately if we aren't running the simulation
        if (pauseSimulation) {
            super.update();
            return;
        } else if (autoPauseSimulation) {
            pauseSimulation = true;
            autoPauseSimulation = false;
        }

        // Update the objects
        updateObjects(duration);

        // Perform the contact generation
        generateContacts();

        // Resolve detected contacts
        resolver.resolveContacts(cData.collectContacts(), duration);

        super.update();
    }

    /** Handle a mouse click. */
    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {
        // Set the position
        last_x = e.getX();
        last_y = e.getY();
    }

    /** Handle a mouse drag */
    public void mouseDragged(java.awt.event.MouseEvent e) {
        // Update the camera
        theta += (e.getX() - last_x) * 0.25f;
        phi += (e.getY() - last_y) * 0.25f;

        // Keep it in bounds
        if (phi < -20.0f) phi = -20.0f;
        else if (phi > 80.0f) phi = 80.0f;

        // Remember the position
        last_x = e.getX();
        last_y = e.getY();
    }

    /** Handles a key press. */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'R':
            case 'r':
                // Reset the simulation
                reset();
                return;

            case 'C':
            case 'c':
                // Toggle rendering of contacts
                renderDebugInfo = !renderDebugInfo;
                return;

            case 'P':
            case 'p':
                // Toggle running the simulation
                pauseSimulation = !pauseSimulation;
                return;

            case ' ':
                // Advance one frame
                autoPauseSimulation = true;
                pauseSimulation = false;
        }

        super.keyPressed(e);
    }

    /** Processes the contact generation code. */
    protected abstract void generateContacts();

    /** Processes the objects in the simulation forward in time. */
    protected abstract void updateObjects(double duration);

    /** Resets the simulation. */
    protected abstract void reset();

}
