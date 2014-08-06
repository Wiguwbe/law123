package demos.blob;

import br.law123.core.Vector3;
import br.law123.forcegenerator.particle.ParticleForceGenerator;
import br.law123.particle.Particle;

/**
 * A force generator for proximal attraction.
 */
class BlobForceGenerator implements ParticleForceGenerator {

    /**
     * Holds a pointer to the particles we might be attracting.
     */
    Particle[] particles;

    /**
     * The maximum force used to push the particles apart.
     */
    double maxReplusion;

    /**
     * The maximum force used to pull particles together.
     */
    double maxAttraction;

    /**
     * The separation between particles where there is no force.
     */
    double minNaturalDistance, maxNaturalDistance;

    /**
     * The force with which to float the head particle, if it is
     * joined to others.
     */
    double floatHead;

    /**
     * The maximum number of particles in the blob before the head
     * is floated at maximum force.
     */
    int maxFloat;

    /**
     * The separation between particles after which they 'break' apart and
     * there is no force.
     */
    double maxDistance;

    @Override
    public void updateForce(Particle particle, double duration) {
        int joinCount = 0;
        for (int i = 0; i < BlobUtils.BLOB_COUNT; i++) {
            // Don't attract yourself
            if (particles[i] == particle) continue;

            // Work out the separation distance
            Vector3 separation = particles[i].getPosition().sub(particle.getPosition());
            separation.setZ(0.0f);
            double distance = separation.magnitude();

            if (distance < minNaturalDistance) {
                // Use a repulsion force.
                distance = 1.0f - distance / minNaturalDistance;
                particle.addForce(separation.unit().mult(1.0f - distance).mult(maxReplusion).mult(-1.0f));
                joinCount++;
            } else if (distance > maxNaturalDistance && distance < maxDistance) {
                // Use an attraction force.
                distance = (distance - maxNaturalDistance) / (maxDistance - maxNaturalDistance);
                particle.addForce(separation.unit().mult(distance * maxAttraction));
                joinCount++;
            }
        }

        // If the particle is the head, and we've got a join count, then float it.
        if (particle == particles[0] && joinCount > 0 && maxFloat > 0) {
            double force = (joinCount / maxFloat) * floatHead;
            if (force > floatHead) force = floatHead;
            particle.addForce(new Vector3(0, force, 0));
        }

    }

    @Override
    public void updateForce(Particle particle) {
        throw new IllegalStateException();
    }

}
