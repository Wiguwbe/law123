package br.law123.collide;

import br.law123.core.Vector3;

/**
 * The plane is not a primitive: it doesn't represent another
 * rigid body. It is used for contacts with the immovable
 * world geometry.
 */
class CollisionPlane {

    /**
     * The plane normal
     */
    private Vector3 direction;

    /**
     * The distance of the plane from the origin.
     */
    private double offset;

    public Vector3 getDirection() {
        return direction;
    }

    public double getOffset() {
        return offset;
    }

}
