package br.law123.forcegenerator;

import br.law123.particle.Particle;

public class ParticleForceRegistration {

    private Particle particle;
    private ParticleForceGenerator fg;

    public ParticleForceRegistration(Particle particle, ParticleForceGenerator fg) {
        this.particle = particle;
        this.fg = fg;
    }

    public Particle getParticle() {
        return particle;
    }

    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    public ParticleForceGenerator getFg() {
        return fg;
    }

    public void setFg(ParticleForceGenerator fg) {
        this.fg = fg;
    }

}
