package br.law123.collide;

import br.law123.collide.util.PlaneCollisor;
import br.law123.core.Vector3;

/**
 * The plane is not a primitive: it doesn't represent another
 * rigid body. It is used for contacts with the immovable
 * world geometry.
 */
public class CollisionPlane implements PlaneCollisor {

    /**
     * The plane normal
     */
    private Vector3 direction;

    /**
     * The distance of the plane from the origin.
     */
    private double offset;

    public CollisionPlane() {
    }

    public CollisionPlane(Vector3 direction) {
        this.direction = direction;
    }

    public void setDirection(Vector3 direction) {
        this.direction = direction;
    }

    @Override
    public Vector3 getDirection() {
        return direction;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

    @Override
    public double getOffset() {
        return offset;
    }

}
