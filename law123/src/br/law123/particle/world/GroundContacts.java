package br.law123.particle.world;

import java.util.List;

import br.law123.core.Core;
import br.law123.particle.Particle;
import br.law123.particle.contact.ParticleContact;

/**
 * A contact generator that takes an STL vector of particle pointers and
 * collides them against the ground.
 */
public class GroundContacts //implements ParticleContactGenerator
{

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

    public int addContact(List<ParticleContact> contacts, int limit) {
        int count = 0;
        ParticleContact contact = contacts.get(count);
        for (Particle p : particles) {

            double y = p.getPosition().getY();
            if (y < 0.0f) {
                contact.setContactNormal(Core.UP);
                contact.getParticle()[0] = p;
                contact.getParticle()[1] = null;
                contact.setPenetration(-y);
                contact.setRestitution(0.2f);

                count++;
                contact = contacts.get(count);
            }

            if (count >= limit) return count;
        }
        return count;
    }
}
