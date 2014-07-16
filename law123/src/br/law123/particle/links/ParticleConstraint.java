package br.law123.particle.links;

import br.law123.core.Vector3;
import br.law123.particle.Particle;
import br.law123.particle.contact.ParticleContact;
import br.law123.particle.contact.ParticleContactGenerator;

/**
 * Constraints are just like links, except they connect a particle to
 * an immovable anchor point.
 */
class ParticleConstraint implements ParticleContactGenerator {

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

    /**
     * Geneates the contacts to keep this link from being
     * violated. This class can only ever generate a single
     * contact, so the pointer can be a pointer to a single
     * element, the limit parameter is assumed to be at least one
     * (zero isn't valid) and the return value is either 0, if the
     * cable wasn't over-extended, or one if a contact was needed.
     * 
     * NB: This method is declared in the same way (as pure
     * virtual) in the parent class, but is replicated here for
     * documentation purposes.
     */
    @Override
    public int addContact(ParticleContact contact, int limit) {
        return 0;
    }
}
