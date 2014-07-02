package br.law123.forcegenerator.particle;

import br.law123.core.Vector3;
import br.law123.particle.Particle;

/**
 * A force generator that applies a gravitational force. One instance
 * can be used for multiple particles.
 */
class ParticleGravity extends BaseParticleForceGenerator {

    /** Holds the acceleration due to gravity. */
    private Vector3 gravity;

    /** Creates the generator with the given acceleration. */
    public ParticleGravity(Vector3 gravity) {
        this.gravity = gravity;
    }

    /** Applies the gravitational force to the given particle. */
    @Override
    public void updateForce(Particle particle, double duration) {
        // Check that we do not have infinite mass
        if (!particle.hasFiniteMass()) return;

        // Apply the mass-scaled force to the particle
        particle.addForce(gravity.mult(particle.getMass()));
    }
}
