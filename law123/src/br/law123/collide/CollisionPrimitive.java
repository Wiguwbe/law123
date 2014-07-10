package br.law123.collide;

import br.law123.core.Matrix4;
import br.law123.core.Vector3;
import br.law123.rigidbody.RigidBody;

/**
 * Represents a primitive to detect collisions against.
 */
class CollisionPrimitive {

    /**
     * The rigid body that is represented by this primitive.
     */
    private RigidBody body;

    /**
     * The offset of this primitive from the given rigid body.
     */
    private Matrix4 offset = new Matrix4();

    /**
     * The resultant transform of the primitive. This is
     * calculated by combining the offset of the primitive
     * with the transform of the rigid body.
     */
    private Matrix4 transform;

    public void setBody(RigidBody body) {
        this.body = body;
    }

    public RigidBody getBody() {
        return body;
    }

    /**
     * Calculates the internals for the primitive.
     */
    public void calculateInternals() {
        transform = body.getTransform().mult(offset);
    }

    /**
     * This is a convenience function to allow access to the
     * axis vectors in the transform for this primitive.
     */
    public Vector3 getAxis(int index) {
        return transform.getAxisVector(index);
    }

    /**
     * Returns the resultant transform of the primitive, calculated from
     * the combined offset of the primitive and the transform
     * (orientation + position) of the rigid body to which it is
     * attached.
     */
    public Matrix4 getTransform() {
        return transform;
    }
}
