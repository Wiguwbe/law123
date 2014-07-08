package demos.ballistic;

import java.awt.event.KeyEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import com.sun.opengl.util.Animator;

import demos.Application;
import demos.Show;
import demos.TimingData;

public class BallisticDemo extends Application {

    private static final int ammoRounds = 16;

    /** Holds the particle data. */
    private AmmoRound[] ammo = new AmmoRound[ammoRounds];

    /** Holds the current shot type. */
    private ShotType currentShotType;

    private Animator animator;

    /** Creates a new demo object. */
    BallisticDemo() {
        this.currentShotType = ShotType.LASER;
        // Make all shots unused
        for (int i = 0; i < ammo.length; i++) {
            ammo[i] = new AmmoRound();
            ammo[i].type = ShotType.UNUSED;
        }
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
        return "Ballistic";
    }

    /** Dispatches a round. */
    private void fire() {
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

        // Set the properties of the particle
        switch (currentShotType) {
            case PISTOL:
                shot.particle.setMass(2.0f); // 2.0kg
                shot.particle.setVelocity(0.0f, 0.0f, 35.0f); // 35m/s
                shot.particle.setAcceleration(0.0f, -1.0f, 0.0f);
                shot.particle.setDamping(0.99f);
                break;

            case ARTILLERY:
                shot.particle.setMass(200.0f); // 200.0kg
                shot.particle.setVelocity(0.0f, 30.0f, 40.0f); // 50m/s
                shot.particle.setAcceleration(0.0f, -20.0f, 0.0f);
                shot.particle.setDamping(0.99f);
                break;

            case FIREBALL:
                shot.particle.setMass(1.0f); // 1.0kg - mostly blast damage
                shot.particle.setVelocity(0.0f, 0.0f, 10.0f); // 5m/s
                shot.particle.setAcceleration(0.0f, 0.6f, 0.0f); // Floats up
                shot.particle.setDamping(0.9f);
                break;

            case LASER:
                // Note that this is the kind of laser bolt seen in films,
                // not a realistic laser beam!
                shot.particle.setMass(0.1f); // 0.1kg - almost no weight
                shot.particle.setVelocity(0.0f, 0.0f, 100.0f); // 100m/s
                shot.particle.setAcceleration(0.0f, 0.0f, 0.0f); // No gravity
                shot.particle.setDamping(0.99f);
                break;
        }

        // Set the data common to all particle types
        shot.particle.setPosition(0.0f, 1.5f, 0.0f);
        shot.startTime = TimingData.get().getLastFrameTimestamp();
        shot.type = currentShotType;

        // Clear the force accumulators
        shot.particle.clearAccumulator();
    }

    /** Update the particle positions. */
    @Override
    public void update() {
        // Find the duration of the last frame in seconds
        float duration = TimingData.get().getLastFrameDuration() * 0.001f;
        if (duration <= 0.0f) return;

        // Update the physics of each particle in turn
        for (AmmoRound shot : ammo) {
            if (shot.type != ShotType.UNUSED) {
                // Run the physics
                shot.particle.integrate(duration);

                // Check if the particle is now invalid
                if (shot.particle.getPosition().getY() < 0.0f || shot.startTime + 5000 < TimingData.get().getLastFrameTimestamp() || shot.particle.getPosition().getZ() > 200.0f) {
                    // We simply set the shot type to be unused, so the
                    // memory it occupies can be reused by another shot.
                    shot.type = ShotType.UNUSED;
                }
            }
        }

        super.update();
    }

    /** Display the particle positions. */
    @Override
    public void display(javax.media.opengl.GLAutoDrawable arg0) {
        update();
        System.out.println("testes");
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
        for (AmmoRound shot : ammo) {
            if (shot.type != ShotType.UNUSED) {
                System.err.println("hahahaha");
                shot.render(gl, glut);
            }
        }
        //gl.glFlush();
        // Render the description
        //getGl().glColor3f(0.0f, 0.0f, 0.0f);
        /*renderText(10.0f, 34.0f, "Click: Fire\n1-4: Select Ammo");

        // Render the name of the current shot type
        switch(currentShotType)
        {
        case PISTOL: renderText(10.0f, 10.0f, "Current Ammo: Pistol"); break;
        case ARTILLERY: renderText(10.0f, 10.0f, "Current Ammo: Artillery"); break;
        case FIREBALL: renderText(10.0f, 10.0f, "Current Ammo: Fireball"); break;
        case LASER: renderText(10.0f, 10.0f, "Current Ammo: Laser"); break;
        }*/
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
        }
    }

    public static void main(String[] args) {
        new Show(new BallisticDemo()).setVisible(true);
    }

}
