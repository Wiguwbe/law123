package br.law123.forcegenerator.particle;

import br.law123.forcegenerator.ParticleForceGenerator;
import br.law123.particle.Particle;


abstract class BaseParticleForceGenerator implements ParticleForceGenerator {

    @Override
    public void updateForce(Particle particle) {
        updateForce(particle, 0);
    }

}
