package br.law123.force;

import br.law123.rigidbody.RigidBody;

/**
 * Definição para força.
 * 
 * @author teixeira
 */
public interface Force {

    /**
     * Aplica força sobre o corpo.
     * 
     * @param body corpo sobre qual a força deve ser aplicada.
     * 
     * @param duration delta T.
     */
    void updateForce(RigidBody body, double duration);

}
