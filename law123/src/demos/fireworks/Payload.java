package demos.fireworks;

/**
 * The payload is the new firework type to create when this
 * firework's fuse is over.
 */
class Payload {

    /** The type of the new particle to create. */
    private int type;

    /** The number of particles in this payload. */
    private int count;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    /** Sets the payload properties in one go. */
    void set(int type, int count) {
        this.type = type;
        this.count = count;
    }
}
