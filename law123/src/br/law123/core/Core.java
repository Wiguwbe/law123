package br.law123.core;

/**
 * @mainpage Cyclone Reference
 * 
 *           Cyclone is a general purpose and robust system for double-time
 *           simulation of rigid bodies. The library was designed to be used in
 *           computer games, but may be applicable to other areas of simulation
 *           or research.
 * 
 * @section docs About This Document
 * 
 *          This documentation contains detailed reference to every aspect of
 *          the Cyclone library.
 * 
 * @subsection contents Contents
 * 
 *             Use the navigation system on the left side of the page to view the
 *             documentation. The navigation tool uses JavaScript, and requires a
 *             version 4 browser or above.
 * 
 *             All the publically accessible functions and classes of Cyclone
 *             are provided in a set of header files. These, and their contents,
 *             can be browsed from the File List section.
 * 
 *             Cyclone is contained in a single namespace, cyclone. Its
 *             contents can be viewed in the Compound List section. The Class
 *             Hierarchy section provides an alternative way to navigate these
 *             classes. The Graphical Class Hierarchy provides an overview of
 *             class inheritance.
 * 
 *             The Compound List section gives an alphabetic list of all symbols
 *             in the library, including method names and functions.
 * 
 * @subsection graphs Graphs
 * 
 *             Most of the documentation contains detailed graphical
 *             representations of the file and class dependencies. These diagrams
 *             are clickable, and provide the fastest mechanism for browsing the
 *             documentation. Each diagram is followed by a link to a help file
 *             giving a legend.
 * 
 * @section use Using Cyclone
 * 
 *          To set up:
 * 
 * @li Create a set of instances of RigidBody.
 * 
 * @li Set their mass, inertia tensor, and damping.
 * 
 * @li Set their initial location, orientation, velocity and rotation.
 * 
 * @li Apply any permanent forces (such as gravity).
 * 
 *     Then each frame:
 * 
 * @li Apply any transient forces (such as springs or thrusts).
 * 
 * @li Call eulerIntegrate on each body in turn.
 * 
 * @li Fill an array of Contact instances with all contacts on all
 *     bodies.
 * 
 * @li Call ContactResolver::resolveContacts to resolve the
 *     contacts.
 * 
 * @li Call calculateInternals to update the bodies' internal
 *     properties (such as the transform matrix).
 * 
 * @li Render the bodies.
 * 
 * @section legal Legal
 * 
 *          This documentation is distributed under license. Use of this
 *          documentation implies agreement with all terms and conditions of
 *          the accompanying software and documentation license.
 */

/**
 * The cyclone namespace includes all cyclone functions and
 * classes. It is defined as a namespace to allow function and class
 * names to be simple without causing conflicts.
 */
public class Core {

    private static final Core INSTANCE = new Core();

    private Core() {
    }

    public static Core get() {
        return INSTANCE;
    }

    public final static Vector3 GRAVITY = new Vector3(0, -9.81, 0);
    public final static Vector3 HIGH_GRAVITY = new Vector3(0, -19.62, 0);
    public final static Vector3 UP = new Vector3(0, 1, 0);
    public final static Vector3 RIGHT = new Vector3(1, 0, 0);
    public final static Vector3 OUT_OF_SCREEN = new Vector3(0, 0, 1);
    public final static Vector3 X = new Vector3(0, 1, 0);
    public final static Vector3 Y = new Vector3(1, 0, 0);
    public final static Vector3 Z = new Vector3(0, 0, 1);

    /**
     * Holds the value for energy under which a body will be put to
     * sleep. This is a global value for the whole solution. By
     * default it is 0.1, which is fine for simulation when gravity is
     * about 20 units per second squared, masses are about one, and
     * other forces are around that of gravity. It may need tweaking
     * if your simulation is drastically different to this.
     */
    private double sleepEpsilon = 0.3;

    /**
     * Sets the current sleep epsilon value: the kinetic energy under
     * which a body may be put to sleep. Bodies are put to sleep if
     * they appear to have a stable kinetic energy less than this
     * value. For simulations that often have low values (such as slow
     * moving, or light objects), this may need reducing.
     * 
     * The value is global; all bodies will use it.
     * 
     * @see sleepEpsilon
     * 
     * @see getSleepEpsilon
     * 
     * @param value The sleep epsilon value to use from this point
     *            on.
     */
    public void setSleepEpsilon(double value) {
        this.sleepEpsilon = value;
    }

    /**
     * Gets the current value of the sleep epsilon parameter.
     * 
     * @see sleepEpsilon
     * 
     * @see setSleepEpsilon
     * 
     * @return The current value of the parameter.
     */
    public double getSleepEpsilon() {
        return sleepEpsilon;
    }

}
