package br.law123.collide;

import br.law123.collide.util.BoxCollisor;
import br.law123.core.Vector3;

/**
 * Represents a rigid body that can be treated as an aligned bounding
 * box for collision detection.
 */
public class CollisionBox extends CollisionPrimitive implements BoxCollisor {

    /**
     * Holds the half-sizes of the box along each of its local axes.
     */
    private Vector3 halfSize;

    public void setHalfSize(Vector3 halfSize) {
        this.halfSize = halfSize;
    }

    @Override
    public Vector3 getHalfSize() {
        return halfSize;
    }
}
