package demos;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.media.opengl.DebugGL;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.GLUT;

/**
 * An application is the base class for all demonstration progams.
 * GLUT is a c-style API, which calls bare functions. This makes
 * it more difficult to provide default services for all demos and
 * only override them when needed.
 * 
 * To solve this, the GLUT API is translated into calls on a
 * generic application object. Each demonstration will create a
 * concrete subclass of Application, providing the behaviours it
 * needs. The common code for all demos manages dispatch of
 * requests to the appropriate application object.
 * 
 * To provide a correct application object of the right type without
 * the core code needing to know which subclass is being used, each
 * demonstration will supply a getApplication function which creates
 * (with new) and returns a pointer to a new Application instance.
 * 
 * Even though subclasses will have to implement most of the methods
 * in this class, I have not made them pure virtual. This saves the
 * annoying need to implement an empty function that isn't needed.
 */
public class Application implements GLEventListener, MouseListener, MouseMotionListener, KeyListener {

    protected GL gl;
    protected GLU glu;
    protected GLUT glut;
    protected GLAutoDrawable glDrawable;
    private GLContext glContext;

    /**
     * Holds the height of the application window.
     */
    private int height;

    /**
     * Holds the current width of the application window.
     */
    private int width;

    public GL getGl() {
        return gl;
    }

    public GLU getGlu() {
        return glu;
    }

    public GLUT getGlut() {
        return glut;
    }

    /**
     * Gets the title of the demo for the title bar of the window.
     * 
     * The default implementation returns a generic title.
     */
    protected String getTitle() {
        return "Demo";
    }

    /**
     * Sets up the graphics, and allows the application to acquire
     * graphical resources. Guaranteed to be called after OpenGL is
     * set up.
     * 
     * The default implementation sets up a basic view, and calls
     * setView to set up the camera projection.
     */
    @Override
    public void init(GLAutoDrawable drawable) {
        glContext = drawable.getContext();
        glDrawable = drawable;
        gl = drawable.getGL();
        glu = new GLU();
        glut = new GLUT();
        glDrawable.setGL(new DebugGL(gl));

        gl.glClearColor(0.9f, 0.95f, 1.0f, 1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glShadeModel(GL.GL_SMOOTH);

        setView();

        Timer t = new Timer();
        t.scheduleAtFixedRate(updateFPS, 1000, 1000);
    }

    /**
     * Called to set the projection characteristics of the camera.
     * 
     * The default implementation uses a 60 degree field of view camera
     * with a range from 1-500 units.
     */
    protected void setView() {
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(60.0, (double) width / (double) height, 1.0, 500.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
    }

    /**
     * Called just before the application is destroyed. Clear up can
     * be performed here or in the application destructor.
     * 
     * The default implementation does nothing.
     */
    protected void deinit() {
        //
    }

    /**
     * Called each frame to update the current state of the scene.
     * 
     * The default implementation requests that the display be refreshed.
     * It should probably be called from any subclass update as the last
     * command.
     */
    protected void update() {
        //glut.glutPostRedisplay();
    }

    /**
     * Called when a keypress is detected.
     * 
     * The default implementation does nothing.
     * 
     * @param key The ascii code of the key that has been pressed.
     */
    @Override
    public void keyPressed(KeyEvent arg0) {
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
    }

    // These are helper functions that can be used by an application 
    // to render things.

    /**
     * Renders the given text to the given x,y location (in screen space)
     * on the window. This is used to pass status information to the
     * application.
     */
    protected void renderText(float x, float y, String text, Integer font) {
        gl.glDisable(GL.GL_DEPTH_TEST);

        // Temporarily set up the view in orthographic projection.
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glOrtho(0.0, width, 0.0, height, -1.0, 1.0);

        // Move to modelview mode.
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();

        // Ensure we have a font
        if (font == null) {
            font = GLUT.BITMAP_HELVETICA_10;
        }

        // Loop through characters displaying them.
        int len = text.length();

        gl.glRasterPos2f(x, y);
        for (int i = 0; i < len; i++) {

            // If we meet a newline, then move down by the line-height
            // TODO: Make the line-height a function of the font
            if (text.charAt(i) == '\n') {
                y -= 12.0f;
                gl.glRasterPos2f(x, y);
            }
            glut.glutBitmapCharacter(font, text.charAt(i));
        }

        // Pop the matrices to return to how we were before.
        gl.glPopMatrix();
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL.GL_MODELVIEW);

        gl.glEnable(GL.GL_DEPTH_TEST);
    }

    @Override
    public void mouseDragged(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseMoved(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    private int totalFrameCount = 0;
    TimerTask updateFPS = new TimerTask() {

        public void run() {
            // display current totalFrameCount - previous,
            // OR
            // display current totalFrameCount, then set
            System.out.println("FPS:" + totalFrameCount);
            totalFrameCount = 0;
        }
    };

    /**
     * Called each frame to display the current scene. The common code
     * will automatically flush the graphics pipe and swap the render
     * buffers after calling this so glFlush doesn't need to be called.
     * 
     * The default
     * implementation draws a simple diagonal line across the surface
     * (as a sanity check to make sure GL is working).
     */
    @Override
    public void display(GLAutoDrawable arg0) {
        totalFrameCount++;
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        gl.glBegin(GL.GL_LINES);
        gl.glVertex2i(1, 1);
        gl.glVertex2i(639, 319);
        gl.glEnd();
    }

    @Override
    public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
        System.out.println("dahsdsahuda");
    }

    /**
     * Notifies the application that the window has changed size.
     * The new size is given.
     * 
     * The default implementation sets the internal height and width
     * parameters and changes the gl viewport. These are steps you'll
     * almost always need, so its worth calling the base class version
     * of this method even if you override it in a demo class.
     */
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        // Avoid the divide by zero.
        if (height <= 0) height = 1;

        // Set the internal variables and update the view
        this.width = width;
        this.height = height;
        gl.glViewport(0, 0, width, height);
        setView();
    }

}
