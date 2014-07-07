package br.law123.forcegenerator.rigidbody;

import br.law123.core.Vector3;
import br.law123.forcegenerator.particle.ParticleForceGenerator;
import br.law123.particle.Particle;
import br.law123.rigidbody.RigidBody;

/**
 * A force generator showing a three component explosion effect.
 * This force generator is intended to represent a single
 * explosion effect for multiple rigid bodies. The force generator
 * can also act as a particle force generator.
 */
public class Explosion extends BaseForceGenerator implements ParticleForceGenerator {

    /**
     * Tracks how long the explosion has been in operation, used
     * for time-sensitive effects.
     */
    private double timePassed;

    // Properties of the explosion, these are public because
    // there are so many and providing a suitable constructor
    // would be cumbersome:

    /**
     * The location of the detonation of the weapon.
     */
    private Vector3 detonation;

    // ... Other Explosion code as before ...

    /**
     * The radius up to which objects implode in the first stage
     * of the explosion.
     */
    private double implosionMaxRadius;

    /**
     * The radius within which objects don't feel the implosion
     * force. Objects near to the detonation aren't sucked in by
     * the air implosion.
     */
    private double implosionMinRadius;

    /**
     * The length of time that objects spend imploding before the
     * concussion phase kicks in.
     */
    private double implosionDuration;

    /**
     * The maximal force that the implosion can apply. This should
     * be relatively small to avoid the implosion pulling objects
     * through the detonation point and out the other side before
     * the concussion wave kicks in.
     */
    private double implosionForce;

    /**
     * The speed that the shock wave is traveling, this is related
     * to the thickness below in the relationship:
     * 
     * thickness >= speed * minimum frame duration
     */
    private double shockwaveSpeed;

    /**
     * The shock wave applies its force over a range of distances,
     * this controls how thick. Faster waves require larger
     * thicknesses.
     */
    private double shockwaveThickness;

    /**
     * This is the force that is applied at the very centre of the
     * concussion wave on an object that is stationary. Objects
     * that are in front or behind of the wavefront, or that are
     * already moving outwards, get proportionally less
     * force. Objects moving in towards the centre get
     * proportionally more force.
     */
    private double peakConcussionForce;

    /**
     * The length of time that the concussion wave is active.
     * As the wave nears this, the forces it applies reduces.
     */
    private double concussionDuration;

    /**
     * This is the peak force for stationary objects in
     * the centre of the convection chimney. Force calculations
     * for this value are the same as for peakConcussionForce.
     */
    private double peakConvectionForce;

    /**
     * The radius of the chimney cylinder in the xz plane.
     */
    private double chimneyRadius;

    /**
     * The maximum height of the chimney.
     */
    private double chimneyHeight;

    /**
     * The length of time the convection chimney is active. Typically
     * this is the longest effect to be in operation, as the heat
     * from the explosion outlives the shock wave and implosion
     * itself.
     */
    private double convectionDuration;

    /**
     * Creates a new explosion with sensible default values.
     */
    public Explosion() {

    }

    /**
     * Calculates and applies the force that the explosion
     * has on the given rigid body.
     */
    @Override
    public void updateForce(RigidBody body, double duration) {

    }

    /**
     * Calculates and applies the force that the explosion has
     * on the given particle.
     */
    @Override
    public void updateForce(Particle particle, double duration) {

    }

    /**
     * Calculates and applies the force that the explosion has
     * on the given particle.
     */
    @Override
    public void updateForce(Particle particle) {

    }

}
