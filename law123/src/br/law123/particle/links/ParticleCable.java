package br.law123.particle.links;

import br.law123.core.Vector3;
import br.law123.particle.contact.ParticleContact;

/**
 * Cables link a pair of particles, generating a contact if they
 * stray too far apart.
 */
public class ParticleCable extends ParticleLink {

    /**
     * Holds the maximum length of the cable.
     */
    double maxLength;

    /**
     * Holds the restitution (bounciness) of the cable.
     */
    double restitution;

    public double getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(double maxLength) {
        this.maxLength = maxLength;
    }

    public double getRestitution() {
        return restitution;
    }

    public void setRestitution(double restitution) {
        this.restitution = restitution;
    }

    /**
     * Fills the given contact structure with the contact needed
     * to keep the cable from over-extending.
     */
    @Override
    public int addContact(ParticleContact contact, int limit) {
        // Find the length of the cable
        double length = currentLength();

        // Check if we're over-extended
        if (length < maxLength) {
            return 0;
        }

        // Otherwise return the contact
        contact.getParticle()[0] = particle[0];
        contact.getParticle()[1] = particle[1];

        // Calculate the normal
        Vector3 normal = particle[1].getPosition().sub(particle[0].getPosition());
        normal.normalise();
        contact.setContactNormal(normal);

        contact.setPenetration(length - maxLength);
        contact.setRestitution(restitution);

        return 1;
    }
}
