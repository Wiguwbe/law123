package br.law123.force;

import br.law123.rigidbody.RigidBody;

/**
 * Defini��o para for�a.
 * 
 * @author teixeira
 */
public interface Force {

    /**
     * Aplica for�a sobre o corpo.
     * 
     * @param body corpo sobre qual a for�a deve ser aplicada.
     * 
     * @param duration delta T.
     */
    void updateForce(RigidBody body, double duration);

}
