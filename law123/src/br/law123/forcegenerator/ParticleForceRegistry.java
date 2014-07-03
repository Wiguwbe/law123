package br.law123.forcegenerator;

import java.util.ArrayList;
import java.util.List;

import br.law123.forcegenerator.particle.ParticleForceGenerator;
import br.law123.particle.Particle;


/**
 * Holds all the force generators and the particles they apply to.
 */
public class ParticleForceRegistry
{

    /**
     * Holds the list of registrations.
     */
    protected final List<ParticleForceRegistration> registrations = new ArrayList<ParticleForceRegistration>();

    /**
     * Registers the given force generator to apply to the
     * given particle.
     */
    void add(Particle particle, ParticleForceGenerator fg){
        registrations.add(new ParticleForceRegistration(particle, fg));
    }

    /**
     * Removes the given registered pair from the registry.
     * If the pair is not registered, this method will have
     * no effect.
     */
    public void remove(Particle particle, ParticleForceGenerator fg) {
        ParticleForceRegistration remove = null;
        for (ParticleForceRegistration i : registrations) {
            if (i.getParticle().equals(particle) && i.getFg().equals(fg)) {
                remove = i;
                break;
            }
        }
        if (remove != null) {
            registrations.remove(remove);
        }
    }

    /**
     * Clears all registrations from the registry. This will
     * not delete the particles or the force generators
     * themselves, just the records of their connection.
     */
    public void clear() {
        registrations.clear();
    }

    /**
     * Calls all the force generators to update the forces of
     * their corresponding particles.
     */
    public void updateForces(double duration){
        for (ParticleForceRegistration i : registrations) {
            i.getFg().updateForce(i.getParticle(), duration);
        }
    }
}