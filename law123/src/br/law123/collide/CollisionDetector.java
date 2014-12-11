package br.law123.collide;

import br.law123.collide.util.BoxCollisor;
import br.law123.collide.util.PlaneCollisor;
import br.law123.collide.util.SphereCollisor;
import br.law123.core.Vector3;
import br.law123.rigidbody.contact.Contact;

/**
 * A wrapper class that holds the fine grained collision detection
 * routines.
 * 
 * Each of the functions has the same format: it takes the details
 * of two objects, and a pointer to a contact array to fill. It
 * returns the number of contacts it wrote into the array.
 */
public class CollisionDetector {

    // Go through each combination of + and - for each half-size
    public static double mults[][] = { { 1, 1, 1 }, { -1, 1, 1 }, { 1, -1, 1 }, { -1, -1, 1 }, { 1, 1, -1 }, { -1, 1, -1 }, { 1, -1, -1 }, { -1, -1, -1 } };

    public static <C extends CollisionPrimitive & SphereCollisor> int sphereAndHalfSpace(C sphere, PlaneCollisor plane, CollisionData data) {
        // Make sure we have contacts
        if (!data.hasMoreContacts()) return 0;

        // Cache the sphere position
        Vector3 position = sphere.getAxis(3);

        // Find the distance from the plane
        double ballDistance = plane.getDirection().mult(position) - sphere.getRadius() - plane.getOffset();

        if (ballDistance >= 0) return 0;

        // Create the contact - it has a normal in the plane direction.
        Contact contact = new Contact();
        contact.setContactNormal(plane.getDirection());
        contact.setPenetration(-ballDistance);
        contact.setContactPoint(position.sub(plane.getDirection().mult(ballDistance + sphere.getRadius())));
        contact.setBodyData(sphere.getBody(), null, data.getFriction(), data.getRestitution());

        data.addContact(contact);
        sphere.bindContact(contact);
        return 1;
    }

    public static <C extends CollisionPrimitive & SphereCollisor> int sphereAndTruePlane(C sphere, CollisionPlane plane, CollisionData data) {
        // Make sure we have contacts
        if (!data.hasMoreContacts()) return 0;

        // Cache the sphere position
        Vector3 position = sphere.getAxis(3);

        // Find the distance from the plane
        double centreDistance = plane.getDirection().mult(position) - plane.getOffset();

        // Check if we're within radius
        if (centreDistance * centreDistance > sphere.getRadius() * sphere.getRadius()) {
            return 0;
        }

        // Check which side of the plane we're on
        Vector3 normal = plane.getDirection();
        double penetration = -centreDistance;
        if (centreDistance < 0) {
            normal.multToMe(-1);
            penetration = -penetration;
        }
        penetration += sphere.getRadius();

        // Create the contact - it has a normal in the plane direction.
        Contact contact = new Contact();
        contact.setContactNormal(normal);
        contact.setPenetration(penetration);
        contact.setContactPoint(position.sub(plane.getDirection().mult(centreDistance)));
        contact.setBodyData(sphere.getBody(), null, data.getFriction(), data.getRestitution());

        data.addContact(contact);
        sphere.bindContact(contact);
        return 1;
    }

    public static <C extends CollisionPrimitive & SphereCollisor> int sphereAndSphere(C one, C two, CollisionData data) {
        // Make sure we have contacts
        if (!data.hasMoreContacts()) return 0;

        // Cache the sphere positions
        Vector3 positionOne = one.getAxis(3);
        Vector3 positionTwo = two.getAxis(3);

        // Find the vector between the objects
        Vector3 midline = positionOne.sub(positionTwo);
        double size = midline.magnitude();

        // See if it is large enough.
        if (size <= 0.0f || size >= one.getRadius() + two.getRadius()) {
            return 0;
        }

        // We manually create the normal, because we have the
        // size to hand.
        Vector3 normal = midline.mult(1.0 / size);

        Contact contact = new Contact();
        contact.setContactNormal(normal);
        contact.setContactPoint(positionOne.sum(midline.mult(0.5)));
        contact.setPenetration(one.getRadius() + two.getRadius() - size);
        contact.setBodyData(one.getBody(), two.getBody(), data.getFriction(), data.getRestitution());

        data.addContact(contact);
        one.bindContact(contact);
        return 1;
    }

