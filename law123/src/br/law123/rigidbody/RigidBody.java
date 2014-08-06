package br.law123.rigidbody;

import java.util.Arrays;

import br.law123.core.Core;
import br.law123.core.Matrix3;
import br.law123.core.Matrix4;
import br.law123.core.Quaternion;
import br.law123.core.Vector3;

/**
 * A rigid body is the basic simulation object in the physics core.
 * 
 * It has position and orientation data, along with first derivatives. It can be
 * integrated forward through time, and have forces, torques and impulses
 * (linear or angular) applied to it. The rigid body manages its state and
 * allows access through a set of methods.
 * 
 * A ridid body contains 64 words (the size of which is given by the precision:
 * sizeof(double)). It contains no virtual functions, so should take up exactly
 * 64 words in memory. Of this total 15 words are padding, distributed among the
 * Vector3 data members.
 */
public class RigidBody {

    /**
     * @name Characteristic Data and State
     * 
     *       This data holds the state of the rigid body. There are two sets of
     *       data: characteristics and state.
     * 
     *       Characteristics are properties of the rigid body independent of its
     *       current kinematic situation. This includes mass, moment of inertia
     *       and damping properties. Two identical rigid bodys will have the
     *       same values for their characteristics.
     * 
     *       State includes all the characteristics and also includes the
     *       kinematic situation of the rigid body in the current simulation. By
     *       setting the whole state data, a rigid body's exact game state can
     *       be replicated. Note that state does not include any forces applied
     *       to the body. Two identical rigid bodies in the same simulation will
     *       not share the same state values.
     * 
     *       The state values make up the smallest set of independent data for
     *       the rigid body. Other state data is calculated from their current
     *       values. When state data is changed the dependent values need to be
     *       updated: this can be achieved either by integrating the simulation,
     *       or by calling the calculateInternals function. This two stage
     *       process is used because recalculating internals can be a costly
     *       process: all state changes should be carried out at the same time,
     *       allowing for a single call.
     * 
     * @see calculateInternals
     */
    /* @{ */
    /**
     * Holds the inverse of the mass of the rigid body. It is more useful to
     * hold the inverse mass because integration is simpler, and because in
     * double time simulation it is more useful to have bodies with infinite
     * mass (immovable) than zero mass (completely unstable in numerical
     * simulation).
     */
    private double inverseMass;

    /**
     * Holds the inverse of the body's inertia tensor. The inertia tensor
     * provided must not be degenerate (that would mean the body had zero
     * inertia for spinning along one axis). As long as the tensor is finite, it
     * will be invertible. The inverse tensor is used for similar reasons to the
     * use of inverse mass.
     * 
     * The inertia tensor, unlike the other variables that define a rigid body,
     * is given in body space.
     * 
     * @see inverseMass
     */
    private Matrix3 inverseInertiaTensor = new Matrix3();

    /**
     * Holds the amount of damping applied to linear motion. Damping is required
     * to remove energy added through numerical instability in the integrator.
     */
    private double linearDamping;

    /**
     * Holds the amount of damping applied to angular motion. Damping is
     * required to remove energy added through numerical instability in the
     * integrator.
     */
    private double angularDamping;

    /**
     * Holds the linear position of the rigid body in world space.
     */
    private Vector3 position = new Vector3();

    /**
     * Holds the angular orientation of the rigid body in world space.
     */
    private Quaternion orientation = new Quaternion();

    /**
     * Holds the linear velocity of the rigid body in world space.
     */
    private Vector3 velocity = new Vector3();

    /**
     * Holds the angular velocity, or rotation, or the rigid body in world
     * space.
     */
    private Vector3 rotation = new Vector3();

    /* @} */

    /**
     * @name Derived Data
     * 
     *       These data members hold information that is derived from the other
     *       data in the class.
     */
    /* @{ */

    /**
     * Holds the inverse inertia tensor of the body in world space. The inverse
     * inertia tensor member is specified in the body's local space.
     * 
     * @see inverseInertiaTensor
     */
    private Matrix3 inverseInertiaTensorWorld = new Matrix3();

    /**
     * Holds the amount of motion of the body. This is a recency weighted mean
     * that can be used to put a body to sleap.
     */
    private double motion;

    /**
     * A body can be put to sleep to avoid it being updated by the integration
     * functions or affected by collisions with the world.
     */
    private boolean isAwake;

