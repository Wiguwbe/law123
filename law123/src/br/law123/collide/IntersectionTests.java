package br.law123.collide;

import br.law123.core.Vector3;

/**
 * A wrapper class that holds fast intersection tests. These
 * can be used to drive the coarse collision detection system or
 * as an early out in the full collision tests below.
 */
public class IntersectionTests {

    public static boolean sphereAndHalfSpace(CollisionSphere sphere, CollisionPlane plane) {
        // Find the distance from the origin
        double ballDistance = plane.getDirection().mult(sphere.getAxis(3)) - sphere.getRadius();

        // Check for the intersection
        return ballDistance <= plane.getOffset();
    }

    public static boolean sphereAndSphere(CollisionSphere one, CollisionSphere two) {
        // Find the vector between the objects
        Vector3 midline = one.getAxis(3).sub(two.getAxis(3));

        // See if it is large enough.
        return midline.squareMagnitude() < (one.getRadius() + two.getRadius()) * (one.getRadius() + two.getRadius());
    }

    public static boolean boxAndBox(CollisionBox one, CollisionBox two) {
        // Find the vector between the two centres
        Vector3 toCentre = two.getAxis(3).sub(one.getAxis(3));
        return (
        // Check on box one's axes first
        CollideUtils.overlapOnAxis(one, two, one.getAxis(0), toCentre) && //
        CollideUtils.overlapOnAxis(one, two, one.getAxis(1), toCentre) && //
        CollideUtils.overlapOnAxis(one, two, one.getAxis(2), toCentre) && //

        // And on two's
        CollideUtils.overlapOnAxis(one, two, two.getAxis(0), toCentre) && //
        CollideUtils.overlapOnAxis(one, two, two.getAxis(1), toCentre) && //
        CollideUtils.overlapOnAxis(one, two, two.getAxis(2), toCentre) && //

        // Now on the cross products
        CollideUtils.overlapOnAxis(one, two, one.getAxis(0).rest(two.getAxis(0)), toCentre) && //
        CollideUtils.overlapOnAxis(one, two, one.getAxis(0).rest(two.getAxis(1)), toCentre) && //
        CollideUtils.overlapOnAxis(one, two, one.getAxis(0).rest(two.getAxis(2)), toCentre) && //
        CollideUtils.overlapOnAxis(one, two, one.getAxis(1).rest(two.getAxis(0)), toCentre) && //
        CollideUtils.overlapOnAxis(one, two, one.getAxis(1).rest(two.getAxis(1)), toCentre) && //
        CollideUtils.overlapOnAxis(one, two, one.getAxis(1).rest(two.getAxis(2)), toCentre) && //
        CollideUtils.overlapOnAxis(one, two, one.getAxis(2).rest(two.getAxis(0)), toCentre) && //
        CollideUtils.overlapOnAxis(one, two, one.getAxis(2).rest(two.getAxis(1)), toCentre) && //
        CollideUtils.overlapOnAxis(one, two, one.getAxis(2).rest(two.getAxis(2)), toCentre));
    }

    /**
     * Does an intersection test on an arbitrarily aligned box and a
     * half-space.
     * 
     * The box is given as a transform matrix, including
     * position, and a vector of half-sizes for the extend of the
     * box along each local axis.
     * 
     * The half-space is given as a direction (i.e. unit) vector and the
     * offset of the limiting plane from the origin, along the given
     * direction.
     */
    public static boolean boxAndHalfSpace(CollisionBox box, CollisionPlane plane) {
        // Work out the projected radius of the box onto the plane direction
        double projectedRadius = CollideUtils.transformToAxis(box, plane.getDirection());

        // Work out how far the box is from the origin
        double boxDistance = plane.getDirection().mult(box.getAxis(3)) - projectedRadius;

        // Check for the intersection
        return boxDistance <= plane.getOffset();
    }

}
