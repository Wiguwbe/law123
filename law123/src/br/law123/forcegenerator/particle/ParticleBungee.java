package br.law123.forcegenerator.particle;

import br.law123.core.Vector3;
import br.law123.particle.Particle;

/**
 * A force generator that applies a spring force only
 * when extended.
 */
class ParticleBungee extends BaseParticleForceGenerator {

    /** The particle at the other end of the spring. */
    private Particle other;

    /** Holds the sprint constant. */
    private double springConstant;

    /**
     * Holds the length of the bungee at the point it begins to
     * generator a force.
     */
    private double restLength;

    /** Creates a new bungee with the given parameters. */
    public ParticleBungee(Particle other, double springConstant, double restLength) {
        this.other = other;
        this.springConstant = springConstant;
        this.restLength = restLength;
    }

    /** Applies the spring force to the given particle. */
    @Override
    public void updateForce(Particle particle, double duration) {
        // Calculate the vector of the spring
        Vector3 force = new Vector3();
        particle.getPosition(force);
        force.subToMe(other.getPosition());

        // Check if the bungee is compressed
        double magnitude = force.magnitude();
        if (magnitude <= restLength) return;

        // Calculate the magnitude of the force
        magnitude = springConstant * (restLength - magnitude);

        // Calculate the final force and apply it
        force.normalise();
        force.multToMe(-magnitude);
        particle.addForce(force);
    }

}
