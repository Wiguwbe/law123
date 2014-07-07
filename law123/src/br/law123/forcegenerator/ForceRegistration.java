package br.law123.forcegenerator;

import br.law123.forcegenerator.rigidbody.ForceGenerator;
import br.law123.rigidbody.RigidBody;

/**
 * Keeps track of one force generator and the body it
 * applies to.
 */
public class ForceRegistration {

    RigidBody body;
    ForceGenerator fg;

    public ForceRegistration(RigidBody body, ForceGenerator fg) {
        this.body = body;
        this.fg = fg;
    }

    public RigidBody getBody() {
        return body;
    }

    public void setBody(RigidBody body) {
        this.body = body;
    }

    public ForceGenerator getFg() {
        return fg;
    }

    public void setFg(ForceGenerator fg) {
        this.fg = fg;
    }

}
