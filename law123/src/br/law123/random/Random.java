package br.law123.random;

import br.law123.core.Quaternion;
import br.law123.core.Vector3;

/**
 * Keeps track of one random stream: i.e. a seed and its output.
 * This is used to get random numbers. Rather than a funcion, this
 * allows there to be several streams of repeatable random numbers
 * at the same time. Uses the RandRotB algorithm.
 */
public class Random {

    /**
     * left bitwise rotation
     */

    int rotl(int n, int r) {
        return (n << r) | (n >> (32 - r));
    }

    /**
     * right bitwise rotation
     */
    int rotr(int n, int r) {
        return (n >> r) | (n << (32 - r));
    }

    /**
     * Creates a new random number stream with a seed based on
     * timing data.
     */
    public Random() {
        this(0);
    }

    /**
     * Creates a new random stream with the given seed.
     */
    public Random(int seed) {
        seed(0);
    }

    /**
     * Sets the seed value for the random stream.
     */
    void seed(int s) {
        if (s == 0) {
            s = Integer.MAX_VALUE;
        }

        // Fill the buffer with some basic random numbers
        for (int i = 0; i < 17; i++) {
            // Simple linear congruential generator
            s = s * 2896453 + 1;
            buffer[i] = s;
        }

        // Initialize pointers into the buffer
        p1 = 0;
        p2 = 10;
    }

    /**
     * Returns the next random bitstring from the stream. This is
     * the fastest method.
     */
    int randomBits() {
        int result;

        // Rotate the buffer and store it back to itself
        result = buffer[p1] = rotl(buffer[p2], 13) + rotl(buffer[p1], 9);

        // Rotate pointers
        if (--p1 < 0) p1 = 16;
        if (--p2 < 0) p2 = 16;

        // Return result
        return result;
    }

    /**
     * Returns a random floating point number between 0 and 1.
     */
    public float randomReal() {
        return new java.util.Random().nextFloat();
    }

    /**
     * Returns a random floating point number between 0 and scale.
     */
    float randomReal(float scale) {
        return randomReal() * scale;
    }

    /**
     * Returns a random floating point number between min and max.
     */
    public float randomReal(float min, float max) {
        return randomReal() * (max - min) + min;
    }

    /**
     * Returns a random integer less than the given value.
     */
    public int randomInt(int max) {
        return randomBits() % max;
    }

    /**
     * Returns a random binomially distributed number between -scale
     * and +scale.
     */
    public float randomBinomial(float scale) {
        return (randomReal() - randomReal()) * scale;
    }

    /**
     * Returns a random vector where each component is binomially
     * distributed in the range (-scale to scale) [mean = 0.0f].
     */
    Vector3 randomVector(float scale) {
        return new Vector3(randomBinomial(scale), randomBinomial(scale), randomBinomial(scale));
    }

    /**
     * Returns a random vector where each component is binomially
     * distributed in the range (-scale to scale) [mean = 0.0f],
     * where scale is the corresponding component of the given
     * vector.
     */
    Vector3 randomVector(Vector3 scale) {
        return new Vector3(randomBinomial((float) scale.getX()), randomBinomial((float) scale.getY()), randomBinomial((float) scale.getZ()));
    }

    /**
     * Returns a random vector in the cube defined by the given
     * minimum and maximum vectors. The probability is uniformly
     * distributed in this region.
     */
    public Vector3 randomVector(Vector3 min, Vector3 max) {
        return new Vector3(randomReal((float) min.getX(), (float) max.getX()), randomReal((float) min.getY(), (float) max.getY()), randomReal((float) min.getZ(), (float) max.getZ()));
    }

    /**
     * Returns a random vector where each component is binomially
     * distributed in the range (-scale to scale) [mean = 0.0f],
     * except the y coordinate which is zero.
     */
    Vector3 randomXZVector(float scale) {
        return new Vector3(randomBinomial(scale), 0, randomBinomial(scale));
    }

    /**
     * Returns a random orientation (i.e. normalized) quaternion.
     */
    public Quaternion randomQuaternion() {
        Quaternion q = new Quaternion(randomReal(), randomReal(), randomReal(), randomReal());
        q.normalise();
        return q;
    }

    // Internal mechanics
    private int p1, p2;
    private int[] buffer = new int[17];
}
