package br.law123.forcegenerator.rigidbody;

import br.law123.core.Vector3;
import br.law123.rigidbody.RigidBody;

/**
 * A force generator that applies a Spring force.
 */
class Spring extends BaseForceGenerator {

    /**
     * The point of connection of the spring, in local
     * coordinates.
     */
    private Vector3 connectionPoint;

    /**
     * The point of connection of the spring to the other object,
     * in that object's local coordinates.
     */
    private Vector3 otherConnectionPoint;

    /** The particle at the other end of the spring. */
    private RigidBody other;

    /** Holds the sprint ant. */
    private double springConstant;

    /** Holds the rest length of the spring. */
    private double restLength;

    /** Creates a new spring with the given parameters. */
    public Spring(Vector3 localConnectionPt, RigidBody other, Vector3 otherConnectionPt, double springConstant, double restLength) {
        this.connectionPoint = localConnectionPt;
        this.otherConnectionPoint = otherConnectionPt;
        this.other = other;
        this.springConstant = springConstant;
        this.restLength = restLength;
    }

    /** Applies the spring force to the given rigid body. */
    @Override
    public void updateForce(RigidBody body, double duration) {
        // Calculate the two ends in world space
        Vector3 lws = body.getPointInWorldSpace(connectionPoint);
        Vector3 ows = other.getPointInWorldSpace(otherConnectionPoint);

        // Calculate the vector of the spring
        Vector3 force = lws.sub(ows);

        // Calculate the magnitude of the force
        double magnitude = force.magnitude();
        magnitude = Math.abs(magnitude - restLength);
        magnitude *= springConstant;

        // Calculate the final force and apply it
        force.normalise();
        force.multToMe(-magnitude);
        body.addForceAtPoint(force, lws);
    }
}