    /**
     * Some bodies may never be allowed to fall asleep. User controlled bodies,
     * for example, should be always awake.
     */
    private boolean canSleep;

    /**
     * Holds a transform matrix for converting body space into world space and
     * vice versa. This can be achieved by calling the getPointIn*Space
     * functions.
     * 
     * @see getPointInLocalSpace
     * @see getPointInWorldSpace
     * @see getTransform
     */
    private Matrix4 transformMatrix = new Matrix4();

    /* @} */

    /**
     * @name Force and Torque Accumulators
     * 
     *       These data members store the current force, torque and acceleration
     *       of the rigid body. Forces can be added to the rigid body in any
     *       order, and the class decomposes them into their ituents,
     *       accumulating them for the next simulation step. At the simulation
     *       step, the accelerations are calculated and stored to be applied to
     *       the rigid body.
     */
    /* @{ */

    /**
     * Holds the accumulated force to be applied at the next integration step.
     */
    private Vector3 forceAccum = new Vector3();

    /**
     * Holds the accumulated torque to be applied at the next integration step.
     */
    private Vector3 torqueAccum = new Vector3();

    /**
     * Holds the acceleration of the rigid body. This value can be used to set
     * acceleration due to gravity (its primary use), or any other ant
     * acceleration.
     */
    private Vector3 acceleration = new Vector3();

    /**
     * Holds the linear acceleration of the rigid body, for the previous frame.
     */
    private Vector3 lastFrameAcceleration;

    /* @} */

    /**
     * @name Constructor and Destructor
     * 
     *       There are no data members in the rigid body class that are created
     *       on the heap. So all data storage is handled automatically.
     */
    /* @{ */

    /* @} */

    /**
     * @name Integration and Simulation Functions
     * 
     *       These functions are used to simulate the rigid body's motion over
     *       time. A normal application sets up one or more rigid bodies,
     *       applies permanent forces (i.e. gravity), then adds transient forces
     *       each frame, and integrates, prior to rendering.
     * 
     *       Currently the only integration function provided is the first order
     *       Newton Euler method.
     */
    /* @{ */

    /**
     * Calculates internal data from state data. This should be called after the
     * body's state is altered directly (it is called automatically during
     * integration). If you change the body's state and then intend to integrate
     * before querying any data (such as the transform matrix), then you can
     * ommit this step.
     */
    public void calculateDerivedData() {
        orientation.normalise();

        // Calculate the transform matrix for the body.
        RigidBodyUtils.calculateTransformMatrix(transformMatrix, position, orientation);

        // Calculate the inertiaTensor in world space.
        RigidBodyUtils.transformInertiaTensor(inverseInertiaTensorWorld, orientation, inverseInertiaTensor, transformMatrix);

    }

    /**
     * Integrates the rigid body forward in time by the given amount. This
     * function uses a Newton-Euler integration method, which is a linear
     * approximation to the correct integral. For this reason it may be
     * inaccurate in some cases.
     */
    public void integrate(double duration) {
        if (!isAwake) return;

        // Calculate linear acceleration from force inputs.
        lastFrameAcceleration = acceleration;
        lastFrameAcceleration.addScaledVector(forceAccum, inverseMass);

        // Calculate angular acceleration from torque inputs.
        Vector3 angularAcceleration = inverseInertiaTensorWorld.transform(torqueAccum);

        // Adjust velocities
        // Update linear velocity from both acceleration and impulse.
        velocity.addScaledVector(lastFrameAcceleration, duration);

        // Update angular velocity from both acceleration and impulse.
        rotation.addScaledVector(angularAcceleration, duration);

        // Impose drag.
        velocity.multToMe(Math.pow(linearDamping, duration));
        rotation.multToMe(Math.pow(angularDamping, duration));

        // Adjust positions
        // Update linear position.
        position.addScaledVector(velocity, duration);

        // Update angular position.
        orientation.addScaledVector(rotation, duration);

        // Normalise the orientation, and update the matrices with the new
        // position and orientation
        calculateDerivedData();

        // Clear accumulators.
        clearAccumulators();

        // Update the kinetic energy store, and possibly put the body to
        // sleep.
        if (canSleep) {
            double currentMotion = velocity.scalarProduct(velocity) + rotation.scalarProduct(rotation);

            double bias = Math.pow(0.5, duration);
            motion = bias * motion + (1 - bias) * currentMotion;

            double sleepEpsilon = Core.get().getSleepEpsilon();
            if (motion < sleepEpsilon) setAwake(false);
            else if (motion > 10 * sleepEpsilon) motion = 10 * sleepEpsilon;
        }
    }

