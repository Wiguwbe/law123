package br.law123.rigidbody.contact;

/**
 * This is the basic polymorphic interface for contact generators
 * applying to rigid bodies.
 */
public interface ContactGenerator {

    /**
     * Fills the given contact structure with the generated
     * contact. The contact pointer should point to the first
     * available contact in a contact array, where limit is the
     * maximum number of contacts in the array that can be written
     * to. The method returns the number of contacts that have
     * been written.
     */
    int addContact(Contact contact, int limit);

    int addContact(Contact contact);
}
