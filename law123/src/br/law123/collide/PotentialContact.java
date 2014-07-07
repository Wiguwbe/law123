package br.law123.collide;

import br.law123.rigidbody.RigidBody;

/**
 * Stores a potential contact to check later.
 */
public class PotentialContact {

    /**
     * Holds the bodies that might be in contact.
     */
    private RigidBody[] body = new RigidBody[2];

    public PotentialContact(RigidBody[] body) {
        this.body = body;
    }

    public RigidBody[] getBody() {
        return body;
    }

    public void setBody(RigidBody[] body) {
        this.body = body;
    }

}
