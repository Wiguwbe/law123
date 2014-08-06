package demos;

import java.awt.BorderLayout;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Show extends JFrame implements GLEventListener {

    private static final long serialVersionUID = 1L;
    private Application app = null;

    public Show(Application app) {
        super(app.getTitle());
        this.app = app;

        //glutInit(&argc, argv);
        TimingData.init();

        // Run the application
        //app.initGraphics();
        //glutMainLoop();

        setBounds(50, 50, 800, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        GLCapabilities glCaps = new GLCapabilities();
        glCaps.setRedBits(8);
        glCaps.setBlueBits(8);
        glCaps.setGreenBits(8);
        glCaps.setAlphaBits(8);

        GLCanvas canvas = new GLCanvas(glCaps);
        add(canvas, BorderLayout.CENTER);
        canvas.addGLEventListener(this);
        canvas.addMouseListener(app);
        canvas.addMouseMotionListener(app);
        canvas.addKeyListener(app);
        canvas.requestFocus();
    }

    @Override
    public void display(GLAutoDrawable arg0) {
        TimingData.get().update();

        app.display(arg0);

        // Update the displayed content.
        app.getGl().glFlush();
        //app.getGlut().glutSwapBuffers();
    }

    @Override
    public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
        app.displayChanged(arg0, arg1, arg2);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        app.init(drawable);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        app.reshape(drawable, x, y, width, height);
    }

}
