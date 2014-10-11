package br.law123.collide;

import br.law123.collide.util.BoxCollisor;
import br.law123.core.Vector3;
import br.law123.rigidbody.contact.Contact;

public class CollideUtils {

    public static <B extends CollisionPrimitive & BoxCollisor> double transformToAxis(B box, Vector3 axis) {
        return box.getHalfSize().getX() * Math.abs(axis.mult(box.getAxis(0))) //
               + box.getHalfSize().getY() * Math.abs(axis.mult(box.getAxis(1))) //
               + box.getHalfSize().getZ() * Math.abs(axis.mult(box.getAxis(2)));
    }

    /**
     * This function checks if the two boxes overlap
     * along the given axis. The final parameter toCentre
     * is used to pass in the vector between the boxes centre
     * points, to avoid having to recalculate it each time.
     */
    public static boolean overlapOnAxis(CollisionBox one, CollisionBox two, Vector3 axis, Vector3 toCentre) {
        // Project the half-size of one onto axis
        double oneProject = transformToAxis(one, axis);
        double twoProject = transformToAxis(two, axis);

        // Project this onto the axis
        double distance = Math.abs(toCentre.mult(axis));

        // Check for overlap
        return (distance < oneProject + twoProject);
    }

    /*
     * This function checks if the two boxes overlap
     * along the given axis, returning the ammount of overlap.
     * The final parameter toCentre
     * is used to pass in the vector between the boxes centre
     * points, to avoid having to recalculate it each time.
     */
    public static <B extends CollisionPrimitive & BoxCollisor> double penetrationOnAxis(B one, B two, Vector3 axis, Vector3 toCentre) {
        // Project the half-size of one onto axis
        double oneProject = transformToAxis(one, axis);
        double twoProject = transformToAxis(two, axis);

        // Project this onto the axis
        double distance = Math.abs(toCentre.mult(axis));

        // Return the overlap (i.e. positive indicates
        // overlap, negative indicates separation).
        return oneProject + twoProject - distance;
    }

    public static <B extends CollisionPrimitive & BoxCollisor> boolean tryAxis(B one, B two, Vector3 axis, Vector3 toCentre, int index,

    // These values may be updated
    NumberReference smallestPenetration, NumberReference smallestCase) {
        // Make sure we have a normalized axis, and don't check almost parallel axes
        if (axis.squareMagnitude() < 0.0001) return true;
        axis.normalise();

        double penetration = penetrationOnAxis(one, two, axis, toCentre);

        if (penetration < 0) return false;
        if (penetration < (Double) smallestPenetration.get()) {
            smallestPenetration.set(penetration);
            smallestCase.set(index);
        }
        return true;
    }

    static <B extends CollisionPrimitive & BoxCollisor> Contact fillPointFaceBoxBox(B one, B two, Vector3 toCentre, CollisionData data, int best, double pen) {
        // This method is called when we know that a vertex from
        // box two is in contact with box one.


        // We know which axis the collision is on (i.e. best),
        // but we need to work out which of the two faces on
        // this axis.
        Vector3 normal = one.getAxis(best);
        if (one.getAxis(best).mult(toCentre) > 0) {
            normal = normal.mult(-1.0f);
        }

        // Work out which vertex of box two we're colliding with.
        // Using toCentre doesn't work!
        Vector3 vertex = new Vector3(two.getHalfSize());
        if (two.getAxis(0).mult(normal) < 0) vertex.setX(-vertex.getX());
        if (two.getAxis(1).mult(normal) < 0) vertex.setY(-vertex.getY());
        if (two.getAxis(2).mult(normal) < 0) vertex.setZ(-vertex.getZ());

        // Create the contact data
        Contact contact = new Contact();

        contact.setContactNormal(normal);
        contact.setPenetration(pen);
        contact.setContactPoint(two.getTransform().mult(vertex));
        contact.setBodyData(one.getBody(), two.getBody(), data.getFriction(), data.getRestitution());
        return contact;
    }

    static Vector3 contactPoint(Vector3 pOne, Vector3 dOne, double oneSize, Vector3 pTwo, Vector3 dTwo, double twoSize,

    // If this is true, and the contact point is outside
    // the edge (in the case of an edge-face contact) then
    // we use one's midpoint, otherwise we use two's.
    boolean useOne) {
        Vector3 toSt, cOne, cTwo;
        double dpStaOne, dpStaTwo, dpOneTwo, smOne, smTwo;
        double denom, mua, mub;

        smOne = dOne.squareMagnitude();
        smTwo = dTwo.squareMagnitude();
        dpOneTwo = dTwo.mult(dOne);

        toSt = pOne.sub(pTwo);
        dpStaOne = dOne.mult(toSt);
        dpStaTwo = dTwo.mult(toSt);

        denom = smOne * smTwo - dpOneTwo * dpOneTwo;

        // Zero denominator indicates parrallel lines
        if (Math.abs(denom) < 0.0001f) {
            return useOne ? pOne : pTwo;
        }

        mua = (dpOneTwo * dpStaTwo - smTwo * dpStaOne) / denom;
        mub = (smOne * dpStaTwo - dpOneTwo * dpStaOne) / denom;

        // If either of the edges has the nearest point out
        // of bounds, then the edges aren't crossed, we have
        // an edge-face contact. Our point is on the edge, which
        // we know from the useOne parameter.
        if (mua > oneSize || mua < -oneSize || mub > twoSize || mub < -twoSize) {
            return useOne ? pOne : pTwo;
        }
        cOne = pOne.sum(dOne.mult(mua));
        cTwo = pTwo.sum(dTwo.mult(mub));

        return cOne.mult(0.5).sum(cTwo.mult(0.5));
    }

}
