package br.law123.collide;

import br.law123.core.Vector3;

/**
 * Represents a rigid body that can be treated as an aligned bounding
 * box for collision detection.
 */
class CollisionBox extends CollisionPrimitive {

    /**
     * Holds the half-sizes of the box along each of its local axes.
     */
    private Vector3 halfSize;

    public Vector3 getHalfSize() {
        return halfSize;
    }
}
