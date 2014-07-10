package br.law123.collide;

import br.law123.rigidbody.contact.Contact;

public class CollisionData {

    /**
     * Holds the base of the collision data: the first contact
     * in the array. This is used so that the contact pointer (below)
     * can be incremented each time a contact is detected, while
     * this pointer points to the first contact found.
     */
    private Contact[] contactArray;

    /** Holds the contact array to write into. */
    //private List<Contact> contacts;

    /** Holds the maximum number of contacts the array can take. */
    private int contactsLeft;

    /** Holds the number of contacts found so far. */
    private int contactCount;

    /** Holds the friction value to write into any collisions. */
    private double friction;

    /** Holds the restitution value to write into any collisions. */
    private double restitution;

    /**
     * Holds the collision tolerance, even uncolliding objects this
     * close should have collisions generated.
     */
    private double tolerance;

    public Contact[] getContactArray() {
        return contactArray;
    }

    public void setContactArray(Contact[] contactArray) {
        this.contactArray = contactArray;
    }

    public int getContactsLeft() {
        return contactsLeft;
    }

    public int getContactCount() {
        return contactCount;
    }

    public double getFriction() {
        return friction;
    }

    public void setFriction(double friction) {
        this.friction = friction;
    }

    public double getRestitution() {
        return restitution;
    }

    public void setRestitution(double restitution) {
        this.restitution = restitution;
    }

    public double getTolerance() {
        return tolerance;
    }

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    /**
     * Checks if there are more contacts available in the contact
     * data.
     */
    public boolean hasMoreContacts() {
        return contactsLeft > 0;
    }

    /**
     * Resets the data so that it has no used contacts recorded.
     */
    public void reset(int maxContacts) {
        contactsLeft = maxContacts;
        contactArray = new Contact[maxContacts];
        contactCount = 0;
        //contacts = contactArray;
    }

    /**
     * Notifies the data that the given number of contacts have
     * been added.
     */
    void addContacts(Contact c) {
        // Reduce the number of contacts remaining, add number used
        contactsLeft--;
        contactArray[contactCount] = c;
        contactCount++;

        // Move the array forward
        //contacts += count;
    }
}