    /* @} */

    /**
     * @name Accessor Functions for the Rigid Body's State
     * 
     *       These functions provide access to the rigid body's characteristics
     *       or state. These data can be accessed individually, or en masse as
     *       an array of values (e.g. getCharacteristics, getState). When
     *       setting new data, make sure the calculateInternals function, or an
     *       integration routine, is called before trying to get data from the
     *       body, since the class contains a number of dependent values that
     *       will need recalculating.
     */
    /* @{ */

    /**
     * Sets the mass of the rigid body.
     * 
     * @param mass
     *            The new mass of the body. This may not be zero. Small masses
     *            can produce unstable rigid bodies under simulation.
     * 
     * @warning This invalidates internal data for the rigid body. Either an
     *          integration function, or the calculateInternals function should
     *          be called before trying to get any settings from the rigid body.
     */
    public void setMass(double mass) {
        assert (mass != 0);
        this.inverseMass = 1.0 / mass;
    }

    /**
     * Gets the mass of the rigid body.
     * 
     * @return The current mass of the rigid body.
     */
    public double getMass() {
        if (inverseMass == 0) {
            return Double.MAX_VALUE;
        }
        return 1.0 / inverseMass;
    }

    /**
     * Sets the inverse mass of the rigid body.
     * 
     * @param inverseMass
     *            The new inverse mass of the body. This may be zero, for a body
     *            with infinite mass (i.e. unmovable).
     * 
     * @warning This invalidates internal data for the rigid body. Either an
     *          integration function, or the calculateInternals function should
     *          be called before trying to get any settings from the rigid body.
     */
    public void setInverseMass(double inverseMass) {
        this.inverseMass = inverseMass;
    }

    /**
     * Gets the inverse mass of the rigid body.
     * 
     * @return The current inverse mass of the rigid body.
     */
    public double getInverseMass() {
        return inverseMass;
    }

    /**
     * Returns true if the mass of the body is not-infinite.
     */
    public boolean hasFiniteMass() {
        return inverseMass >= 0.0f;
    }

    /**
     * Sets the intertia tensor for the rigid body.
     * 
     * @param inertiaTensor
     *            The inertia tensor for the rigid body. This must be a full
     *            rank matrix and must be invertible.
     * 
     * @warning This invalidates internal data for the rigid body. Either an
     *          integration function, or the calculateInternals function should
     *          be called before trying to get any settings from the rigid body.
     */
    public void setInertiaTensor(Matrix3 inertiaTensor) {
        inverseInertiaTensor.setInverse(inertiaTensor);
        RigidBodyUtils.checkInverseInertiaTensor(inverseInertiaTensor);
    }

    /**
     * Copies the current inertia tensor of the rigid body into the given
     * matrix.
     * 
     * @param inertiaTensor
     *            A pointer to a matrix to hold the current inertia tensor of
     *            the rigid body. The inertia tensor is expressed in the rigid
     *            body's local space.
     */
    @Deprecated
    public void getInertiaTensor(Matrix3 inertiaTensor) {
        inertiaTensor.setInverse(inverseInertiaTensor);
    }

    /**
     * Gets a copy of the current inertia tensor of the rigid body.
     * 
     * @return A new matrix containing the current intertia tensor. The inertia
     *         tensor is expressed in the rigid body's local space.
     */
    public Matrix3 getInertiaTensor() {
        Matrix3 it = new Matrix3();
        getInertiaTensor(it);
        return it;
    }

    /**
     * Copies the current inertia tensor of the rigid body into the given
     * matrix.
     * 
     * @param inertiaTensor
     *            A pointer to a matrix to hold the current inertia tensor of
     *            the rigid body. The inertia tensor is expressed in world
     *            space.
     */
    @Deprecated
    public void getInertiaTensorWorld(Matrix3 inertiaTensor) {
        inertiaTensor.setInverse(inverseInertiaTensorWorld);
    }

    /**
     * Gets a copy of the current inertia tensor of the rigid body.
     * 
     * @return A new matrix containing the current intertia tensor. The inertia
     *         tensor is expressed in world space.
     */
    public Matrix3 getInertiaTensorWorld() {
        Matrix3 it = new Matrix3();
        getInertiaTensorWorld(it);
        return it;
    }

