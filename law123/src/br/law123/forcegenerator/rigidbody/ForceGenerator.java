package br.law123.forcegenerator.rigidbody;

import br.law123.rigidbody.RigidBody;

/**
 * A force generator can be asked to add a force to one or more
 * bodies.
 */
public interface ForceGenerator {

    /**
     * Overload this in implementations of the interface to calculate
     * and update the force applied to the given rigid body.
     */
    void updateForce(RigidBody particle);

    /**
     * Overload this in implementations of the interface to calculate
     * and update the force applied to the given rigid body.
     */
    void updateForce(RigidBody body, double duration);
}
