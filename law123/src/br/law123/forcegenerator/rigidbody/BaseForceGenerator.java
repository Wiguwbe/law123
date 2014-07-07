package br.law123.forcegenerator.rigidbody;

import br.law123.rigidbody.RigidBody;

abstract class BaseForceGenerator implements ForceGenerator {

    @Override
    public void updateForce(RigidBody body) {
        updateForce(body, 0);
    }

}
