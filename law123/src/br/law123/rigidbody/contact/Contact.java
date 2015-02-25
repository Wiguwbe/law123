package br.law123.rigidbody.contact;

import br.law123.core.Matrix3;
import br.law123.core.Quaternion;
import br.law123.core.Vector3;
import br.law123.rigidbody.RigidBody;

/**
 * A contact represents two bodies in contact. Resolving a
 * contact removes their interpenetration, and applies sufficient
 * impulse to keep them apart. Colliding bodies may also rebound.
 * Contacts can be used to represent positional joints, by making
 * the contact raint keep the bodies in their correct
 * orientation.
 * 
 * It can be a good idea to create a contact object even when the
 * contact isn't violated. Because resolving one contact can violate
 * another, contacts that are close to being violated should be
 * sent to the resolver; that way if one resolution moves the body,
 * the contact may be violated, and can be resolved. If the contact
 * is not violated, it will not be resolved, so you only loose a
 * small amount of execution time.
 * 
 * The contact has no callable functions, it just holds the contact
 * details. To resolve a set of contacts, use the contact resolver
 * class.
 */
public class Contact {

    private static final double velocityLimit = 0.25f;
    private static final double angularLimit = 0.2f;
    // ... Other data as before ...

    /**
     * The contact resolver object needs access into the contacts to
     * set and effect the contact.
     */

    /**
     * Holds the bodies that are involved in the contact. The
     * second of these can be NULL, for contacts with the scenery.
     */
    private RigidBody[] body = new RigidBody[2];

    /**
     * Holds the lateral friction coefficient at the contact.
     */
    private double friction;

    /**
     * Holds the normal restitution coefficient at the contact.
     */
    private double restitution;

    /**
     * Holds the position of the contact in world coordinates.
     */
    private Vector3 contactPoint;

    /**
     * Holds the direction of the contact in world coordinates.
     */
    private Vector3 contactNormal = new Vector3();

    /**
     * Holds the depth of penetration at the contact point. If both
     * bodies are specified then the contact point should be midway
     * between the inter-penetrating points.
     */
    private double penetration;

    /**
     * A transform matrix that converts co-ordinates in the contact's
     * frame of reference to world co-ordinates. The columns of this
     * matrix form an orthonormal set of vectors.
     */
    protected Matrix3 contactToWorld = new Matrix3();

    /**
     * Holds the closing velocity at the point of contact. This is set
     * when the calculateInternals function is run.
     */
    protected Vector3 contactVelocity;

    /**
     * Holds the required change in velocity for this contact to be
     * resolved.
     */
    protected double desiredDeltaVelocity;

    /**
     * Holds the world space position of the contact point relative to
     * centre of each body. This is set when the calculateInternals
     * function is run.
     */
    protected Vector3[] relativeContactPosition = new Vector3[2];

    public RigidBody[] getBody() {
        return body;
    }

    public double getFriction() {
        return friction;
    }

    public void setFriction(double friction) {
        this.friction = friction;
    }

    public double getRestitution() {
        return restitution;
    }

    public void setRestitution(double restitution) {
        this.restitution = restitution;
    }

    public Vector3 getContactPoint() {
        return contactPoint;
    }

    public void setContactPoint(Vector3 contactPoint) {
        this.contactPoint = new Vector3(contactPoint);
    }

    public Vector3 getContactNormal() {
        return contactNormal;
    }

    public void setContactNormal(Vector3 contactNormal) {
        this.contactNormal = new Vector3(contactNormal);
    }

    public double getPenetration() {
        return penetration;
    }

    public void setPenetration(double penetration) {
        this.penetration = penetration;
    }

    public Matrix3 getContactToWorld() {
        return contactToWorld;
    }

    /**
     * Sets the data that doesn't normally depend on the position
     * of the contact (i.e. the bodies, and their material properties).
     */
    public void setBodyData(RigidBody one, RigidBody two, double friction, double restitution) {
        this.body[0] = one;
        this.body[1] = two;
        this.friction = friction;
        this.restitution = restitution;
    }

    /**
     * Calculates internal data from state data. This is called before
     * the resolution algorithm tries to do any resolution. It should
     * never need to be called manually.
     */
    protected void calculateInternals(double duration) {
        // Check if the first object is NULL, and swap if it is.
        if (body[0] == null) swapBodies();
        assert (body[0] != null);

        // Calculate an set of axis at the contact point.
        calculateContactBasis();

        // Store the relative position of the contact relative to each body
        relativeContactPosition[0] = contactPoint.sub(body[0].getPosition());
        if (body[1] != null) {
            relativeContactPosition[1] = contactPoint.sub(body[1].getPosition());
        }

        // Find the relative velocity of the bodies at the contact point.
        contactVelocity = calculateLocalVelocity(0, duration);
        if (body[1] != null) {
            contactVelocity.subToMe(calculateLocalVelocity(1, duration));
        }

        // Calculate the desired change in velocity for resolution
        calculateDesiredDeltaVelocity(duration);
    }

