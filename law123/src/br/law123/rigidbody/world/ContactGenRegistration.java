package br.law123.rigidbody.world;

import br.law123.rigidbody.contact.ContactGenerator;

public class ContactGenRegistration {

    private ContactGenerator gen;
    private ContactGenRegistration next;

    public ContactGenRegistration(ContactGenerator gen, ContactGenRegistration next) {
        this.gen = gen;
        this.next = next;
    }

    public ContactGenerator getGen() {
        return gen;
    }

    public void setGen(ContactGenerator gen) {
        this.gen = gen;
    }

    public ContactGenRegistration getNext() {
        return next;
    }

    public void setNext(ContactGenRegistration next) {
        this.next = next;
    }

}
