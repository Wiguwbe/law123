package br.law123.forcegenerator.rigidbody;

import br.law123.core.Vector3;
import br.law123.rigidbody.RigidBody;

/**
 * A force generator to apply a buoyant force to a rigid body.
 */
class Buoyancy extends BaseForceGenerator {

    /**
     * The maximum submersion depth of the object before
     * it generates its maximum buoyancy force.
     */
    private double maxDepth;

    /**
     * The volume of the object.
     */
    private double volume;

    /**
     * The height of the water plane above y=0. The plane will be
     * parallel to the XZ plane.
     */
    private double waterHeight;

    /**
     * The density of the liquid. Pure water has a density of
     * 1000kg per cubic meter.
     */
    private double liquidDensity;

    /**
     * The centre of buoyancy of the rigid body, in body coordinates.
     */
    private Vector3 centreOfBuoyancy;

    public Buoyancy(Vector3 cOfB, double maxDepth, double volume, double waterHeight) {
        this(cOfB, maxDepth, volume, waterHeight, 1000.0f);
    }

    /** Creates a new buoyancy force with the given parameters. */
    public Buoyancy(Vector3 cOfB, double maxDepth, double volume, double waterHeight, double liquidDensity) {
        this.centreOfBuoyancy = cOfB;
        this.liquidDensity = liquidDensity;
        this.maxDepth = maxDepth;
        this.volume = volume;
        this.waterHeight = waterHeight;
    }

    /**
     * Applies the force to the given rigid body.
     */
    @Override
    public void updateForce(RigidBody body, double duration)

    {
        // Calculate the submersion depth
        Vector3 pointInWorld = body.getPointInWorldSpace(centreOfBuoyancy);
        double depth = pointInWorld.getY();

        // Check if we're out of the water
        if (depth >= waterHeight + maxDepth) return;
        Vector3 force = new Vector3(0, 0, 0);

        // Check if we're at maximum depth
        if (depth <= waterHeight - maxDepth) {
            force.setY(liquidDensity * volume);
            body.addForceAtBodyPoint(force, centreOfBuoyancy);
            return;
        }

        // Otherwise we are partly submerged
        force.setY(liquidDensity * volume * (depth - maxDepth - waterHeight) / 2 * maxDepth);
        body.addForceAtBodyPoint(force, centreOfBuoyancy);
    }

}