    /**
     * Reverses the contact. This involves swapping the two rigid bodies
     * and reversing the contact normal. The internal values should then
     * be recalculated using calculateInternals (this is not done
     * automatically).
     * Swaps the bodies in the current contact, so body 0 is at body 1 and
     * vice versa. This also changes the direction of the contact normal,
     * but doesn't update any calculated internal data. If you are calling
     * this method manually, then call calculateInternals afterwards to
     * make sure the internal data is up to date.
     */
    protected void swapBodies() {
        contactNormal.multToMe(-1);

        RigidBody temp = body[0];
        body[0] = body[1];
        body[1] = temp;
    }

    /**
     * Updates the awake state of rigid bodies that are taking
     * place in the given contact. A body will be made awake if it
     * is in contact with a body that is awake.
     */
    protected void matchAwakeState() {
        // Collisions with the world never cause a body to wake up.
        if (body[1] == null) return;

        boolean body0awake = body[0].getAwake();
        boolean body1awake = body[1].getAwake();

        // Wake up only the sleeping one
        if (body0awake ^ body1awake) {
            if (body0awake) body[1].setAwake();
            else body[0].setAwake();
        }
    }

    /**
     * Calculates and sets the internal value for the desired delta
     * velocity.
     */
    protected void calculateDesiredDeltaVelocity(double duration) {

        // Calculate the acceleration induced velocity accumulated this frame
        double velocityFromAcc = 0;

        if (body[0].getAwake()) {
            velocityFromAcc += body[0].getLastFrameAcceleration().mult(duration).mult(contactNormal);
        }

        if (body[1] != null && body[1].getAwake()) {
            velocityFromAcc -= body[1].getLastFrameAcceleration().mult(duration).mult(contactNormal);
        }

        // If the velocity is very slow, limit the restitution
        double thisRestitution = restitution;
        if (Math.abs(contactVelocity.getX()) < velocityLimit) {
            thisRestitution = 0.0f;
        }

        // Combine the bounce velocity with the removed
        // acceleration velocity.
        desiredDeltaVelocity = -contactVelocity.getX() - thisRestitution * (contactVelocity.getX() - velocityFromAcc);
    }

    /**
     * Calculates and returns the velocity of the contact
     * point on the given body.
     */
    protected Vector3 calculateLocalVelocity(int bodyIndex, double duration) {
        RigidBody thisBody = body[bodyIndex];

        // Work out the velocity of the contact point.
        Vector3 velocity = thisBody.getRotation().rest(relativeContactPosition[bodyIndex]);
        velocity.sumToMe(thisBody.getVelocity());

        // Turn the velocity into contact-coordinates.
        Vector3 _contactVelocity = contactToWorld.transformTranspose(velocity);

        // Calculate the ammount of velocity that is due to forces without
        // reactions.
        Vector3 accVelocity = thisBody.getLastFrameAcceleration().mult(duration);

        // Calculate the velocity in contact-coordinates.
        accVelocity = contactToWorld.transformTranspose(accVelocity);

        // We ignore any component of acceleration in the contact normal
        // direction, we are only interested in planar acceleration
        accVelocity.setX(0);

        // Add the planar velocities - if there's enough friction they will
        // be removed during velocity resolution
        _contactVelocity.sumToMe(accVelocity);

        // And return it
        return _contactVelocity;
    }

