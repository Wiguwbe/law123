package br.law123.particle.contact;

import br.law123.core.Vector3;

/**
 * The contact resolution routine for particle contacts. One resolver instance
 * can be shared for the whole simulation.
 */
public class ParticleContactResolver {

    /**
     * Holds the number of iterations allowed.
     */
    private int iterations;

    /**
     * This is a performance tracking value - we keep a record of the actual
     * number of iterations used.
     */
    private int iterationsUsed;

    /**
     * Creates a new contact resolver.
     */
    public ParticleContactResolver(int iterations) {
        this.iterations = iterations;
    }

    /**
     * Sets the number of iterations that can be used.
     */
    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    /**
     * Resolves a set of particle contacts for both penetration and velocity.
     * 
     * Contacts that cannot interact with each other should be passed to
     * separate calls to resolveContacts, as the resolution algorithm takes much
     * longer for lots of contacts than it does for the same number of contacts
     * in small sets.
     * 
     * @param contactArray
     *            Pointer to an array of particle contact objects.
     * 
     * @param numContacts
     *            The number of contacts in the array to resolve.
     * 
     * @param numIterations
     *            The number of iterations through the resolution algorithm.
     *            This should be at least the number of contacts (otherwise some
     *            constraints will not be resolved - although sometimes this is
     *            not noticable). If the iterations are not needed they will not
     *            be used, so adding more iterations may not make any
     *            difference. But in some cases you would need millions of
     *            iterations. Think about the number of iterations as a bound:
     *            if you specify a large number, sometimes the algorithm WILL
     *            use it, and you may drop frames.
     * 
     * @param duration
     *            The duration of the previous integration step. This is used to
     *            compensate for forces applied.
     */
    public void resolveContacts(ParticleContact[] contactArray, int numContacts, double duration) {
        int i;

        iterationsUsed = 0;
        while (iterationsUsed < iterations) {
            // Find the contact with the largest closing velocity;
            double max = Double.MAX_VALUE;
            int maxIndex = numContacts;
            for (i = 0; i < numContacts; i++) {
                double sepVel = contactArray[i].calculateSeparatingVelocity();
                if (sepVel < max && (sepVel < 0 || contactArray[i].getPenetration() > 0)) {
                    max = sepVel;
                    maxIndex = i;
                }
            }

            // Do we have anything worth resolving?
            if (maxIndex == numContacts) break;

            // Resolve this contact
            contactArray[maxIndex].resolve(duration);

            // Update the interpenetrations for all particles
            Vector3[] move = contactArray[maxIndex].getParticleMovement();
            for (i = 0; i < numContacts; i++) {
                if (contactArray[i].getParticle()[0] == contactArray[maxIndex].getParticle()[0]) {
                    contactArray[i].setPenetration(contactArray[i].getPenetration() - move[0].mult(contactArray[i].getContactNormal()));
                } else if (contactArray[i].getParticle()[0] == contactArray[maxIndex].getParticle()[1]) {
                    contactArray[i].setPenetration(contactArray[i].getPenetration() - move[1].mult(contactArray[i].getContactNormal()));
                }
                if (contactArray[i].getParticle()[1] != null) {
                    if (contactArray[i].getParticle()[1] == contactArray[maxIndex].getParticle()[0]) {
                        contactArray[i].setPenetration(contactArray[i].getPenetration() + move[0].mult(contactArray[i].getContactNormal()));
                    } else if (contactArray[i].getParticle()[1] == contactArray[maxIndex].getParticle()[1]) {
                        contactArray[i].setPenetration(contactArray[i].getPenetration() + move[1].mult(contactArray[i].getContactNormal()));
                    }
                }
            }

            iterationsUsed++;
        }
    }
}
