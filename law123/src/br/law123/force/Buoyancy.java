package br.law123.force;

import br.law123.core.Vector3;
import br.law123.rigidbody.RigidBody;

/**
 * Gerador de for�a que aplica a for�a de flutua��o sobre corpos r�gidos.
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
     * �gua pura tem 1000kg por metro c�bico de densidade.
     * 
     * @param cOfB centro de flutua��o do corpo, cordenadas baseadas no corpo.
     * @param maxDepth o m�ximo de submer��o do corpo antes que gere o m�ximo de for�a de flutu��o.
     * @param volume volume do corpo.
     * @param waterHeight altura do plano XZ da �gua.
     * @param liquidDensity densidade do l�quido.
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
