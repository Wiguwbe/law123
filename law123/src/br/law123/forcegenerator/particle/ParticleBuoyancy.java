package br.law123.forcegenerator.particle;

import br.law123.core.Vector3;
import br.law123.particle.Particle;

/**
 * A force generator that applies a buoyancy force for a plane of
 * liquid parrallel to XZ plane.
 */
class ParticleBuoyancy extends BaseParticleForceGenerator {

    /**
     * The maximum submersion depth of the object before
     * it generates its maximum boyancy force.
     */
    private double maxDepth;

    /**
     * The volume of the object.
     */
    private double volume;

    /**
     * The height of the water plane above y=0. The plane will be
     * parrallel to the XZ plane.
     */
    private double waterHeight;

    /**
     * The density of the liquid. Pure water has a density of
     * 1000kg per cubic meter.
     */
    private double liquidDensity;

    /** Creates a new buoyancy force with the given parameters. */
    public ParticleBuoyancy(double maxDepth, double volume, double waterHeight) {
        this(maxDepth, volume, waterHeight, 1000.0f);
    }

    public ParticleBuoyancy(double maxDepth, double volume, double waterHeight, double liquidDensity) {
        super();
        this.maxDepth = maxDepth;
        this.volume = volume;
        this.waterHeight = waterHeight;
        this.liquidDensity = liquidDensity;
    }

    /** Applies the buoyancy force to the given particle. */
    @Override
    public void updateForce(Particle particle, double duration) {
        // Calculate the submersion depth
        double depth = particle.getPosition().getY();

        // Check if we're out of the water
        if (depth >= waterHeight + maxDepth) return;
        Vector3 force = new Vector3(0, 0, 0);

        // Check if we're at maximum depth
        if (depth <= waterHeight - maxDepth) {
            force.setY(liquidDensity * volume);
            particle.addForce(force);
            return;
        }

        // Otherwise we are partly submerged
        force.setY(liquidDensity * volume * (depth - maxDepth - waterHeight) / 2 * maxDepth);
        particle.addForce(force);
    }
}
