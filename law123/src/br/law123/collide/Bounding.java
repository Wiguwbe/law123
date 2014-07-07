package br.law123.collide;

public abstract class Bounding {

    /**
     * Checks if the bounding sphere overlaps with the other given
     * bounding sphere.
     */
    abstract boolean overlaps(Bounding other);

    /**
     * Reports how much this bounding sphere would have to grow
     * by to incorporate the given bounding sphere. Note that this
     * calculation returns a value not in any particular units (i.e.
     * its not a volume growth). In fact the best implementation
     * takes into account the growth in surface area (after the
     * Goldsmith-Salmon algorithm for tree construction).
     */
    abstract double getGrowth(Bounding other);

    /**
     * Returns the volume of this bounding volume. This is used
     * to calculate how to recurse into the bounding volume tree.
     * For a bounding sphere it is a simple calculation.
     */
    abstract double getSize();

}
