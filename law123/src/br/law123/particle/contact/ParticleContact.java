package br.law123.particle.contact;

import br.law123.core.Vector3;
import br.law123.particle.Particle;

/**
 * A Contact represents two objects in contact (in this case ParticleContact
 * representing two Particles). Resolving a contact removes their
 * interpenetration, and applies sufficient impulse to keep them apart.
 * Colliding bodies may also rebound.
 * 
 * The contact has no callable functions, it just holds the contact details. To
 * resolve a set of contacts, use the particle contact resolver class.
 */
public class ParticleContact {

    // ... Other ParticleContact code as before ...

    /**
     * Holds the particles that are involved in the contact. The second of these
     * can be NULL, for contacts with the scenery.
     */
    private Particle[] particle = new Particle[2];

    /**
     * Holds the normal restitution coefficient at the contact.
     */
    private double restitution;

    /**
     * Holds the direction of the contact in world coordinates.
     */
    private Vector3 contactNormal;

    /**
     * Holds the depth of penetration at the contact.
     */
    private double penetration;

    /**
     * Holds the amount each particle is moved by during interpenetration
     * resolution.
     */
    private Vector3[] particleMovement = { new Vector3(), new Vector3() };

    public Particle[] getParticle() {
        return particle;
    }

    public void setRestitution(double restitution) {
        this.restitution = restitution;
    }

    public Vector3 getContactNormal() {
        return contactNormal;
    }

    public void setContactNormal(Vector3 contactNormal) {
        this.contactNormal = contactNormal;
    }

    public double getPenetration() {
        return penetration;
    }

    public void setPenetration(double penetration) {
        this.penetration = penetration;
    }

    public Vector3[] getParticleMovement() {
        return particleMovement;
    }

    /**
     * Resolves this contact, for both velocity and interpenetration.
     */
    protected void resolve(double duration) {
        resolveVelocity(duration);
        resolveInterpenetration(duration);
    }

    /**
     * Calculates the separating velocity at this contact.
     */
    protected double calculateSeparatingVelocity() {
        Vector3 relativeVelocity = new Vector3(particle[0].getVelocity());
        if (particle[1] != null) {
            relativeVelocity.subToMe(particle[1].getVelocity());
        }
        return relativeVelocity.mult(contactNormal);
    }

    /**
     * Handles the impulse calculations for this collision.
     */
    private void resolveVelocity(double duration) {
        // Find the velocity in the direction of the contact
        double separatingVelocity = calculateSeparatingVelocity();

        // Check if it needs to be resolved
        if (separatingVelocity > 0) {
            // The contact is either separating, or stationary - there's
            // no impulse required.
            return;
        }

        // Calculate the new separating velocity
        double newSepVelocity = -separatingVelocity * restitution;

        // Check the velocity build-up due to acceleration only
        Vector3 accCausedVelocity = new Vector3(particle[0].getAcceleration());
        if (particle[1] != null) {
            accCausedVelocity.subToMe(particle[1].getAcceleration());
        }
        double accCausedSepVelocity = accCausedVelocity.mult(contactNormal) * duration;

        // If we've got a closing velocity due to acceleration build-up,
        // remove it from the new separating velocity
        if (accCausedSepVelocity < 0) {
            newSepVelocity += restitution * accCausedSepVelocity;

            // Make sure we haven't removed more than was
            // there to remove.
            if (newSepVelocity < 0) newSepVelocity = 0;
        }

        double deltaVelocity = newSepVelocity - separatingVelocity;

        // We apply the change in velocity to each object in proportion to
        // their inverse mass (i.e. those with lower inverse mass [higher
        // actual mass] get less change in velocity)..
        double totalInverseMass = particle[0].getInverseMass();
        if (particle[1] != null) {
            totalInverseMass += particle[1].getInverseMass();
        }

        // If all particles have infinite mass, then impulses have no effect
        if (totalInverseMass <= 0) return;

        // Calculate the impulse to apply
        double impulse = deltaVelocity / totalInverseMass;

        // Find the amount of impulse per unit of inverse mass
        Vector3 impulsePerIMass = contactNormal.mult(impulse);

        // Apply impulses: they are applied in the direction of the contact,
        // and are proportional to the inverse mass.
        particle[0].setVelocity(particle[0].getVelocity().sum(impulsePerIMass.mult(particle[0].getInverseMass())));
        if (particle[1] != null) {
            // Particle 1 goes in the opposite direction
            particle[1].setVelocity(particle[1].getVelocity().sum(impulsePerIMass.mult(-particle[1].getInverseMass())));
        }
    }

    /**
     * Handles the interpenetration resolution for this contact.
     */
    private void resolveInterpenetration(double duration) {
        // If we don't have any penetration, skip this step.
        if (penetration <= 0) return;

        // The movement of each object is based on their inverse mass, so
        // total that.
        double totalInverseMass = particle[0].getInverseMass();
        if (particle[1] != null) {
            totalInverseMass += particle[1].getInverseMass();
        }

        // If all particles have infinite mass, then we do nothing
        if (totalInverseMass <= 0) return;

        // Find the amount of penetration resolution per unit of inverse mass
        Vector3 movePerIMass = contactNormal.mult((penetration / totalInverseMass));

        // Calculate the the movement amounts
        particleMovement[0] = movePerIMass.mult(particle[0].getInverseMass());
        if (particle[1] != null) {
            particleMovement[1] = movePerIMass.mult(-particle[1].getInverseMass());
        } else {
            particleMovement[1].clear();
        }

        // Apply the penetration resolution
        particle[0].setPosition(particle[0].getPosition().sum(particleMovement[0]));
        if (particle[1] != null) {
            particle[1].setPosition(particle[1].getPosition().sum(particleMovement[1]));
        }
    }

}