    /**
     * Sets the inverse intertia tensor for the rigid body.
     * 
     * @param inverseInertiaTensor
     *            The inverse inertia tensor for the rigid body. This must be a
     *            full rank matrix and must be invertible.
     * 
     * @warning This invalidates internal data for the rigid body. Either an
     *          integration function, or the calculateInternals function should
     *          be called before trying to get any settings from the rigid body.
     */
    public void setInverseInertiaTensor(Matrix3 inverseInertiaTensor) {
        RigidBodyUtils.checkInverseInertiaTensor(inverseInertiaTensor);
        this.inverseInertiaTensor = inverseInertiaTensor;
    }

    /**
     * Copies the current inverse inertia tensor of the rigid body into the
     * given matrix.
     * 
     * @param inverseInertiaTensor
     *            A pointer to a matrix to hold the current inverse inertia
     *            tensor of the rigid body. The inertia tensor is expressed in
     *            the rigid body's local space.
     */
    @Deprecated
    private void getInverseInertiaTensor(Matrix3 inverseInertiaTensor) {
        fillMatrix3(this.inverseInertiaTensor, inverseInertiaTensor);
    }

    /**
     * Gets a copy of the current inverse inertia tensor of the rigid body.
     * 
     * @return A new matrix containing the current inverse intertia tensor. The
     *         inertia tensor is expressed in the rigid body's local space.
     */
    public Matrix3 getInverseInertiaTensor() {
        return inverseInertiaTensor;
    }

    /**
     * Copies the current inverse inertia tensor of the rigid body into the
     * given matrix.
     * 
     * @param inverseInertiaTensor
     *            A pointer to a matrix to hold the current inverse inertia
     *            tensor of the rigid body. The inertia tensor is expressed in
     *            world space.
     */
    @Deprecated
    public void getInverseInertiaTensorWorld(Matrix3 inverseInertiaTensor) {
        fillMatrix3(this.inverseInertiaTensorWorld, inverseInertiaTensor);
    }

    /**
     * Gets a copy of the current inverse inertia tensor of the rigid body.
     * 
     * @return A new matrix containing the current inverse intertia tensor. The
     *         inertia tensor is expressed in world space.
     */
    public Matrix3 getInverseInertiaTensorWorld() {
        return inverseInertiaTensorWorld;
    }

    /**
     * Sets both linear and angular damping in one function call.
     * 
     * @param linearDamping
     *            The speed that velocity is shed from the rigid body.
     * 
     * @param angularDamping
     *            The speed that rotation is shed from the rigid body.
     * 
     * @see setLinearDamping
     * @see setAngularDamping
     */
    public void setDamping(double linearDamping, double angularDamping) {
        this.linearDamping = linearDamping;
        this.angularDamping = angularDamping;
    }

    /**
     * Sets the linear damping for the rigid body.
     * 
     * @param linearDamping
     *            The speed that velocity is shed from the rigid body.
     * 
     * @see setAngularDamping
     */
    public void setLinearDamping(double linearDamping) {
        this.linearDamping = linearDamping;
    }

    /**
     * Gets the current linear damping value.
     * 
     * @return The current linear damping value.
     */
    public double getLinearDamping() {
        return linearDamping;
    }

    /**
     * Sets the angular damping for the rigid body.
     * 
     * @param angularDamping
     *            The speed that rotation is shed from the rigid body.
     * 
     * @see setLinearDamping
     */
    public void setAngularDamping(double angularDamping) {
        this.angularDamping = angularDamping;
    }

    /**
     * Gets the current angular damping value.
     * 
     * @return The current angular damping value.
     */
    public double getAngularDamping() {
        return angularDamping;
    }

    /**
     * Sets the position of the rigid body.
     * 
     * @param position
     *            The new position of the rigid body.
     */
    public void setPosition(Vector3 position) {
        this.position = position;
    }

    /**
     * Sets the position of the rigid body by component.
     * 
     * @param x
     *            The x coordinate of the new position of the rigid body.
     * 
     * @param y
     *            The y coordinate of the new position of the rigid body.
     * 
     * @param z
     *            The z coordinate of the new position of the rigid body.
     */
    public void setPosition(double x, double y, double z) {
        position.setX(x);
        position.setY(y);
        position.setZ(z);
    }

