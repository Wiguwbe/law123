package demos.flightsim;

import java.awt.event.KeyEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import br.law123.core.Core;
import br.law123.core.Matrix3;
import br.law123.core.Matrix4;
import br.law123.core.Vector3;
import br.law123.forcegenerator.ForceRegistry;
import br.law123.forcegenerator.rigidbody.Aero;
import br.law123.forcegenerator.rigidbody.AeroControl;
import br.law123.forcegenerator.rigidbody.AngledAero;
import br.law123.rigidbody.RigidBody;

import com.sun.opengl.util.Animator;

import demos.Application;
import demos.Show;
import demos.TimingData;

/**
 * The main demo class definition.
 */
public class FlightSimDemo extends Application {

    private Animator animator;


    private AeroControl left_wing;
    private AeroControl right_wing;
    private AeroControl rudder;
    private Aero tail;
    private RigidBody aircraft = new RigidBody();
    private ForceRegistry registry = new ForceRegistry();

    private Vector3 windspeed = new Vector3();

    float left_wing_control;
    float right_wing_control;
    float rudder_control;

    void resetPlane() {
        aircraft.setPosition(0, 0, 0);
        aircraft.setOrientation(1, 0, 0, 0);

        aircraft.setVelocity(0, 0, 0);
        aircraft.setRotation(0, 0, 0);
    }

    /** Creates a new demo object. */
    public FlightSimDemo() {
        super();

        right_wing = new AeroControl(new Matrix3(0, 0, 0, -1, -0.5f, 0, 0, 0, 0), new Matrix3(0, 0, 0, -0.995f, -0.5f, 0, 0, 0, 0), new Matrix3(0, 0, 0, -1.005f, -0.5f, 0, 0, 0, 0), new Vector3(-1.0f, 0.0f, 2.0f), windspeed);

        left_wing = new AeroControl(new Matrix3(0, 0, 0, -1, -0.5f, 0, 0, 0, 0), new Matrix3(0, 0, 0, -0.995f, -0.5f, 0, 0, 0, 0), new Matrix3(0, 0, 0, -1.005f, -0.5f, 0, 0, 0, 0), new Vector3(-1.0f, 0.0f, -2.0f), windspeed);

        rudder = new AeroControl(new Matrix3(0, 0, 0, 0, 0, 0, 0, 0, 0), new Matrix3(0, 0, 0, 0, 0, 0, 0.01f, 0, 0), new Matrix3(0, 0, 0, 0, 0, 0, -0.01f, 0, 0), new Vector3(2.0f, 0.5f, 0), windspeed);

        tail = new AngledAero(new Matrix3(0, 0, 0, -1, -0.5f, 0, 0, 0, -0.1f), new Vector3(2.0f, 0, 0), windspeed);

        left_wing_control = 0;
        right_wing_control = 0;
        rudder_control = 0;

        windspeed = new Vector3(0, 0, 0);
        // Set up the aircraft rigid body.
        resetPlane();

        aircraft.setMass(2.5f);
        Matrix3 it = new Matrix3();
        it.setBlockInertiaTensor(new Vector3(2, 1, 1), 1);
        aircraft.setInertiaTensor(it);

        aircraft.setDamping(0.8f, 0.8f);

        aircraft.setAcceleration(Core.GRAVITY);
        aircraft.calculateDerivedData();

        aircraft.setAwake();
        aircraft.setCanSleep(false);

        registry.add(aircraft, left_wing);
        registry.add(aircraft, right_wing);
        registry.add(aircraft, rudder);
        registry.add(aircraft, tail);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        super.init(drawable);

        animator = new Animator(drawable);
        animator.start();
    }

    private void drawAircraft() {
        // Fuselage
        gl.glPushMatrix();
        gl.glTranslatef(-0.5f, 0, 0);
        gl.glScalef(2.0f, 0.8f, 1.0f);
        glut.glutSolidCube(1.0f);
        gl.glPopMatrix();

        // Rear Fuselage
        gl.glPushMatrix();
        gl.glTranslatef(1.0f, 0.15f, 0);
        gl.glScalef(2.75f, 0.5f, 0.5f);
        glut.glutSolidCube(1.0f);
        gl.glPopMatrix();

        // Wings
        gl.glPushMatrix();
        gl.glTranslatef(0, 0.3f, 0);
        gl.glScalef(0.8f, 0.1f, 6.0f);
        glut.glutSolidCube(1.0f);
        gl.glPopMatrix();

        // Rudder
        gl.glPushMatrix();
        gl.glTranslatef(2.0f, 0.775f, 0);
        gl.glScalef(0.75f, 1.15f, 0.1f);
        glut.glutSolidCube(1.0f);
        gl.glPopMatrix();

        // Tail-plane
        gl.glPushMatrix();
        gl.glTranslatef(1.9f, 0, 0);
        gl.glScalef(0.85f, 0.1f, 2.0f);
        glut.glutSolidCube(1.0f);
        gl.glPopMatrix();
    }

    /** Returns the window title for the demo. */
    @Override
    protected String getTitle() {
        return "Voa supemain, voa";
    }

