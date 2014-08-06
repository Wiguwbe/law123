package br.law123.particle.links;

import br.law123.core.Vector3;
import br.law123.particle.Particle;
import br.law123.particle.contact.ParticleContactGenerator;

/**
 * Constraints are just like links, except they connect a particle to
 * an immovable anchor point.
 */
abstract class ParticleConstraint implements ParticleContactGenerator {

    /**
     * Holds the particles connected by this constraint.
     */
    protected Particle particle;

    /**
     * The point to which the particle is anchored.
     */
    protected Vector3 anchor;

    public Particle getParticle() {
        return particle;
    }

    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    /**
     * Returns the current length of the link.
     */
    protected double currentLength() {
        Vector3 relativePos = particle.getPosition().sub(anchor);
        return relativePos.magnitude();
    }

    public Vector3 getAnchor() {
        return anchor;
    }

    public void setAnchor(Vector3 anchor) {
        this.anchor = anchor;
    }

}
