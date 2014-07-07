package br.law123.forcegenerator.rigidbody;

import br.law123.core.Vector3;
import br.law123.rigidbody.RigidBody;

/**
 * A force generator that applies a gravitational force. One instance
 * can be used for multiple rigid bodies.
 */
class Gravity extends BaseForceGenerator {

    /** Holds the acceleration due to gravity. */
    private Vector3 gravity;

    /** Creates the generator with the given acceleration. */
    Gravity(Vector3 gravity) {
        this.gravity = gravity;
    }

    /** Applies the gravitational force to the given rigid body. */
    @Override
    public void updateForce(RigidBody body, double duration)

    {
        // Check that we do not have infinite mass
        if (!body.hasFiniteMass()) return;

        // Apply the mass-scaled force to the body
        body.addForce(gravity.mult(body.getMass()));
    }
}
