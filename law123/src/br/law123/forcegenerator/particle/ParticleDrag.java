package br.law123.forcegenerator.particle;

import br.law123.core.Vector3;
import br.law123.particle.Particle;

/**
 * A force generator that applies a drag force. One instance
 * can be used for multiple particles.
 */
class ParticleDrag extends BaseParticleForceGenerator {

    /** Holds the velocity drag coeffificent. */
    private double k1;

    /** Holds the velocity squared drag coeffificent. */
    private double k2;

    /** Creates the generator with the given coefficients. */

    public ParticleDrag(double k1, double k2) {
        this.k1 = k1;
        this.k2 = k2;
    }

    /** Applies the drag force to the given particle. */
    @Override
    public void updateForce(Particle particle, double duration) {
        Vector3 force = new Vector3();
        particle.getVelocity(force);

        // Calculate the total drag coefficient
        double dragCoeff = force.magnitude();
        dragCoeff = k1 * dragCoeff + k2 * dragCoeff * dragCoeff;

        // Calculate the final force and apply it
        force.normalise();
        force.multToMe(-dragCoeff);
        particle.addForce(force);
    }

}
