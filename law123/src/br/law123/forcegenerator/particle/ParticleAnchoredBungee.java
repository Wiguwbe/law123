package br.law123.forcegenerator.particle;

import br.law123.core.Vector3;
import br.law123.particle.Particle;

/**
 * A force generator that applies a bungee force, where
 * one end is attached to a fixed point in space.
 */
class ParticleAnchoredBungee extends ParticleAnchoredSpring {

    /** Applies the spring force to the given particle. */
    @Override
    public void updateForce(Particle particle, double duration) {
        // Calculate the vector of the spring
        Vector3 force = new Vector3();
        particle.getPosition(force);
        force.subToMe(anchor);

        // Calculate the magnitude of the force
        double magnitude = force.magnitude();
        if (magnitude < restLength) return;

        magnitude = magnitude - restLength;
        magnitude *= springConstant;

        // Calculate the final force and apply it
        force.normalise();
        force.multToMe(-magnitude);
        particle.addForce(force);
    }

}
