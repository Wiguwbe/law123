package br.law123.rigidbody.joints;

import br.law123.core.Vector3;
import br.law123.rigidbody.RigidBody;
import br.law123.rigidbody.contact.Contact;
import br.law123.rigidbody.contact.ContactGenerator;

/**
 * Joints link together two rigid bodies and make sure they do not
 * separate. In a general phyiscs engine there may be many
 * different types of joint, that reduce the number of relative
 * degrees of freedom between two objects. This joint is a common
 * position joint: each object has a location (given in
 * body-coordinates) that will be kept at the same point in the
 * simulation.
 */
public class Joint implements ContactGenerator {

    /**
     * Holds the two rigid bodies that are connected by this joint.
     */
    private RigidBody[] body = new RigidBody[2];

    /**
     * Holds the relative location of the connection for each
     * body, given in local coordinates.
     */
    private Vector3[] position = new Vector3[2];

    /**
     * Holds the maximum displacement at the joint before the
     * joint is considered to be violated. This is normally a
     * small, epsilon value. It can be larger, however, in which
     * case the joint will behave as if an inelastic cable joined
     * the bodies at their joint locations.
     */
    double error;

    /**
     * Configures the joint in one go.
     */
    public void set(RigidBody a, Vector3 a_pos, RigidBody b, Vector3 b_pos, double error) {
        body[0] = a;
        body[1] = b;

        position[0] = a_pos;
        position[1] = b_pos;

        this.error = error;
    }

    /**
     * Generates the contacts required to restore the joint if it
     * has been violated.
     */
    @Override
    public int addContact(Contact contact, int limit) {
        // Calculate the position of each connection point in world coordinates
        Vector3 a_pos_world = body[0].getPointInWorldSpace(position[0]);
        Vector3 b_pos_world = body[1].getPointInWorldSpace(position[1]);

        // Calculate the length of the joint
        Vector3 a_to_b = b_pos_world.sub(a_pos_world);
        Vector3 normal = a_to_b;
        normal.normalise();
        double length = a_to_b.magnitude();

        // Check if it is violated
        if (Math.abs(length) > error) {
            contact.getBody()[0] = body[0];
            contact.getBody()[1] = body[1];
            contact.setContactNormal(normal);
            contact.setContactPoint(a_pos_world.sum(b_pos_world).mult(0.5f));
            contact.setPenetration(length - error);
            contact.setFriction(1.0f);
            contact.setRestitution(0);
            return 1;
        }

        return 0;
    }

    @Override
    public int addContact(Contact contact) {
        return addContact(contact, 0);
    }

}
