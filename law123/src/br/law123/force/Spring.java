package br.law123.force;

import br.law123.core.Vector3;
import br.law123.rigidbody.RigidBody;

/**
 * Gerador de força que aplica a força de mola sobre corpos rígidos.
 */
public class Spring implements Force {

    private Vector3 connectionPoint;
    private Vector3 otherConnectionPoint;

    private RigidBody other;

    private double springConstant;
    private double restLength;

    /**
     * Construtor.
     * 
     * @param localConnectionPt ponto de conexão da mola, cordenadas locais.
     * @param other corpo para usar a mola.
     * @param otherConnectionPt ponto de conexão da mola com o outro objeto, cordenadas baseadas no objeto.
     * @param springConstant constante da mola.
     * @param restLength constante de descanso.
     */
    public Spring(Vector3 localConnectionPt, RigidBody other, Vector3 otherConnectionPt, double springConstant, double restLength) {
        this.connectionPoint = localConnectionPt;
        this.otherConnectionPoint = otherConnectionPt;
        this.other = other;
        this.springConstant = springConstant;
        this.restLength = restLength;
    }

    @Override
    public void updateForce(RigidBody body, double duration) {
        // Calculate the two ends in world space
        Vector3 lws = body.getPointInWorldSpace(connectionPoint);
        Vector3 ows = other.getPointInWorldSpace(otherConnectionPoint);

        // Calculate the vector of the spring
        Vector3 force = lws.sub(ows);

        // Calculate the magnitude of the force
        double magnitude = force.magnitude();
        magnitude = Math.abs(magnitude - restLength);
        magnitude *= springConstant;

        // Calculate the final force and apply it
        force.normalise();
        force.multToMe(-magnitude);
        body.addForceAtPoint(force, lws);
    }
}