    /**
     * Fills the given vector with the position of the rigid body.
     * 
     * @param position
     *            A pointer to a vector into which to write the position.
     */
    @Deprecated
    public void getPosition(Vector3 position) {
        fillVector3(this.position, position);
    }

    /**
     * Gets the position of the rigid body.
     * 
     * @return The position of the rigid body.
     */
    public Vector3 getPosition() {
        return position;
    }

    /**
     * Sets the orientation of the rigid body.
     * 
     * @param orientation
     *            The new orientation of the rigid body.
     * 
     * @note The given orientation does not need to be normalised, and can be
     *       zero. This function automatically ructs a valid rotation quaternion
     *       with (0,0,0,0) mapping to (1,0,0,0).
     */
    public void setOrientation(Quaternion orientation) {
        this.orientation = orientation;
        this.orientation.normalise();
    }

    /**
     * Sets the orientation of the rigid body by component.
     * 
     * @param r
     *            The double component of the rigid body's orientation
     *            quaternion.
     * 
     * @param i
     *            The first complex component of the rigid body's orientation
     *            quaternion.
     * 
     * @param j
     *            The second complex component of the rigid body's orientation
     *            quaternion.
     * 
     * @param k
     *            The third complex component of the rigid body's orientation
     *            quaternion.
     * 
     * @note The given orientation does not need to be normalised, and can be
     *       zero. This function automatically ructs a valid rotation quaternion
     *       with (0,0,0,0) mapping to (1,0,0,0).
     */
    public void setOrientation(double r, double i, double j, double k) {
        orientation.setR(r);
        orientation.setI(i);
        orientation.setJ(j);
        orientation.setK(k);
        orientation.normalise();
    }

    /**
     * Fills the given quaternion with the current value of the rigid body's
     * orientation.
     * 
     * @param orientation
     *            A pointer to a quaternion to receive the orientation data.
     */
    @Deprecated
    public void getOrientation(Quaternion orientation) {
        fillQuaternion(this.orientation, orientation);
    }

    /**
     * Gets the orientation of the rigid body.
     * 
     * @return The orientation of the rigid body.
     */
    public Quaternion getOrientation() {
        return orientation;
    }

    /**
     * Fills the given matrix with a transformation representing the rigid
     * body's orientation.
     * 
     * @note Transforming a direction vector by this matrix turns it from the
     *       body's local space to world space.
     * 
     * @param matrix
     *            A pointer to the matrix to fill.
     */
    @Deprecated
    public void getOrientation(Matrix3 matrix) {
        getOrientation(matrix.data);
    }

    /**
     * Fills the given matrix data structure with a transformation representing
     * the rigid body's orientation.
     * 
     * @note Transforming a direction vector by this matrix turns it from the
     *       body's local space to world space.
     * 
     * @param matrix
     *            A pointer to the matrix to fill.
     */
    public void getOrientation(double[] matrix) {
        matrix[0] = transformMatrix.getData(0);
        matrix[1] = transformMatrix.getData(1);
        matrix[2] = transformMatrix.getData(2);

        matrix[3] = transformMatrix.getData(4);
        matrix[4] = transformMatrix.getData(5);
        matrix[5] = transformMatrix.getData(6);

        matrix[6] = transformMatrix.getData(8);
        matrix[7] = transformMatrix.getData(9);
        matrix[8] = transformMatrix.getData(10);
    }

    /**
     * Fills the given matrix with a transformation representing the rigid
     * body's position and orientation.
     * 
     * @note Transforming a vector by this matrix turns it from the body's local
     *       space to world space.
     * 
     * @param transform
     *            A pointer to the matrix to fill.
     */
    @Deprecated
    public void getTransform(Matrix4 transform) {
        double[] copy = Arrays.copyOf(transformMatrix.getData(), transformMatrix.getData().length);
        transform.setData(copy);
    }

    /**
     * Fills the given matrix data structure with a transformation representing
     * the rigid body's position and orientation.
     * 
     * @note Transforming a vector by this matrix turns it from the body's local
     *       space to world space.
     * 
     * @param matrix
     *            A pointer to the matrix to fill.
     */
    public void getTransform(double[] matrix) {
        for (int i = 0; i < transformMatrix.getData().length; i++) {
            matrix[i] = transformMatrix.getData()[i];
        }
        matrix[12] = matrix[13] = matrix[14] = 0;
        matrix[15] = 1;
    }