    /**
     * Does a collision test on a collision box and a plane representing
     * a half-space (i.e. the normal of the plane
     * points out of the half-space).
     */
    public static <B extends CollisionPrimitive & BoxCollisor, P extends PlaneCollisor> int boxAndHalfSpace(B box, P plane, CollisionData data) {
        // Make sure we have contacts
        if (!data.hasMoreContacts()) return 0;

        // Check for intersection
        if (!IntersectionTests.boxAndHalfSpace(box, plane)) {
            return 0;
        }

        // We have an intersection, so find the intersection points. We can make
        // do with only checking vertices. If the box is resting on a plane
        // or on an edge, it will be reported as four or two contact points.

        int contactsUsed = 0;
        for (int i = 0; i < 8; i++) {

            // Calculate the position of each vertex
            Vector3 vertexPos = new Vector3(mults[i][0], mults[i][1], mults[i][2]);
            vertexPos.componentProductUpdate(box.getHalfSize());
            vertexPos = box.getTransform().transform(vertexPos);

            // Calculate the distance from the plane
            double vertexDistance = vertexPos.mult(plane.getDirection());

            // Compare this to the plane's distance
            if (vertexDistance <= plane.getOffset()) {
                // Create the contact data.

                // The contact point is halfway between the vertex and the
                // plane - we multiply the direction by half the separation
                // distance and add the vertex location.
                Contact contact = new Contact();

                contact.setContactPoint(new Vector3(plane.getDirection()));
                contact.getContactPoint().multToMe(vertexDistance - plane.getOffset());
                contact.getContactPoint().sumToMe(vertexPos);
                contact.setContactNormal(new Vector3(plane.getDirection()));
                contact.setPenetration(plane.getOffset() - vertexDistance);

                // Write the appropriate data
                contact.setBodyData(box.getBody(), null, data.getFriction(), data.getRestitution());

                // Move onto the next contact
                data.addContact(contact);

                box.bindContact(contact);
                contactsUsed++;
                if (!data.hasMoreContacts()) return contactsUsed;
            }
        }

        return contactsUsed;
    }

