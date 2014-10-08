package br.law123.collide;

import br.law123.collide.util.SphereCollisor;

/**
 * Represents a rigid body that can be treated as a sphere
 * for collision detection.
 */
public class CollisionSphere extends CollisionPrimitive implements SphereCollisor {

    /**
     * The radius of the sphere.
     */
    private double radius;

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public double getRadius() {
        return radius;
    }
}
