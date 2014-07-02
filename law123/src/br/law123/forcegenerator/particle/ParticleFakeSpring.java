package br.law123.forcegenerator.particle;

import br.law123.core.Vector3;
import br.law123.particle.Particle;

/**
 * A force generator that fakes a stiff spring force, and where
 * one end is attached to a fixed point in space.
 */
class ParticleFakeSpring extends BaseParticleForceGenerator {

    /** The location of the anchored end of the spring. */
    private Vector3 anchor;

    /** Holds the sprint constant. */
    private double springConstant;

    /** Holds the damping on the oscillation of the spring. */
    private double damping;

    /** Creates a new spring with the given parameters. */

    /** Applies the spring force to the given particle. */
    public ParticleFakeSpring(Vector3 anchor, double springConstant, double damping) {
        this.anchor = anchor;
        this.springConstant = springConstant;
        this.damping = damping;
    }

    @Override
    public void updateForce(Particle particle, double duration) {
        // Check that we do not have infinite mass
        if (!particle.hasFiniteMass()) return;

        // Calculate the relative position of the particle to the anchor
        Vector3 position = new Vector3();
        particle.getPosition(position);
        position.subToMe(anchor);

        // Calculate the constants and check they are in bounds.
        double gamma = 0.5f * Math.sqrt(4 * springConstant - damping * damping);
        if (gamma == 0.0f) return;
        Vector3 c = position.mult((damping / (2.0f * gamma))).sum(particle.getVelocity().mult(1.0f / gamma));

        // Calculate the target position
        Vector3 target = position.mult(Math.cos(gamma * duration)).sum(c.mult(Math.sin(gamma * duration))); //TODO: verificar com formula
        target.multToMe(Math.exp(-0.5f * duration * damping));

        // Calculate the resulting acceleration and therefore the force
        Vector3 accel = (target.sub(position)).mult(1.0 / (duration * duration)).sub(particle.getVelocity().mult(1.0 / duration));
        particle.addForce(accel.mult(particle.getMass()));
    }

}
