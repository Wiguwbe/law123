package demos.fireworks;

import br.law123.particle.Particle;

/**
 * Fireworks are particles, with additional data for rendering and
 * evolution.
 */
class Firework extends Particle {

    /** Fireworks have an integer type, used for firework rules. */
    private int type;

    /**
     * The age of a firework determines when it detonates. Age gradually
     * decreases, when it passes zero the firework delivers its payload.
     * Think of age as fuse-left.
     */
    private double age;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getAge() {
        return age;
    }

    public void setAge(double age) {
        this.age = age;
    }

    /**
     * Updates the firework by the given duration of time. Returns true
     * if the firework has reached the end of its life and needs to be
     * removed.
     */
    public boolean update(double duration) {
        // Update our physical state
        integrate(duration);

        // We work backwards from our age to zero.
        age -= duration;
        return (age < 0) || (getPosition().getY() < 0);
    }
}
