package br.law123.force;

import br.law123.core.Vector3;
import br.law123.rigidbody.RigidBody;

/**
 * Gerador de força que aplica a força de flutuação sobre corpos rígidos.
 */
class Buoyancy implements Force {

    private double maxDepth;
    private double volume;

    private double waterHeight;
    private double liquidDensity;
    private Vector3 centreOfBuoyancy;

    public Buoyancy(Vector3 cOfB, double maxDepth, double volume, double waterHeight) {
        this(cOfB, maxDepth, volume, waterHeight, 1000.0f);
    }

    /**
     * Construtor.
     * </br></br></br>
     * Água pura tem 1000kg por metro cúbico de densidade.
     * 
     * @param cOfB centro de flutuação do corpo, cordenadas baseadas no corpo.
     * @param maxDepth o máximo de submerção do corpo antes que gere o máximo de força de flutução.
     * @param volume volume do corpo.
     * @param waterHeight altura do plano XZ da água.
     * @param liquidDensity densidade do líquido.
     */
    public Buoyancy(Vector3 cOfB, double maxDepth, double volume, double waterHeight, double liquidDensity) {
        this.centreOfBuoyancy = cOfB;
        this.liquidDensity = liquidDensity;
        this.maxDepth = maxDepth;
        this.volume = volume;
        this.waterHeight = waterHeight;
    }

    @Override
    public void updateForce(RigidBody body, double duration) {
        // Calculate the submersion depth
        Vector3 pointInWorld = body.getPointInWorldSpace(centreOfBuoyancy);
        double depth = pointInWorld.getY();

        // Check if we're out of the water
        if (depth >= waterHeight + maxDepth) return;
        Vector3 force = new Vector3(0, 0, 0);

        // Check if we're at maximum depth
        if (depth <= waterHeight - maxDepth) {
            force.setY(liquidDensity * volume);
            body.addForceAtBodyPoint(force, centreOfBuoyancy);
            return;
        }

        // Otherwise we are partly submerged
        force.setY(liquidDensity * volume * (depth - maxDepth - waterHeight) / 2 * maxDepth);
        body.addForceAtBodyPoint(force, centreOfBuoyancy);
    }

}
