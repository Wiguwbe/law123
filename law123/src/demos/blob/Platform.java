package demos.blob;

import br.law123.core.Vector3;
import br.law123.particle.Particle;
import br.law123.particle.contact.ParticleContact;
import br.law123.particle.contact.ParticleContactGenerator;

/**
 * Platforms are two dimensional: lines on which the
 * particles can rest. Platforms are also contact generators for the physics.
 */
public class Platform implements ParticleContactGenerator {

    private Vector3 start;
    private Vector3 end;

    /**
     * Holds a pointer to the particles we're checking for collisions with.
     */
    private Particle[] particles;

    private static final double restitution = 0.0f;

    public Vector3 getStart() {
        return start;
    }

    public void setStart(Vector3 start) {
        this.start = start;
    }

    public Vector3 getEnd() {
        return end;
    }

    public void setEnd(Vector3 end) {
        this.end = end;
    }

    public Particle[] getParticles() {
        return particles;
    }

    public void setParticles(Particle[] particles) {
        this.particles = particles;
    }

    @Override
    public int addContact(ParticleContact[] contacts, int offset, int limit) {

        int used = 0;
        for (int i = 0; i < BlobUtils.BLOB_COUNT; i++) {
            ParticleContact contact = contacts[offset + i];
            if (used >= limit) break;

            // Check for penetration
            Vector3 toParticle = particles[i].getPosition().sub(start);
            Vector3 lineDirection = end.sub(start);
            double projected = toParticle.mult(lineDirection);
            double platformSqLength = lineDirection.squareMagnitude();
            if (projected <= 0) {
                // The blob is nearest to the start point
                if (toParticle.squareMagnitude() < BlobUtils.BLOB_RADIUS * BlobUtils.BLOB_RADIUS) {
                    // We have a collision
                    contact.setContactNormal(toParticle.unit());
                    contact.getContactNormal().setZ(0);
                    contact.setRestitution(restitution);
                    contact.getParticle()[0] = particles[i];
                    contact.getParticle()[1] = null;
                    contact.setPenetration(BlobUtils.BLOB_RADIUS - toParticle.magnitude());
                    used++;
                }

            } else if (projected >= platformSqLength) {
                // The blob is nearest to the end point
                toParticle = particles[i].getPosition().sub(end);
                if (toParticle.squareMagnitude() < BlobUtils.BLOB_RADIUS * BlobUtils.BLOB_RADIUS) {
                    // We have a collision
                    contact.setContactNormal(toParticle.unit());
                    contact.getContactNormal().setZ(0);
                    contact.setRestitution(restitution);
                    contact.getParticle()[0] = particles[i];
                    contact.getParticle()[1] = null;
                    contact.setPenetration(BlobUtils.BLOB_RADIUS - toParticle.magnitude());
                    used++;
                }
            } else {
                // the blob is nearest to the middle.
                double distanceToPlatform = toParticle.squareMagnitude() - projected * projected / platformSqLength;
                if (distanceToPlatform < BlobUtils.BLOB_RADIUS * BlobUtils.BLOB_RADIUS) {
                    // We have a collision
                    Vector3 closestPoint = start.sum(lineDirection.mult(projected / platformSqLength));

                    contact.setContactNormal((particles[i].getPosition().sub(closestPoint)).unit());
                    contact.getContactNormal().setZ(0);
                    contact.setRestitution(restitution);
                    contact.getParticle()[0] = particles[i];
                    contact.getParticle()[1] = null;
                    contact.setPenetration(BlobUtils.BLOB_RADIUS - Math.sqrt(distanceToPlatform));
                    used++;
                }
            }
        }
        return used;
    }

}
