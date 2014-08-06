package br.law123.particle.world;

import java.util.List;

import br.law123.core.Core;
import br.law123.core.Vector3;
import br.law123.particle.Particle;
import br.law123.particle.contact.ParticleContact;
import br.law123.particle.contact.ParticleContactGenerator;

/**
 * A contact generator that takes an STL vector of particle pointers and
 * collides them against the ground.
 */
public class GroundContacts implements ParticleContactGenerator {

    private List<Particle> particles;

    public List<Particle> getParticles() {
        return particles;
    }

    public void setParticles(List<Particle> particles) {
        this.particles = particles;
    }

    public void init(List<Particle> aparticles) {
        this.particles = aparticles;
    }

    @Override
    public int addContact(ParticleContact[] contact, int offset, int limit) {
        int count = 0;
        int i = offset;
        for (Particle p : particles) {
            double y = p.getPosition().getY();
            if (y < 0.0f) {
                contact[i].setContactNormal(new Vector3(Core.UP));
                contact[i].getParticle()[0] = p;
                contact[i].getParticle()[1] = null;
                contact[i].setPenetration(-y);
                contact[i].setRestitution(0.2f);
                i++;
                count++;
            }

            if (count >= limit) return count;
        }
        return count;
    }
}
