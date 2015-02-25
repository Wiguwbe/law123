package br.law123.force;

import br.law123.core.Vector3;
import br.law123.rigidbody.RigidBody;

/**
 * Gerador de for�a que aplica a for�a gravitacional sobre corpos r�gidos.
 * 
 * @author teixeira
 */
public class Gravity implements Force {

    private Vector3 gravity;

    /**
     * Construtor.
     * 
     * @param gravity acelera��o da gravidade.
     */
    public Gravity(Vector3 gravity) {
        this.gravity = gravity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateForce(RigidBody body, double duration) {
        // Check that we do not have infinite mass
        if (!body.hasFiniteMass()) {
            return;
        }

        // Apply the mass-scaled force to the body
        body.addForce(gravity.mult(body.getMass()));
    }
}