    /**
     * Fills the given matrix data structure with a transformation representing
     * the rigid body's position and orientation. The matrix is transposed from
     * that returned by getTransform. This call returns a matrix suitable for
     * applying as an OpenGL transform.
     * 
     * @note Transforming a vector by this matrix turns it from the body's local
     *       space to world space.
     * 
     * @param matrix
     *            A pointer to the matrix to fill.
     */
    public void getGLTransform(float[] matrix) {
        matrix[0] = (float) transformMatrix.getData(0);
        matrix[1] = (float) transformMatrix.getData(4);
        matrix[2] = (float) transformMatrix.getData(8);
        matrix[3] = 0;

        matrix[4] = (float) transformMatrix.getData(1);
        matrix[5] = (float) transformMatrix.getData(5);
        matrix[6] = (float) transformMatrix.getData(9);
        matrix[7] = 0;

        matrix[8] = (float) transformMatrix.getData(2);
        matrix[9] = (float) transformMatrix.getData(6);
        matrix[10] = (float) transformMatrix.getData(10);
        matrix[11] = 0;

        matrix[12] = (float) transformMatrix.getData(3);
        matrix[13] = (float) transformMatrix.getData(7);
        matrix[14] = (float) transformMatrix.getData(11);
        matrix[15] = 1;
    }

    /**
     * Gets a transformation representing the rigid body's position and
     * orientation.
     * 
     * @note Transforming a vector by this matrix turns it from the body's local
     *       space to world space.
     * 
     * @return The transform matrix for the rigid body.
     */
    public Matrix4 getTransform() {
        return transformMatrix;
    }

    /**
     * Converts the given point from world space into the body's local space.
     * 
     * @param point
     *            The point to covert, given in world space.
     * 
     * @return The converted point, in local space.
     */
    public Vector3 getPointInLocalSpace(Vector3 point) {
        return transformMatrix.transformInverse(point);
    }

    /**
     * Converts the given point from world space into the body's local space.
     * 
     * @param point
     *            The point to covert, given in local space.
     * 
     * @return The converted point, in world space.
     */
    public Vector3 getPointInWorldSpace(Vector3 point) {
        return transformMatrix.transform(point);
    }

    /**
     * Converts the given direction from world space into the body's local
     * space.
     * 
     * @note When a direction is converted between frames of reference, there is
     *       no translation required.
     * 
     * @param direction
     *            The direction to covert, given in world space.
     * 
     * @return The converted direction, in local space.
     */
    public Vector3 getDirectionInLocalSpace(Vector3 direction) {
        return transformMatrix.transformInverseDirection(direction);
    }

    /**
     * Converts the given direction from world space into the body's local
     * space.
     * 
     * @note When a direction is converted between frames of reference, there is
     *       no translation required.
     * 
     * @param direction
     *            The direction to covert, given in local space.
     * 
     * @return The converted direction, in world space.
     */
    public Vector3 getDirectionInWorldSpace(Vector3 direction) {
        return transformMatrix.transformDirection(direction);
    }

    /**
     * Sets the velocity of the rigid body.
     * 
     * @param velocity
     *            The new velocity of the rigid body. The velocity is given in
     *            world space.
     */
    public void setVelocity(Vector3 velocity) {
        this.velocity = velocity;
    }

    /**
     * Sets the velocity of the rigid body by component. The velocity is given
     * in world space.
     * 
     * @param x
     *            The x coordinate of the new velocity of the rigid body.
     * 
     * @param y
     *            The y coordinate of the new velocity of the rigid body.
     * 
     * @param z
     *            The z coordinate of the new velocity of the rigid body.
     */
    public void setVelocity(double x, double y, double z) {
        velocity.setX(x);
        velocity.setY(y);
        velocity.setZ(z);
    }

    /**
     * Fills the given vector with the velocity of the rigid body.
     * 
     * @param velocity
     *            A pointer to a vector into which to write the velocity. The
     *            velocity is given in world local space.
     */
    @Deprecated
    private void getVelocity(Vector3 velocity) {
        fillVector3(this.velocity, velocity);
    }

    /**
     * Gets the velocity of the rigid body.
     * 
     * @return The velocity of the rigid body. The velocity is given in world
     *         local space.
     */
    public Vector3 getVelocity() {
        return velocity;
    }

    /**
     * Applies the given change in velocity.
     */
    public void addVelocity(Vector3 deltaVelocity) {
        velocity.sumToMe(deltaVelocity);
    }

