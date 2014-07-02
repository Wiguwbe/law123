package br.law123.forcegenerator.particle;

import br.law123.core.Vector3;
import br.law123.particle.Particle;

/**
 * A force generator that applies a Spring force.
 */
class ParticleSpring extends BaseParticleForceGenerator {

    /** The particle at the other end of the spring. */
    private Particle other;

    /** Holds the sprint constant. */
    private double springConstant;

    /** Holds the rest length of the spring. */
    private double restLength;

    /** Creates a new spring with the given parameters. */
    public ParticleSpring(Particle other, double springConstant, double restLength) {
        super();
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

        // Calculate the magnitude of the force
        double magnitude = force.magnitude();
        magnitude = Math.abs(magnitude - restLength);
        magnitude *= springConstant;

        // Calculate the final force and apply it
        force.normalise();
        force.multToMe(-magnitude);
        particle.addForce(force);
    }
}
