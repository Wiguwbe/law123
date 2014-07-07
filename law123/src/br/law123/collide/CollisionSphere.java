package br.law123.collide;

/**
 * Represents a rigid body that can be treated as a sphere
 * for collision detection.
 */
class CollisionSphere extends CollisionPrimitive {

    /**
     * The radius of the sphere.
     */
    private double radius;

    public double getRadius() {
        return radius;
    }
}