    /**
     * Sets the rotation of the rigid body.
     * 
     * @param rotation
     *            The new rotation of the rigid body. The rotation is given in
     *            world space.
     */
    public void setRotation(Vector3 rotation) {
        this.rotation = rotation;
    }

    /**
     * Sets the rotation of the rigid body by component. The rotation is given
     * in world space.
     * 
     * @param x
     *            The x coordinate of the new rotation of the rigid body.
     * 
     * @param y
     *            The y coordinate of the new rotation of the rigid body.
     * 
     * @param z
     *            The z coordinate of the new rotation of the rigid body.
     */
    public void setRotation(double x, double y, double z) {
        rotation.setX(x);
        rotation.setY(y);
        rotation.setZ(z);
    }

    /**
     * Fills the given vector with the rotation of the rigid body.
     * 
     * @param rotation
     *            A pointer to a vector into which to write the rotation. The
     *            rotation is given in world local space.
     */
    @Deprecated
    private void getRotation(Vector3 rotation) {
        fillVector3(this.rotation, rotation);
    }

    /**
     * Gets the rotation of the rigid body.
     * 
     * @return The rotation of the rigid body. The rotation is given in world
     *         local space.
     */
    public Vector3 getRotation() {
        return rotation;
    }

    /**
     * Applies the given change in rotation.
     */
    public void addRotation(Vector3 deltaRotation) {
        rotation.sumToMe(deltaRotation);
    }

    /**
     * Returns true if the body is awake and responding to integration.
     * 
     * @return The awake state of the body.
     */
    public boolean getAwake() {
        return isAwake;
    }

    /**
     * Sets the awake state of the body. If the body is set to be not awake,
     * then its velocities are also cancelled, since a moving body that is not
     * awake can cause problems in the simulation.
     * 
     * @param awake
     *            The new awake state of the body.
     */
    public void setAwake() {
        setAwake(true);

    }

    /**
     * Sets the awake state of the body. If the body is set to be not awake,
     * then its velocities are also cancelled, since a moving body that is not
     * awake can cause problems in the simulation.
     * 
     * @param awake
     *            The new awake state of the body.
     */
    public void setAwake(boolean awake) {
        if (awake) {
            isAwake = true;

            // Add a bit of motion to avoid it falling asleep immediately.
            motion = Core.get().getSleepEpsilon() * 2.0f;
        } else {
            isAwake = false;
            velocity.clear();
            rotation.clear();
        }
    }

    /**
     * Returns true if the body is allowed to go to sleep at any time.
     */
    public boolean getCanSleep() {
        return canSleep;
    }

    /**
     * Sets whether the body is ever allowed to go to sleep. Bodies under the
     * player's control, or for which the set of transient forces applied each
     * frame are not predictable, should be kept awake.
     * 
     * @param canSleep
     *            Whether the body can now be put to sleep.
     */
    public void setCanSleep() {
        setCanSleep(true);
    }

    /**
     * Sets whether the body is ever allowed to go to sleep. Bodies under the
     * player's control, or for which the set of transient forces applied each
     * frame are not predictable, should be kept awake.
     * 
     * @param canSleep
     *            Whether the body can now be put to sleep.
     */
    public void setCanSleep(boolean canSleep) {
        this.canSleep = canSleep;

        if (!canSleep && !isAwake) setAwake();
    }

    /* @} */

    /**
     * @name Retrieval Functions for Dynamic Quantities
     * 
     *       These functions provide access to the acceleration properties of
     *       the body. The acceleration is generated by the simulation from the
     *       forces and torques applied to the rigid body. Acceleration cannot
     *       be directly influenced, it is set during integration, and represent
     *       the acceleration experienced by the body of the previous simulation
     *       step.
     */
    /* @{ */

    /**
     * Fills the given vector with the current accumulated value for linear
     * acceleration. The acceleration accumulators are set during the
     * integration step. They can be read to determine the rigid body's
     * acceleration over the last integration step. The linear acceleration is
     * given in world space.
     * 
     * @param linearAcceleration
     *            A pointer to a vector to receive the linear acceleration data.
     */
    @Deprecated
    public void getLastFrameAcceleration(Vector3 linearAcceleration) {
        linearAcceleration = this.lastFrameAcceleration;
    }

