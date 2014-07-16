package br.law123.particle.links;

import br.law123.core.Vector3;
import br.law123.particle.Particle;
import br.law123.particle.contact.ParticleContact;
import br.law123.particle.contact.ParticleContactGenerator;

/**
 * Links connect two particles together, generating a contact if
 * they violate the constraints of their link. It is used as a
 * base class for cables and rods, and could be used as a base
 * class for springs with a limit to their extension..
 */
public abstract class ParticleLink implements ParticleContactGenerator {

    /**
     * Holds the pair of particles that are connected by this link.
     */
    protected Particle[] particle = new Particle[2];

    public Particle[] getParticle() {
        return particle;
    }

    /**
     * Returns the current length of the link.
     */
    protected double currentLength() {
        Vector3 relativePos = particle[0].getPosition().sub(particle[1].getPosition());
        return relativePos.magnitude();
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