    /**
     * Calculates an orthonormal basis for the contact point, based on
     * the primary friction direction (for anisotropic friction) or
     * a random orientation (for isotropic friction).
     * 
     * ructs an arbitrary orthonormal basis for the contact. This is
     * stored as a 3x3 matrix, where each vector is a column (in other
     * words the matrix transforms contact space into world space). The x
     * direction is generated from the contact normal, and the y and z
     * directionss are set so they are at right angles to it.
     */
    protected void calculateContactBasis() {
        Vector3[] contactTangent = new Vector3[2];
        contactTangent[0] = new Vector3();
        contactTangent[1] = new Vector3();

        // Check whether the Z-axis is nearer to the X or Y axis
        if (Math.abs(contactNormal.getX()) > Math.abs(contactNormal.getY())) {
            // Scaling factor to ensure the results are normalised
            double s = 1.0f / Math.sqrt(contactNormal.getZ() * contactNormal.getZ() + contactNormal.getX() * contactNormal.getX());

            // The new X-axis is at right angles to the world Y-axis
            contactTangent[0].setX(contactNormal.getZ() * s);
            contactTangent[0].setY(0);
            contactTangent[0].setZ(-contactNormal.getX() * s);

            // The new Y-axis is at right angles to the new X- and Z- axes
            contactTangent[1].setX(contactNormal.getY() * contactTangent[0].getX());
            contactTangent[1].setY(contactNormal.getZ() * contactTangent[0].getX() - contactNormal.getX() * contactTangent[0].getZ());
            contactTangent[1].setZ(-contactNormal.getY() * contactTangent[0].getX());
        } else {
            // Scaling factor to ensure the results are normalised
            double s = 1.0 / Math.sqrt(contactNormal.getZ() * contactNormal.getZ() + contactNormal.getY() * contactNormal.getY());

            // The new X-axis is at right angles to the world X-axis
            contactTangent[0].setX(0);
            contactTangent[0].setY(-contactNormal.getZ() * s);
            contactTangent[0].setZ(contactNormal.getY() * s);

            // The new Y-axis is at right angles to the new X- and Z- axes
            contactTangent[1].setX(contactNormal.getY() * contactTangent[0].getZ() - contactNormal.getZ() * contactTangent[0].getY());
            contactTangent[1].setY(-contactNormal.getX() * contactTangent[0].getZ());
            contactTangent[1].setZ(contactNormal.getX() * contactTangent[0].getY());
        }

        // Make a matrix from the three vectors.
        contactToWorld.setComponents(contactNormal, contactTangent[0], contactTangent[1]);
    }

    /**
     * Applies an impulse to the given body, returning the
     * change in velocities.
     */
    //protected void applyImpulse( Vector3 &impulse, RigidBody *body, Vector3 *velocityChange, Vector3 *rotationChange);

    /**
     * Performs an inertia-weighted impulse based resolution of this
     * contact alone.
     */
    protected void applyVelocityChange(Vector3 velocityChange[], Vector3 rotationChange[]) {
        // Get hold of the inverse mass and inverse inertia tensor, both in
        // world coordinates.
        Matrix3[] inverseInertiaTensor = { null, null };
        inverseInertiaTensor[0] = body[0].getInverseInertiaTensorWorld();
        if (body[1] != null) {
            inverseInertiaTensor[1] = body[1].getInverseInertiaTensorWorld();
        }

        // We will calculate the impulse for each contact axis
        Vector3 impulseContact;

        if (friction == 0.0) {
            // Use the short format for frictionless contacts
            impulseContact = calculateFrictionlessImpulse(inverseInertiaTensor);
        } else {
            // Otherwise we may have impulses that aren't in the direction of the
            // contact, so we need the more complex version.
            impulseContact = calculateFrictionImpulse(inverseInertiaTensor);
        }

        // Convert impulse to world coordinates
        Vector3 impulse = contactToWorld.transform(impulseContact);

        // Split in the impulse into linear and rotational components
        Vector3 impulsiveTorque = relativeContactPosition[0].rest(impulse);
        rotationChange[0] = inverseInertiaTensor[0].transform(impulsiveTorque);
        velocityChange[0].clear();
        velocityChange[0].addScaledVector(impulse, body[0].getInverseMass());

        // Apply the changes
        body[0].addVelocity(velocityChange[0]);
        body[0].addRotation(rotationChange[0]);

        if (body[1] != null) {
            // Work out body one's linear and angular changes
            impulsiveTorque = impulse.rest(relativeContactPosition[1]);
            rotationChange[1] = inverseInertiaTensor[1].transform(impulsiveTorque);
            velocityChange[1].clear();
            velocityChange[1].addScaledVector(impulse, -body[1].getInverseMass());

            // And apply them.
            body[1].addVelocity(velocityChange[1]);
            body[1].addRotation(rotationChange[1]);
        }
    }

