package br.law123.forcegenerator.rigidbody;

import br.law123.core.Matrix3;
import br.law123.core.Vector3;
import br.law123.rigidbody.RigidBody;

/**
 * A force generator with a control aerodynamic surface. This
 * requires three inertia tensors, for the two extremes and
 * 'resting' position of the control surface. The latter tensor is
 * the one inherited from the base class, the two extremes are
 * defined in this class.
 */
public class AeroControl extends Aero {

    /**
     * The aerodynamic tensor for the surface, when the control is at
     * its maximum value.
     */
    private Matrix3 maxTensor;

    /**
     * The aerodynamic tensor for the surface, when the control is at
     * its minimum value.
     */
    private Matrix3 minTensor;

    /**
     * The current position of the control for this surface. This
     * should range between -1 (in which case the minTensor value
     * is used), through 0 (where the base-class tensor value is
     * used) to +1 (where the maxTensor value is used).
     */
    private double controlSetting;

    /**
     * Calculates the final aerodynamic tensor for the current
     * control setting.
     */
    protected Matrix3 getTensor() {
        if (controlSetting <= -1.0f) {
            return minTensor;
        }
        if (controlSetting >= 1.0f) {
            return maxTensor;
        }
        if (controlSetting < 0) {
            return Matrix3.linearInterpolate(minTensor, tensor, controlSetting + 1.0f);
        }
        if (controlSetting > 0) {
            return Matrix3.linearInterpolate(tensor, maxTensor, controlSetting);
        }
        return tensor;
    }

    /**
     * Creates a new aerodynamic control surface with the given
     * properties.
     */
    public AeroControl(Matrix3 base, Matrix3 min, Matrix3 max, Vector3 position, Vector3 windspeed) {
        super(base, position, windspeed);
        this.minTensor = min;
        this.maxTensor = max;
        this.controlSetting = 0.0f;
    }

    /**
     * Sets the control position of this control. This * should
     * range between -1 (in which case the minTensor value is *
     * used), through 0 (where the base-class tensor value is used) *
     * to +1 (where the maxTensor value is used). Values outside that
     * range give undefined results.
     */
    public void setControl(double value) {
        controlSetting = value;
    }

    /**
     * Applies the force to the given rigid body.
     */
    @Override
    public void updateForce(RigidBody body, double duration) {
        updateForceFromTensor(body, duration, getTensor());
    }
}
