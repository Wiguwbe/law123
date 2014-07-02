package br.law123.forcegenerator.particle;

import br.law123.core.Vector3;
import br.law123.particle.Particle;

/**
 * A force generator that applies a Spring force, where
 * one end is attached to a fixed point in space.
 */
class ParticleAnchoredSpring extends BaseParticleForceGenerator {

    /** The location of the anchored end of the spring. */
    protected Vector3 anchor;

    /** Holds the sprint constant. */
    protected double springConstant;

    /** Holds the rest length of the spring. */
    protected double restLength;

    ParticleAnchoredSpring() {
    }

    /** Creates a new spring with the given parameters. */
    public ParticleAnchoredSpring(Vector3 anchor, double springConstant, double restLength) {
        super();
        this.anchor = anchor;
        this.springConstant = springConstant;
        this.restLength = restLength;
    }

    /** Set the spring's properties. */
    @SuppressWarnings("hiding")
    public void init(Vector3 anchor, double springConstant, double restLength) {
        this.anchor = anchor;
        this.springConstant = springConstant;
        this.restLength = restLength;
    }

    /** Retrieve the anchor point. */
    public Vector3 getAnchor() {
        return anchor;
    }

    /** Set the spring's properties. */

    /** Applies the spring force to the given particle. */
    @Override
    public void updateForce(Particle particle, double duration) {
        // Calculate the vector of the spring
        Vector3 force = new Vector3();
        particle.getPosition(force);
        force.subToMe(anchor);

        // Calculate the magnitude of the force
        double magnitude = force.magnitude();
        magnitude = (restLength - magnitude) * springConstant;

        // Calculate the final force and apply it
        force.normalise();
        force.multToMe(magnitude);
        particle.addForce(force);
    }

}