    /**
     * Performs an inertia weighted penetration resolution of this
     * contact alone.
     */
    protected void applyPositionChange(Vector3 linearChange[], Vector3 angularChange[], double penetration) {

        double[] angularMove = new double[2];
        double[] linearMove = new double[2];

        double totalInertia = 0;
        double[] linearInertia = new double[2];
        double[] angularInertia = new double[2];

        // We need to work out the inertia of each object in the direction
        // of the contact normal, due to angular inertia only.
        for (int i = 0; i < 2; i++)
            if (body[i] != null) {
                Matrix3 inverseInertiaTensor = body[i].getInverseInertiaTensorWorld();

                // Use the same procedure as for calculating frictionless
                // velocity change to work out the angular inertia.
                Vector3 angularInertiaWorld = relativeContactPosition[i].rest(contactNormal);
                angularInertiaWorld = inverseInertiaTensor.transform(angularInertiaWorld);
                angularInertiaWorld = angularInertiaWorld.rest(relativeContactPosition[i]);
                angularInertia[i] = angularInertiaWorld.mult(contactNormal);

                // The linear component is simply the inverse mass
                linearInertia[i] = body[i].getInverseMass();

                // Keep track of the total inertia from all components
                totalInertia += linearInertia[i] + angularInertia[i];

                // We break the loop here so that the totalInertia value is
                // completely calculated (by both iterations) before
                // continuing.
            }

        // Loop through again calculating and applying the changes
        for (int i = 0; i < 2; i++)
            if (body[i] != null) {
                // The linear and angular movements required are in proportion to
                // the two inverse inertias.
                double sign = (i == 0) ? 1 : -1;
                angularMove[i] = sign * penetration * (angularInertia[i] / totalInertia);
                linearMove[i] = sign * penetration * (linearInertia[i] / totalInertia);

                // To avoid angular projections that are too great (when mass is large
                // but inertia tensor is small) limit the angular move.
                Vector3 projection = relativeContactPosition[i];
                projection.addScaledVector(contactNormal, -relativeContactPosition[i].scalarProduct(contactNormal));

                // Use the small angle approximation for the sine of the angle (i.e.
                // the magnitude would be sine(angularLimit) * projection.magnitude
                // but we approximate sine(angularLimit) to angularLimit).
                double maxMagnitude = angularLimit * projection.magnitude();

                if (angularMove[i] < -maxMagnitude) {
                    double totalMove = angularMove[i] + linearMove[i];
                    angularMove[i] = -maxMagnitude;
                    linearMove[i] = totalMove - angularMove[i];
                } else if (angularMove[i] > maxMagnitude) {
                    double totalMove = angularMove[i] + linearMove[i];
                    angularMove[i] = maxMagnitude;
                    linearMove[i] = totalMove - angularMove[i];
                }

                // We have the linear amount of movement required by turning
                // the rigid body (in angularMove[i]). We now need to
                // calculate the desired rotation to achieve that.
                if (angularMove[i] == 0) {
                    // Easy case - no angular movement means no rotation.
                    angularChange[i].clear();
                } else {
                    // Work out the direction we'd like to rotate in.
                    Vector3 targetAngularDirection = relativeContactPosition[i].vectorProduct(contactNormal);

                    Matrix3 inverseInertiaTensor = body[i].getInverseInertiaTensorWorld();

                    // Work out the direction we'd need to rotate to achieve that
                    angularChange[i] = inverseInertiaTensor.transform(targetAngularDirection).mult(angularMove[i] / angularInertia[i]);
                }

                // Velocity change is easier - it is just the linear movement
                // along the contact normal.
                linearChange[i] = contactNormal.mult(linearMove[i]);

                // Now we can start to apply the values we've calculated.
                // Apply the linear movement
                Vector3 pos = body[i].getPosition();
                pos.addScaledVector(contactNormal, linearMove[i]);
                body[i].setPosition(pos);

                // And the change in orientation
                Quaternion q = body[i].getOrientation();
                q.addScaledVector(angularChange[i], (1.0));
                body[i].setOrientation(q);

                // We need to calculate the derived data for any body that is
                // asleep, so that the changes are reflected in the object's
                // data. Otherwise the resolution will not change the position
                // of the object, and the next collision detection round will
                // have the same penetration.
                if (!body[i].getAwake()) body[i].calculateDerivedData();
            }
    }

