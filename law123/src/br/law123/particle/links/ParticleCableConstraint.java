package br.law123.particle.links;

import br.law123.core.Vector3;
import br.law123.particle.contact.ParticleContact;

/**
 * Cables link a particle to an anchor point, generating a contact if they
 * stray too far apart.
 */
public class ParticleCableConstraint extends ParticleConstraint {

    /**
     * Holds the maximum length of the cable.
     */
    private double maxLength;

    /**
     * Holds the restitution (bounciness) of the cable.
     */
    private double restitution;

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
    public int addContact(ParticleContact[] contacts, int offset, int limit) {
        // Find the length of the cable
        double length = currentLength();

        // Check if we're over-extended
        if (length < maxLength) {
            return 0;
        }

        ParticleContact contact = contacts[offset];

        // Otherwise return the contact
        contact.getParticle()[0] = particle;
        contact.getParticle()[1] = null;

        // Calculate the normal
        Vector3 normal = anchor.sub(particle.getPosition());
        normal.normalise();
        contact.setContactNormal(normal);

        contact.setPenetration(length - maxLength);
        contact.setRestitution(restitution);

        return 1;
    }
}
