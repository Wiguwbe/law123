package br.law123.particle.links;

import br.law123.core.Vector3;
import br.law123.particle.Particle;
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
    protected Particle[] particle = { new Particle(), new Particle() };

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

}