    public static <B extends CollisionPrimitive & BoxCollisor> int boxAndBox(B one, B two, CollisionData data) {
        //if (!IntersectionTests::boxAndBox(one, two)) return 0;

        // Find the vector between the two centres
        Vector3 toCentre = two.getAxis(3).sub(one.getAxis(3));

        // We start assuming there is no contact
        NumberReference pen = new NumberReference(Double.MAX_VALUE);
        NumberReference best = new NumberReference(0xffffff);

        // Now we check each axes, returning if it gives us
        // a separating axis, and keeping track of the axis with
        // the smallest penetration otherwise.
        if (//
        (!CollideUtils.tryAxis(one, two, one.getAxis(0), toCentre, 0, pen, best)) || (!CollideUtils.tryAxis(one, two, one.getAxis(1), toCentre, 1, pen, best)) || (!CollideUtils.tryAxis(one, two, one.getAxis(2), toCentre, 2, pen, best)) ||

        (!CollideUtils.tryAxis(one, two, two.getAxis(0), toCentre, 3, pen, best)) || (!CollideUtils.tryAxis(one, two, two.getAxis(1), toCentre, 4, pen, best)) || (!CollideUtils.tryAxis(one, two, two.getAxis(2), toCentre, 5, pen, best))) {
            return 0;
        }

        // Store the best axis-major, in case we run into almost
        // parallel edge collisions later
        int bestSingleAxis = (Integer) best.get();

        if (//
        (!CollideUtils.tryAxis(one, two, one.getAxis(0).rest(two.getAxis(0)), toCentre, 6, pen, best)) || (!CollideUtils.tryAxis(one, two, one.getAxis(0).rest(two.getAxis(1)), toCentre, 7, pen, best)) || (!CollideUtils.tryAxis(one, two, one.getAxis(0).rest(two.getAxis(2)), toCentre, 8, pen, best)) || (!CollideUtils.tryAxis(one, two, one.getAxis(1).rest(two.getAxis(0)), toCentre, 9, pen, best)) || (!CollideUtils.tryAxis(one, two, one.getAxis(1).rest(two.getAxis(1)), toCentre, 10, pen, best)) || (!CollideUtils.tryAxis(one, two, one.getAxis(1).rest(two.getAxis(2)), toCentre, 11, pen, best)) || (!CollideUtils.tryAxis(one, two, one.getAxis(2).rest(two.getAxis(0)), toCentre, 12, pen, best)) || (!CollideUtils.tryAxis(one, two, one.getAxis(2).rest(two.getAxis(1)), toCentre, 13, pen, best)) || (!CollideUtils.tryAxis(one, two, one.getAxis(2).rest(two.getAxis(2)), toCentre, 14, pen, best))) {
            return 0;
        }

        // Make sure we've got a result.
        assert (((Integer) best.get()) != 0xffffff);

        // We now know there's a collision, and we know which
        // of the axes gave the smallest penetration. We now
        // can deal with it in different ways depending on
        // the case.
        if (((Integer) best.get()) < 3) {
            // We've got a vertex of box two on a face of box one.
            Contact contact = CollideUtils.fillPointFaceBoxBox(one, two, toCentre, data, (Integer) best.get(), (Double) pen.get());
            data.addContact(contact);
            one.bindContact(contact);
            return 1;
        } else if (((Integer) best.get()) < 6) {
            // We've got a vertex of box one on a face of box two.
            // We use the same algorithm as above, but swap around
            // one and two (and therefore also the vector between their
            // centres).
            Contact contact = CollideUtils.fillPointFaceBoxBox(two, one, toCentre.mult(-1.0f), data, ((Integer) best.get()) - 3, (Double) pen.get());
            data.addContact(contact);
            one.bindContact(contact);
            return 1;
        } else {
            // We've got an edge-edge contact. Find out which axes
            best.set((Integer) best.get() - 6);
            int oneAxisIndex = ((Integer) best.get()) / 3;
            int twoAxisIndex = ((Integer) best.get()) % 3;
            Vector3 oneAxis = one.getAxis(oneAxisIndex);
            Vector3 twoAxis = two.getAxis(twoAxisIndex);
            Vector3 axis = oneAxis.rest(twoAxis);
            axis.normalise();

            // The axis should point from box one to box two.
            if (axis.mult(toCentre) > 0) axis = axis.mult(-1.0f);

            // We have the axes, but not the edges: each axis has 4 edges parallel
            // to it, we need to find which of the 4 for each object. We do
            // that by finding the point in the centre of the edge. We know
            // its component in the direction of the box's collision axis is zero
            // (its a mid-point) and we determine which of the extremes in each
            // of the other axes is closest.
            Vector3 ptOnOneEdge = new Vector3(one.getHalfSize());
            Vector3 ptOnTwoEdge = new Vector3(two.getHalfSize());
            for (int i = 0; i < 3; i++) {
                if (i == oneAxisIndex) ptOnOneEdge.set(i, 0);
                else if (one.getAxis(i).mult(axis) > 0) ptOnOneEdge.set(i, -ptOnOneEdge.get(i));

                if (i == twoAxisIndex) ptOnTwoEdge.set(i, 0);
                else if (two.getAxis(i).mult(axis) < 0) ptOnTwoEdge.set(i, -ptOnTwoEdge.get(i));
            }

            // Move them into world coordinates (they are already oriented
            // correctly, since they have been derived from the axes).
            ptOnOneEdge = one.getTransform().mult(ptOnOneEdge);
            ptOnTwoEdge = two.getTransform().mult(ptOnTwoEdge);

            // So we have a point and a direction for the colliding edges.
            // We need to find out point of closest approach of the two
            // line-segments.
            Vector3 vertex = CollideUtils.contactPoint(ptOnOneEdge, oneAxis, one.getHalfSize().get(oneAxisIndex), ptOnTwoEdge, twoAxis, two.getHalfSize().get(twoAxisIndex), bestSingleAxis > 2);

            // We can fill the contact.
            Contact contact = new Contact();

            contact.setPenetration((Double) pen.get());
            contact.setContactNormal(axis);
            contact.setContactPoint(vertex);
            contact.setBodyData(one.getBody(), two.getBody(), data.getFriction(), data.getRestitution());
            data.addContact(contact);
            one.bindContact(contact);
            return 1;
        }
    }

