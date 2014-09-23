package br.law123.collide;

/**
 * Represents a rigid body that can be treated as a sphere
 * for collision detection.
 */
public class CollisionSphere extends CollisionPrimitive implements CollistionDetectionListener {

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

    @Override
    public void collisionDetection(double duration) {
    }
}