    /** Display the particles. */
    @Override
    public void display(GLAutoDrawable arg0) {
        update();

        // Clear the view port and set the camera direction
        getGl().glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        getGl().glLoadIdentity();

        Vector3 pos = aircraft.getPosition();
        Vector3 offset = new Vector3(4.0f + aircraft.getVelocity().magnitude(), 0, 0);
        offset = aircraft.getTransform().transformDirection(offset);
        getGlu().gluLookAt(pos.getX() + offset.getX(), pos.getY() + 5.0f, pos.getZ() + offset.getZ(), pos.getX(), pos.getY(), pos.getZ(), 0.0, 1.0, 0.0);

        getGl().glColor3f(0.6f, 0.6f, 0.6f);
        int bx = (int) pos.getX();
        int bz = (int) pos.getZ();
        getGl().glBegin(GL.GL_QUADS);
        for (int x = -20; x <= 20; x++)
            for (int z = -20; z <= 20; z++) {
                getGl().glVertex3f(bx + x - 0.1f, 0, bz + z - 0.1f);
                getGl().glVertex3f(bx + x - 0.1f, 0, bz + z + 0.1f);
                getGl().glVertex3f(bx + x + 0.1f, 0, bz + z + 0.1f);
                getGl().glVertex3f(bx + x + 0.1f, 0, bz + z - 0.1f);
            }
        getGl().glEnd();

        // Set the transform matrix for the aircraft
        Matrix4 transform = aircraft.getTransform();
        float[] gl_transform = new float[16];
        transform.fillGLArray(gl_transform);
        getGl().glPushMatrix();
        getGl().glMultMatrixf(gl_transform, 0);

        // Draw the aircraft
        getGl().glColor3f(0, 0, 0);
        drawAircraft();
        getGl().glPopMatrix();

        getGl().glColor3f(0.8f, 0.8f, 0.8f);
        getGl().glPushMatrix();
        getGl().glTranslatef(0, (float) (-1.0f - pos.getY()), 0);
        getGl().glScalef(1.0f, 0.001f, 1.0f);
        getGl().glMultMatrixf(gl_transform, 0);
        drawAircraft();
        getGl().glPopMatrix();

        System.out.println(String.format("Altitude: %.1f | Speed %.1f", aircraft.getPosition().getY(), aircraft.getVelocity().magnitude()));
        getGl().glColor3f(0, 0, 0);

        System.out.println(String.format("Left Wing: %.1f | Right Wing: %.1f | Rudder %.1f", left_wing_control, right_wing_control, rudder_control));
    }

    /** Update the particle positions. */
    @Override
    protected void update() {
        // Find the duration of the last frame in seconds
        float duration = TimingData.get().getLastFrameDuration() * 0.001f;
        if (duration <= 0.0f) return;

        // Start with no forces or acceleration.
        aircraft.clearAccumulators();

        // Add the propeller force
        Vector3 propulsion = new Vector3(-10.0f, 0, 0);
        propulsion = aircraft.getTransform().transformDirection(propulsion);
        aircraft.addForce(propulsion);

        // Add the forces acting on the aircraft.
        registry.updateForces(duration);

        // Update the aircraft's physics.
        aircraft.integrate(duration);

        // Do a very basic collision detection and response with the ground.
        Vector3 pos = aircraft.getPosition();
        if (pos.getY() < 0.0f) {
            pos.setY(0.0f);
            aircraft.setPosition(pos);

            if (aircraft.getVelocity().getY() < -10.0f) {
                resetPlane();
            }
        }

        super.update();
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
        switch (arg0.getKeyChar()) {
            case 'q':
            case 'Q':
                rudder_control += 0.1f;
                break;

            case 'e':
            case 'E':
                rudder_control -= 0.1f;
                break;

            case 'w':
            case 'W':
                left_wing_control -= 0.1f;
                right_wing_control -= 0.1f;
                break;

            case 's':
            case 'S':
                left_wing_control += 0.1f;
                right_wing_control += 0.1f;
                break;

            case 'd':
            case 'D':
                left_wing_control -= 0.1f;
                right_wing_control += 0.1f;
                break;

            case 'a':
            case 'A':
                left_wing_control += 0.1f;
                right_wing_control -= 0.1f;
                break;

            case 'x':
            case 'X':
                left_wing_control = 0.0f;
                right_wing_control = 0.0f;
                rudder_control = 0.0f;
                break;

            case 'r':
            case 'R':
                resetPlane();
                break;

            default:
                super.keyPressed(arg0);
        }

        // Make sure the controls are in range
        if (left_wing_control < -1.0f) left_wing_control = -1.0f;
        else if (left_wing_control > 1.0f) left_wing_control = 1.0f;
        if (right_wing_control < -1.0f) right_wing_control = -1.0f;
        else if (right_wing_control > 1.0f) right_wing_control = 1.0f;
        if (rudder_control < -1.0f) rudder_control = -1.0f;
        else if (rudder_control > 1.0f) rudder_control = 1.0f;

        // Update the control surfaces
        left_wing.setControl(left_wing_control);
        right_wing.setControl(right_wing_control);
        rudder.setControl(rudder_control);
    }

    public static void main(String[] args) {
        new Show(new FlightSimDemo()).setVisible(true);
    }

}
