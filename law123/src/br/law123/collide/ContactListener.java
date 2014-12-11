package br.law123.collide;

import br.law123.rigidbody.contact.Contact;


/**
 * Listener to contact events.
 * 
 * @author teixeira
 */
public interface ContactListener {
    
    /**
     * Invoked on contact.
     * 
     * @param contact contact data.
     */
    void bindContact(Contact contact);

}