    /**
     * Calculates the impulse needed to resolve this contact,
     * given that the contact has no friction. A pair of inertia
     * tensors - one for each contact object - is specified to
     * save calculation time: the calling function has access to
     * these anyway.
     */
    protected Vector3 calculateFrictionlessImpulse(Matrix3[] inverseInertiaTensor) {

        // Build a vector that shows the change in velocity in
        // world space for a unit impulse in the direction of the contact
        // normal.
        Vector3 deltaVelWorld = relativeContactPosition[0].rest(contactNormal);
        deltaVelWorld = inverseInertiaTensor[0].transform(deltaVelWorld);
        deltaVelWorld = deltaVelWorld.rest(relativeContactPosition[0]);

        // Work out the change in velocity in contact coordiantes.
        double deltaVelocity = deltaVelWorld.mult(contactNormal);

        // Add the linear component of velocity change
        deltaVelocity += body[0].getInverseMass();

        // Check if we need to the second body's data
        if (body[1] != null) {
            // Go through the same transformation sequence again
            deltaVelWorld = relativeContactPosition[1].rest(contactNormal);
            deltaVelWorld = inverseInertiaTensor[1].transform(deltaVelWorld);
            deltaVelWorld = deltaVelWorld.rest(relativeContactPosition[1]);

            // Add the change in velocity due to rotation
            deltaVelocity += deltaVelWorld.mult(contactNormal);

            // Add the change in velocity due to linear motion
            deltaVelocity += body[1].getInverseMass();
        }

        Vector3 impulseContact = new Vector3();
        // Calculate the required size of the impulse
        impulseContact.setX(desiredDeltaVelocity / deltaVelocity);
        impulseContact.setY(0);
        impulseContact.setZ(0);
        return impulseContact;
    }

    /**
     * Calculates the impulse needed to resolve this contact,
     * given that the contact has a non-zero coefficient of
     * friction. A pair of inertia tensors - one for each contact
     * object - is specified to save calculation time: the calling
     * function has access to these anyway.
     */
    protected Vector3 calculateFrictionImpulse(Matrix3[] inverseInertiaTensor) {
        Vector3 impulseContact;
        double inverseMass = body[0].getInverseMass();

        // The equivalent of a cross product in matrices is multiplication
        // by a skew symmetric matrix - we build the matrix for converting
        // between linear and angular quantities.
        Matrix3 impulseToTorque = new Matrix3();
        impulseToTorque.setSkewSymmetric(relativeContactPosition[0]);

        // Build the matrix to convert contact impulse to change in velocity
        // in world coordinates.
        Matrix3 deltaVelWorld = new Matrix3(impulseToTorque);
        deltaVelWorld.multToMe(inverseInertiaTensor[0]);
        deltaVelWorld.multToMe(impulseToTorque);
        deltaVelWorld.multToMe(-1);

        // Check if we need to add body two's data
        if (body[1] != null) {
            // Set the cross product matrix
            impulseToTorque.setSkewSymmetric(relativeContactPosition[1]);

            // Calculate the velocity change matrix
            Matrix3 deltaVelWorld2 = impulseToTorque;
            deltaVelWorld2.multToMe(inverseInertiaTensor[1]);
            deltaVelWorld2.multToMe(impulseToTorque);
            deltaVelWorld2.multToMe(-1);

            // Add to the total delta velocity.
            deltaVelWorld.sumToMe(deltaVelWorld2);

            // Add to the inverse mass
            inverseMass += body[1].getInverseMass();
        }

        // Do a change of basis to convert into contact coordinates.
        Matrix3 deltaVelocity = contactToWorld.transpose();
        deltaVelocity.multToMe(deltaVelWorld);
        deltaVelocity.multToMe(contactToWorld);

        // Add in the linear velocity change
        deltaVelocity.data[0] += inverseMass;
        deltaVelocity.data[4] += inverseMass;
        deltaVelocity.data[8] += inverseMass;

        // Invert to get the impulse needed per unit velocity
        Matrix3 impulseMatrix = deltaVelocity.inverse();

        // Find the target velocities to kill
        Vector3 velKill = new Vector3(desiredDeltaVelocity, -contactVelocity.getY(), -contactVelocity.getZ());

        // Find the impulse to kill target velocities
        impulseContact = impulseMatrix.transform(velKill);

        // Check for exceeding friction
        double planarImpulse = Math.sqrt(impulseContact.getY() * impulseContact.getY() + impulseContact.getZ() * impulseContact.getZ());
        if (planarImpulse > impulseContact.getX() * friction) {
            // We need to use dynamic friction
            impulseContact.setY(impulseContact.getY() / planarImpulse);
            impulseContact.setZ(impulseContact.getZ() / planarImpulse);

            impulseContact.setX(deltaVelocity.data[0] + deltaVelocity.data[1] * friction * impulseContact.getY() + deltaVelocity.data[2] * friction * impulseContact.getZ());
            impulseContact.setX(desiredDeltaVelocity / impulseContact.getX());
            impulseContact.setY(impulseContact.getY() * friction * impulseContact.getX());
            impulseContact.setZ(impulseContact.getZ() * friction * impulseContact.getX());
        }
        return impulseContact;
    }

}
