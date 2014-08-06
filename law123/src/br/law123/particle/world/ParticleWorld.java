package br.law123.particle.world;

import java.util.ArrayList;
import java.util.List;

import br.law123.forcegenerator.ParticleForceRegistry;
import br.law123.particle.Particle;
import br.law123.particle.contact.ParticleContact;
import br.law123.particle.contact.ParticleContactGenerator;
import br.law123.particle.contact.ParticleContactResolver;

/**
 * Keeps track of a set of particles, and provides the means to
 * update them all.
 */
public class ParticleWorld {

    /**
     * Holds the particles
     */
    private List<Particle> particles = new ArrayList<Particle>();

    /**
     * True if the world should calculate the number of iterations
     * to give the contact resolver at each frame.
     */
    private boolean calculateIterations;

    /**
     * Holds the force generators for the particles in this world.
     */
    private ParticleForceRegistry registry = new ParticleForceRegistry();

    /**
     * Holds the resolver for contacts.
     */
    private ParticleContactResolver resolver;

    /**
     * Contact generators.
     */
    private List<ParticleContactGenerator> contactGenerators = new ArrayList<ParticleContactGenerator>();

    /**
     * Holds the list of contacts.
     */
    private ParticleContact[] contacts;

    /**
     * Holds the maximum number of contacts allowed (i.e. the
     * size of the contacts array).
     */
    private int maxContacts;

    /**
     * Creates a new particle simulator that can handle up to the
     * given number of contacts per frame. You can also optionally
     * give a number of contact-resolution iterations to use. If you
     * don't give a number of iterations, then twice the number of
     * contacts will be used.
     */
    public ParticleWorld(int maxContacts) {
        this(maxContacts, 0);
    }

    public ParticleWorld(int maxContacts, int iterations) {
        this.resolver = new ParticleContactResolver(iterations);
        this.maxContacts = maxContacts;
        contacts = new ParticleContact[maxContacts];
        for (int i = 0; i < contacts.length; i++) {
            contacts[i] = new ParticleContact();
        }
        calculateIterations = (iterations == 0);

    }

    /**
     * Calls each of the registered contact generators to report
     * their contacts. Returns the number of generated contacts.
     */
    public int generateContacts() {
        int limit = maxContacts;

        int i = 0;
        //ParticleContact nextContact = contacts[i];

        for (ParticleContactGenerator g : contactGenerators) {
            int used = g.addContact(contacts, i, limit);
            limit -= used;

            i += used;
            //nextContact = contacts[i];

            // We've run out of contacts to fill. This means we're missing
            // contacts.
            if (limit <= 0) break;
        }

        // Return the number of contacts used.
        return maxContacts - limit;
    }

    /**
     * Integrates all the particles in this world forward in time
     * by the given duration.
     */
    public void integrate(double duration) {
        for (Particle p : particles) {
            // Remove all forces from the accumulator
            p.integrate(duration);
        }
    }

    /**
     * Processes all the physics for the particle world.
     */
    public void runPhysics(double duration) {
        // First apply the force generators
        registry.updateForces(duration);

        // Then integrate the objects
        integrate(duration);

        // Generate contacts
        int usedContacts = generateContacts();

        // And process them
        if (usedContacts > 0) {
            if (calculateIterations) resolver.setIterations(usedContacts * 2);
            resolver.resolveContacts(contacts, usedContacts, duration);
        }
    }

    /**
     * Initializes the world for a simulation frame. This clears
     * the force accumulators for particles in the world. After
     * calling this, the particles can have their forces for this
     * frame added.
     */
    public void startFrame() {
        for (Particle p : particles) {
            // Remove all forces from the accumulator
            p.clearAccumulator();
        }
    }

    /**
     * Returns the list of particles.
     */

    public List<Particle> getParticles() {
        return particles;
    }

    /**
     * Returns the list of contact generators.
     */

    public List<ParticleContactGenerator> getContactGenerators() {
        return contactGenerators;
    }

    /**
     * Returns the force registry.
     * 
     * @return
     */
    public ParticleForceRegistry getForceRegistry() {
        return registry;
    }
}