    public static <B extends CollisionPrimitive & BoxCollisor> int boxAndPoint(B box, Vector3 point, CollisionData data) {
        // Transform the point into box coordinates
        Vector3 relPt = box.getTransform().transformInverse(point);

        Vector3 normal = new Vector3();

        // Check each axis, looking for the axis on which the
        // penetration is least deep.
        double min_depth = box.getHalfSize().getX() - Math.abs(relPt.getX());
        if (min_depth < 0) return 0;
        normal = box.getAxis(0).mult((relPt.getX() < 0) ? -1 : 1);

        double depth = box.getHalfSize().getY() - Math.abs(relPt.getY());
        if (depth < 0) return 0;
        else if (depth < min_depth) {
            min_depth = depth;
            normal = box.getAxis(1).mult((relPt.getY() < 0) ? -1 : 1);
        }

        depth = box.getHalfSize().getZ() - Math.abs(relPt.getZ());
        if (depth < 0) return 0;
        else if (depth < min_depth) {
            min_depth = depth;
            normal = box.getAxis(2).mult((relPt.getZ() < 0) ? -1 : 1);
        }

        // Compile the contact
        Contact contact = new Contact();
        contact.setContactNormal(normal);
        contact.setContactPoint(point);
        contact.setPenetration(min_depth);

        // Note that we don't know what rigid body the point
        // belongs to, so we just use NULL. Where this is called
        // this value can be left, or filled in.
        contact.setBodyData(box.getBody(), null, data.getFriction(), data.getRestitution());

        data.addContact(contact);
        box.bindContact(contact);
        return 1;
    }

    public static <B extends CollisionPrimitive & BoxCollisor, C extends CollisionPrimitive & SphereCollisor> boolean boxAndSphere(B box, C sphere, CollisionData data) {
        // Transform the centre of the sphere into box coordinates
        Vector3 centre = sphere.getAxis(3);
        Vector3 relCentre = box.getTransform().transformInverse(centre);

        // Early out check to see if we can exclude the contact
        if (Math.abs(relCentre.getX()) - sphere.getRadius() > box.getHalfSize().getX() || Math.abs(relCentre.getY()) - sphere.getRadius() > box.getHalfSize().getY() || Math.abs(relCentre.getZ()) - sphere.getRadius() > box.getHalfSize().getZ()) {
            return false;
        }

        Vector3 closestPt = new Vector3();
        double dist;

        // Clamp each coordinate to the box.
        dist = relCentre.getX();
        if (dist > box.getHalfSize().getX()) dist = box.getHalfSize().getX();
        if (dist < -box.getHalfSize().getX()) dist = -box.getHalfSize().getX();
        closestPt.setX(dist);

        dist = relCentre.getY();
        if (dist > box.getHalfSize().getY()) dist = box.getHalfSize().getY();
        if (dist < -box.getHalfSize().getY()) dist = -box.getHalfSize().getY();
        closestPt.setY(dist);

        dist = relCentre.getZ();
        if (dist > box.getHalfSize().getZ()) dist = box.getHalfSize().getZ();
        if (dist < -box.getHalfSize().getZ()) dist = -box.getHalfSize().getZ();
        closestPt.setZ(dist);

        // Check we're in contact
        dist = closestPt.sub(relCentre).squareMagnitude();
        if (dist > sphere.getRadius() * sphere.getRadius()) return false;

        // Compile the contact
        Vector3 closestPtWorld = box.getTransform().transform(closestPt);

        Contact contact = new Contact();
        contact.setContactNormal(closestPtWorld.sub(centre));
        contact.getContactNormal().normalise();
        contact.setContactPoint(closestPtWorld);
        contact.setPenetration(sphere.getRadius() - Math.sqrt(dist));
        contact.setBodyData(box.getBody(), sphere.getBody(), data.getFriction(), data.getRestitution());

        data.addContact(contact);
        box.bindContact(contact);
        return true;
    }

}