    /**
     * Gets the current accumulated value for linear acceleration. The
     * acceleration accumulators are set during the integration step. They can
     * be read to determine the rigid body's acceleration over the last
     * integration step. The linear acceleration is given in world space.
     * 
     * @return The rigid body's linear acceleration.
     */
    public Vector3 getLastFrameAcceleration() {
        return lastFrameAcceleration;
    }

    /* @} */

    /**
     * @name Force, Torque and Acceleration Set-up Functions
     * 
     *       These functions set up forces and torques to apply to the rigid
     *       body.
     */
    /* @{ */

    /**
     * Clears the forces and torques in the accumulators. This will be called
     * automatically after each intergration step.
     */
    public void clearAccumulators() {
        forceAccum.clear();
        torqueAccum.clear();
    }

    /**
     * Adds the given force to centre of mass of the rigid body. The force is
     * expressed in world-coordinates.
     * 
     * @param force
     *            The force to apply.
     */
    public void addForce(Vector3 force) {
        forceAccum.sumToMe(force);
        isAwake = true;
    }

    /**
     * Adds the given force to the given point on the rigid body. Both the force
     * and the application point are given in world space. Because the force is
     * not applied at the centre of mass, it may be split into both a force and
     * torque.
     * 
     * @param force
     *            The force to apply.
     * 
     * @param point
     *            The location at which to apply the force, in
     *            world-coordinates.
     */
    public void addForceAtPoint(Vector3 force, Vector3 point) {
        // Convert to coordinates relative to center of mass.
        Vector3 pt = point;
        pt.subToMe(position);

        forceAccum.sumToMe(force);
        torqueAccum.sumToMe(pt.rest(force));

        isAwake = true;
    }

    /**
     * Adds the given force to the given point on the rigid body. The direction
     * of the force is given in world coordinates, but the application point is
     * given in body space. This is useful for spring forces, or other forces
     * fixed to the body.
     * 
     * @param force
     *            The force to apply.
     * 
     * @param point
     *            The location at which to apply the force, in body-coordinates.
     */
    public void addForceAtBodyPoint(Vector3 force, Vector3 point) {
        // Convert to coordinates relative to center of mass.
        Vector3 pt = getPointInWorldSpace(point);
        addForceAtPoint(force, pt);

    }

    /**
     * Adds the given torque to the rigid body. The force is expressed in
     * world-coordinates.
     * 
     * @param torque
     *            The torque to apply.
     */
    public void addTorque(Vector3 torque) {
        torqueAccum.sumToMe(torque);
        isAwake = true;
    }

    /**
     * Sets the ant acceleration of the rigid body.
     * 
     * @param acceleration
     *            The new acceleration of the rigid body.
     */
    public void setAcceleration(Vector3 acceleration) {
        this.acceleration = acceleration;
    }

    /**
     * Sets the ant acceleration of the rigid body by component.
     * 
     * @param x
     *            The x coordinate of the new acceleration of the rigid body.
     * 
     * @param y
     *            The y coordinate of the new acceleration of the rigid body.
     * 
     * @param z
     *            The z coordinate of the new acceleration of the rigid body.
     */
    public void setAcceleration(double x, double y, double z) {
        acceleration.setX(x);
        acceleration.setY(y);
        acceleration.setZ(z);
    }

    /**
     * Fills the given vector with the acceleration of the rigid body.
     * 
     * @param acceleration
     *            A pointer to a vector into which to write the acceleration.
     *            The acceleration is given in world local space.
     */
    @Deprecated
    private void getAcceleration(Vector3 acceleration) {
        fillVector3(this.acceleration, acceleration);
    }

    /**
     * Gets the acceleration of the rigid body.
     * 
     * @return The acceleration of the rigid body. The acceleration is given in
     *         world local space.
     */
    public Vector3 getAcceleration() {
        return acceleration;
    }

    private void fillVector3(Vector3 origin, Vector3 destin) {
        destin.setX(origin.getX());
        destin.setY(origin.getY());
        destin.setZ(origin.getZ());
    }

    private void fillMatrix3(Matrix3 origin, Matrix3 destin) {
        for (int i = 0; i < 9; i++) {
            destin.setData(i, origin.getData(i));
        }
    }

    private void fillQuaternion(Quaternion origin, Quaternion destin) {
        destin.setR(origin.getR());
        destin.setI(origin.getI());
        destin.setJ(origin.getJ());
        destin.setK(origin.getK());
    }

}
