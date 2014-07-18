package demos.fireworks;

import br.law123.core.Core;
import br.law123.core.Vector3;
import br.law123.random.Random;

/**
 * Firework rules control the length of a firework's fuse and the
 * particles it should evolve into.
 */
class FireworkRule {

    static final Random crandom = new Random();

    /** The type of firework that is managed by this rule. */
    private int type;

    /** The minimum length of the fuse. */
    private float minAge;

    /** The maximum legnth of the fuse. */
    private float maxAge;

    /** The minimum relative velocity of this firework. */
    private Vector3 minVelocity;

    /** The maximum relative velocity of this firework. */
    private Vector3 maxVelocity;

    /** The damping of this firework type. */
    private float damping;

    /** The number of payloads for this firework type. */
    private int payloadCount;

    /** The set of payloads. */
    private Payload[] payloads;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getMinAge() {
        return minAge;
    }

    public void setMinAge(float minAge) {
        this.minAge = minAge;
    }

    public float getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(float maxAge) {
        this.maxAge = maxAge;
    }

    public Vector3 getMinVelocity() {
        return minVelocity;
    }

    public void setMinVelocity(Vector3 minVelocity) {
        this.minVelocity = minVelocity;
    }

    public Vector3 getMaxVelocity() {
        return maxVelocity;
    }

    public void setMaxVelocity(Vector3 maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    public float getDamping() {
        return damping;
    }

    public void setDamping(float damping) {
        this.damping = damping;
    }

    public int getPayloadCount() {
        return payloadCount;
    }

    public void setPayloadCount(int payloadCount) {
        this.payloadCount = payloadCount;
    }

    public Payload[] getPayloads() {
        return payloads;
    }

    public void setPayloads(Payload[] payloads) {
        this.payloads = payloads;
    }

    void init(int payloadCount) {
        this.payloadCount = payloadCount;
        payloads = new Payload[payloadCount];
        for (int i = 0; i < payloads.length; i++) {
            payloads[i] = new Payload();
        }
    }

    /**
     * Set all the rule parameters in one go.
     */
    void setParameters(int type, float minAge, float maxAge, Vector3 minVelocity, Vector3 maxVelocity, float damping) {
        this.type = type;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.minVelocity = minVelocity;
        this.maxVelocity = maxVelocity;
        this.damping = damping;
    }

    /**
     * Creates a new firework of this type and writes it into the given
     * instance. The optional parent firework is used to base position
     * and velocity on.
     */
    void create(Firework firework, Firework parent) {
        firework.setType(type);
        firework.setAge(crandom.randomReal(minAge, maxAge));

        Vector3 vel = new Vector3();
        if (parent != null) {
            // The position and velocity are based on the parent.
            firework.setPosition(parent.getPosition());
            vel.sumToMe(parent.getVelocity());
        } else {
            Vector3 start = new Vector3();
            int x = crandom.randomInt(3) - 1;
            start.setX(5.0f * x);
            firework.setPosition(start);
        }

        vel.sumToMe(crandom.randomVector(minVelocity, maxVelocity));
        firework.setVelocity(vel);

        // We use a mass of one in all cases (no point having fireworks
        // with different masses, since they are only under the influence
        // of gravity).
        firework.setMass(1);

        firework.setDamping(damping);

        firework.setAcceleration(Core.GRAVITY);

        firework.clearAccumulator();
    }
}
