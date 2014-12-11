package br.law123.collide;

import java.util.ArrayList;
import java.util.List;

import br.law123.rigidbody.contact.Contact;

public class CollisionData {

    private final String id;

    private List<Contact> contacts = new ArrayList<Contact>();

    private final double friction;
    private final double restitution;
    private final double tolerance;

    private int maxContacts;
    private int apllyedContacts = 0;

    public CollisionData(double friction, double restitution, double tolerance, int maxContacts) {
        this("default", friction, restitution, tolerance, maxContacts);
    }

    public CollisionData(String id, double friction, double restitution, double tolerance, int maxContacts) {
        this.id = id;
        this.friction = friction;
        this.restitution = restitution;
        this.maxContacts = maxContacts;
        this.tolerance = tolerance;
    }

    public String getId() {
        return id;
    }

    public List<Contact> collectContacts() {
        List<Contact> a = this.contacts;
        this.contacts = new ArrayList<Contact>();
        return a;
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
        return apllyedContacts < maxContacts;
    }

    void addContact(Contact c) {
        contacts.add(c);
        this.apllyedContacts++;
    }

    public void reset(int maxContact) {
        this.maxContacts = maxContact;
        this.contacts = new ArrayList<Contact>();
        this.apllyedContacts = 0;
    }
}
