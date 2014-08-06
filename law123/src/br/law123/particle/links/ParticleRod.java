package br.law123.particle.links;

import br.law123.core.Vector3;
import br.law123.particle.contact.ParticleContact;

/**
 * Rods link a pair of particles, generating a contact if they
 * stray too far apart or too close.
 */
public class ParticleRod extends ParticleLink {

    /**
     * Holds the length of the rod.
     */
    private double length;

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    /**
     * Fills the given contact structure with the contact needed
     * to keep the rod from extending or compressing.
     */
    @Override
    public int addContact(ParticleContact[] contacts, int offset, int limit) {
        // Find the length of the rod
        double currentLen = currentLength();

        // Check if we're over-extended
        if (currentLen == length) {
            return 0;
        }

        ParticleContact contact = contacts[offset];

        // Otherwise return the contact
        contact.getParticle()[0] = particle[0];
        contact.getParticle()[1] = particle[1];

        // Calculate the normal
        Vector3 normal = particle[1].getPosition().sub(particle[0].getPosition());
        normal.normalise();

        // The contact normal depends on whether we're extending or compressing
        if (currentLen > length) {
            contact.setContactNormal(normal);
            contact.setPenetration(currentLen - length);
        } else {
            contact.setContactNormal(normal.mult(-1));
            contact.setPenetration(length - currentLen);
        }

        // Always use zero restitution (no bounciness)
        contact.setRestitution(0);

        return 1;
    }
}
