package br.law123.rigidbody.world;

import br.law123.rigidbody.contact.Contact;
import br.law123.rigidbody.contact.ContactResolver;

/**
 * The world represents an independent simulation of physics. It
 * keeps track of a set of rigid bodies, and provides the means to
 * update them all.
 */
public class World {

    // ... other World data as before ...
    /**
     * True if the world should calculate the number of iterations
     * to give the contact resolver at each frame.
     */
    private boolean calculateIterations;

    /**
     * Holds a single rigid body in a linked list of bodies.
     */

    /**
     * Holds the head of the list of registered bodies.
     */
    private BodyRegistration firstBody;

    /**
     * Holds the resolver for sets of contacts.
     */
    private ContactResolver resolver;

    /**
     * Holds the head of the list of contact generators.
     */
    private ContactGenRegistration firstContactGen;

    /**
     * Holds an array of contacts, for filling by the contact
     * generators.
     */
    private Contact[] contacts;

    /**
     * Holds the maximum number of contacts allowed (i.e. the size
     * of the contacts array).
     */
    private int maxContacts;

    /**
     * Creates a new simulator that can handle up to the given
     * number of contacts per frame. You can also optionally give
     * a number of contact-resolution iterations to use. If you
     * don't give a number of iterations, then four times the
     * number of detected contacts will be used for each frame.
     */
    public World(int maxContacts, int iterations) {
        this.resolver = new ContactResolver(iterations);
        this.maxContacts = maxContacts;
        contacts = new Contact[maxContacts];
        calculateIterations = (iterations == 0);
    }

    /**
     * Calls each of the registered contact generators to report
     * their contacts. Returns the number of generated contacts.
     */
    int generateContacts() {
        int limit = maxContacts;

        int i = 0;
        Contact nextContact = contacts[i];

        ContactGenRegistration reg = firstContactGen;
        while (reg != null) {
            int used = reg.getGen().addContact(nextContact, limit);
            limit -= used;
            i += used;
            nextContact = contacts[i];

            // We've run out of contacts to fill. This means we're missing
            // contacts.
            if (limit <= 0) break;

            reg = reg.getNext();
        }

        // Return the number of contacts used.
        return maxContacts - limit;
    }

    /**
     * Processes all the physics for the world.
     */
    void runPhysics(double duration) {
        // First apply the force generators
        //registry.updateForces(duration);

        // Then integrate the objects
        BodyRegistration reg = firstBody;
        while (reg != null) {
            // Remove all forces from the accumulator
            reg.getBody().integrate(duration);

            // Get the next registration
            reg = reg.getNext();
        }

        // Generate contacts
        int usedContacts = generateContacts();

        // And process them
        if (calculateIterations) resolver.setIterations(usedContacts * 4);
        resolver.resolveContacts(contacts, usedContacts, duration);
    }

    /**
     * Initialises the world for a simulation frame. This clears
     * the force and torque accumulators for bodies in the
     * world. After calling this, the bodies can have their forces
     * and torques for this frame added.
     */
    public void startFrame() {
        BodyRegistration reg = firstBody;
        while (reg != null) {
            // Remove all forces from the accumulator
            reg.getBody().clearAccumulators();
            reg.getBody().calculateDerivedData();

            // Get the next registration
            reg = reg.getNext();
        }
    }

}
