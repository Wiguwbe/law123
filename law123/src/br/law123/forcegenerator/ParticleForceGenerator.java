package br.law123.forcegenerator;

import br.law123.particle.Particle;

/**
 * A force generator can be asked to add a force to one or more
 * particles.
 */
public interface ParticleForceGenerator {

    /**
     * Overload this in implementations of the interface to calculate
     * and update the force applied to the given particle.
     */
    void updateForce(Particle particle, double duration);

    /**
     * Overload this in implementations of the interface to calculate
     * and update the force applied to the given particle.
     */
    void updateForce(Particle particle);
}
