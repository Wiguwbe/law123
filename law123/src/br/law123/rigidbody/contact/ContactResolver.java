package br.law123.rigidbody.contact;

import java.util.List;

import br.law123.core.Vector3;

public class ContactResolver {

    private int velocityIterations;
    private int positionIterations;
    private double velocityEpsilon;

    private double positionEpsilon;

    private int velocityIterationsUsed;
    private int positionIterationsUsed;

    public ContactResolver(int iterations) {
        this(iterations, 0.01, 0.01);
    }

    public ContactResolver(int iterations, double velocityEpsilon, double positionEpsilon) {
        setIterations(iterations, iterations);
        setEpsilon(velocityEpsilon, positionEpsilon);
    }

    public ContactResolver(int velocityIterations, int positionIterations) {
        this(velocityIterations, positionIterations, 0.01, 0.01);
    }

    public ContactResolver(int velocityIterations, int positionIterations, double velocityEpsilon, double positionEpsilon) {
        setIterations(velocityIterations);
        setEpsilon(velocityEpsilon, positionEpsilon);
    }

    /**
     * Returns true if the resolver has valid settings and is ready to go.
     */
    boolean isValid() {
        return (velocityIterations > 0) && (positionIterations > 0) && (positionEpsilon >= 0.0f) && (positionEpsilon >= 0.0f);
    }

    void setIterations(int velocityIterations, int positionIterations) {
        this.velocityIterations = velocityIterations;
        this.positionIterations = positionIterations;
    }

    public void setIterations(int iterations) {
        setIterations(iterations, iterations);
    }

    public void setEpsilon(double velocityEpsilon, double positionEpsilon) {
        this.velocityEpsilon = velocityEpsilon;
        this.positionEpsilon = positionEpsilon;
    }

    public void resolveContacts(List<Contact> contacts, double duration) {
        // Make sure we have something to do.
        if (contacts.size() < 1) {
            return;
        }

        if (!isValid()) return;

        // Prepare the contacts for processing
        prepareContacts(contacts, duration);

        // Resolve the interpenetration problems with the contacts.
        adjustPositions(contacts, duration);

        // Resolve the velocity problems with the contacts.
        adjustVelocities(contacts, duration);
    }

    private void prepareContacts(List<Contact> contacts, double duration) {
        for (Contact contact : contacts) {
            contact.calculateInternals(duration);
        }
    }

    private void adjustVelocities(List<Contact> contacts, double duration) {
        Vector3[] velocityChange = { new Vector3(), new Vector3() };
        Vector3[] rotationChange = { new Vector3(), new Vector3() };
        Vector3 deltaVel = new Vector3();

        // iteratively handle impacts in order of severity.
        velocityIterationsUsed = 0;
        while (velocityIterationsUsed < velocityIterations) {
            // Find contact with maximum magnitude of probable velocity change.
            double max = velocityEpsilon;
            int index = contacts.size();
            for (int i = 0; i < contacts.size(); i++) {
                Contact contact = contacts.get(i);
                if (contact.desiredDeltaVelocity > max) {
                    max = contact.desiredDeltaVelocity;
                    index = i;
                }
            }
            if (index == contacts.size()) {
                break;
            }

            // Match the awake state at the contact
            Contact contact = contacts.get(index);
            contact.matchAwakeState();

            // Do the resolution on the contact that came out top.
            contact.applyVelocityChange(velocityChange, rotationChange);

            // With the change in velocity of the two bodies, the update of
            // contact velocities means that some of the relative closing
            // velocities need recomputing.
            for (int i = 0; i < contacts.size(); i++) {
                // Check each body in the contact
                for (int b = 0; b < 2; b++) {
                    Contact contacti = contacts.get(i);
                    if (contacti.getBody()[b] != null) {
                        // Check for a match with each body in the newly
                        // resolved contact
                        for (int d = 0; d < 2; d++) {
                            if (contacti.getBody()[b] == contact.getBody()[d]) {
                                deltaVel = velocityChange[d].sum(rotationChange[d].vectorProduct(contacti.relativeContactPosition[b]));

                                // The sign of the change is negative if we're dealing
                                // with the second body in a contact.
                                contacti.contactVelocity.sumToMe(contacti.getContactToWorld().transformTranspose(deltaVel).mult(b == 1 ? -1 : 1));
                                contacti.calculateDesiredDeltaVelocity(duration);
                            }
                        }
                    }
                }
            }
            velocityIterationsUsed++;
        }
    }

    /**
     * Resolves the positional issues with the given array of constraints,
     * using the given number of iterations.
     */
    private void adjustPositions(List<Contact> contacts, double duration) {
        int i;
        int index;
        Vector3[] linearChange = new Vector3[2];
        Vector3[] angularChange = { new Vector3(), new Vector3() };
        double max;
        Vector3 deltaPosition;

        // iteratively resolve interpenetrations in order of severity.
        positionIterationsUsed = 0;
        while (positionIterationsUsed < positionIterations) {
            // Find biggest penetration
            max = positionEpsilon;
            index = contacts.size();
            for (i = 0; i < contacts.size(); i++) {
                Contact contact = contacts.get(i);
                if (contact.getPenetration() > max) {
                    max = contact.getPenetration();
                    index = i;
                }
            }
            if (index == contacts.size()) {
                break;
            }

            // Match the awake state at the contact
            Contact contact = contacts.get(index);
            contact.matchAwakeState();

            // Resolve the penetration.
            contact.applyPositionChange(linearChange, angularChange, max);

            // Again this action may have changed the penetration of other
            // bodies, so we update contacts.
            for (i = 0; i < contacts.size(); i++) {
                // Check each body in the contact
                for (int b = 0; b < 2; b++)
                    if (contacts.get(i).getBody()[b] != null) {
                        // Check for a match with each body in the newly
                        // resolved contact
                        for (int d = 0; d < 2; d++) {
                            Contact contacti = contacts.get(i);
                            if (contacti.getBody()[b] == contact.getBody()[d]) {
                                deltaPosition = linearChange[d].sum(angularChange[d].vectorProduct(contacti.relativeContactPosition[b]));

                                // The sign of the change is positive if we're
                                // dealing with the second body in a contact
                                // and negative otherwise (because we're
                                // subtracting the resolution)..
                                contacti.setPenetration(contacti.getPenetration() + deltaPosition.scalarProduct(contacti.getContactNormal().mult(b == 1 ? 1 : -1)));
                            }
                        }
                    }
            }
            positionIterationsUsed++;
        }
    }

}
