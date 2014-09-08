package br.law123.collide;

import java.util.ArrayList;
import java.util.List;

import br.law123.rigidbody.contact.Contact;

public class CollisionData {

    private final List<Contact> contacts = new ArrayList<Contact>();

    private final double friction;
    private final double restitution;
    private final double tolerance;

    private final int maxContacts;

    public CollisionData(double friction, double restitution, double tolerance, int maxContacts) {
        this.friction = friction;
        this.restitution = restitution;
        this.maxContacts = maxContacts;
        this.tolerance = tolerance;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public double getFriction() {
        return friction;
    }

    public double getRestitution() {
        return restitution;
    }

    public double getTolerance() {
        return tolerance;
    }

    public boolean hasMoreContacts() {
        return contacts.size() < maxContacts;
    }

    void addContact(Contact c) {
        contacts.add(c);
    }
}
