package br.law123.forcegenerator.rigidbody;

import br.law123.core.Matrix3;
import br.law123.core.Quaternion;
import br.law123.core.Vector3;
import br.law123.rigidbody.RigidBody;

/**
 * A force generator with an aerodynamic surface that can be
 * re-oriented relative to its rigid body. This derives the
 */
public class AngledAero extends Aero {

    /**
     * Holds the orientation of the aerodynamic surface relative
     * to the rigid body to which it is attached.
     */
    Quaternion orientation;

    /**
     * Creates a new aerodynamic surface with the given properties.
     */
    public AngledAero(Matrix3 tensor, Vector3 position, Vector3 windspeed) {
        super(tensor, position, windspeed);
    }

    /**
     * Sets the relative orientation of the aerodynamic surface,
     * relative to the rigid body it is attached to. Note that
     * this doesn't affect the point of connection of the surface
     * to the body.
     */
    public void setOrientation(Quaternion quat) {

    }

    /**
     * Applies the force to the given rigid body.
     */
    @Override
    public void updateForce(RigidBody body, double duration) {

    }
}
