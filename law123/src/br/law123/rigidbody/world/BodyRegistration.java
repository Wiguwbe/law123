package br.law123.rigidbody.world;

import br.law123.rigidbody.RigidBody;

class BodyRegistration {

    private RigidBody body;
    private BodyRegistration next;

    public BodyRegistration(RigidBody body, BodyRegistration next) {
        this.body = body;
        this.next = next;
    }

    public RigidBody getBody() {
        return body;
    }

    public void setBody(RigidBody body) {
        this.body = body;
    }

    public BodyRegistration getNext() {
        return next;
    }

    public void setNext(BodyRegistration next) {
        this.next = next;
    }

}
