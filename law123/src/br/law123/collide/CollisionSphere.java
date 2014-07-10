package br.law123.collide;

/**
 * Represents a rigid body that can be treated as a sphere
 * for collision detection.
 */
public class CollisionSphere extends CollisionPrimitive {

    /**
     * The radius of the sphere.
     */
    private double radius;

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }
}
