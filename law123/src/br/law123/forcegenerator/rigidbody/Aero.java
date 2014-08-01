package br.law123.forcegenerator.rigidbody;

import br.law123.core.Matrix3;
import br.law123.core.Vector3;
import br.law123.rigidbody.RigidBody;

/**
 * A force generator that applies an aerodynamic force.
 */
public class Aero extends BaseForceGenerator {

    /**
     * Holds the aerodynamic tensor for the surface in body
     * space.
     */
    protected Matrix3 tensor;

    /**
     * Holds the relative position of the aerodynamic surface in
     * body coordinates.
     */
    private Vector3 position;

    /**
     * Holds a pointer to a vector containing the windspeed of the
     * environment. This is easier than managing a separate
     * windspeed vector per generator and having to update it
     * manually as the wind changes.
     */
    private final Vector3 windspeed;

    /**
     * Creates a new aerodynamic force generator with the
     * given properties.
     */
    public Aero(Matrix3 tensor, Vector3 position, Vector3 windspeed) {
        this.tensor = tensor;
        this.position = position;
        this.windspeed = windspeed;
    }

    /**
     * Applies the force to the given rigid body.
     */
    @Override
    public void updateForce(RigidBody body, double duration) {
        updateForceFromTensor(body, duration, tensor);
    }

    /**
     * Uses an explicit tensor matrix to update the force on
     * the given rigid body. This is exactly the same as for updateForce
     * only it takes an explicit tensor.
     */
    protected void updateForceFromTensor(RigidBody body, double duration, Matrix3 tensor) { //TODO analisar
        // Calculate total velocity (windspeed and body's velocity).
        Vector3 velocity = body.getVelocity();
        velocity.sumToMe(windspeed);

        // Calculate the velocity in body coordinates
        Vector3 bodyVel = body.getTransform().transformInverseDirection(velocity);

        // Calculate the force in body coordinates
        Vector3 bodyForce = tensor.transform(bodyVel);
        Vector3 force = body.getTransform().transformDirection(bodyForce);

        // Apply the force
        body.addForceAtBodyPoint(force, position);
    }
}
