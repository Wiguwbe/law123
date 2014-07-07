package br.law123.collide;

import java.util.Arrays;

import br.law123.rigidbody.RigidBody;

public class BVHNode<T extends Bounding> {

    /**
     * Holds the child nodes of this node.
     */
    @SuppressWarnings("unchecked")
    private BVHNode<T>[] children = new BVHNode[2];

    /**
     * Holds a single bounding volume encompassing all the
     * descendents of this node.
     */
    private T volume;

    /**
     * Holds the rigid body at this node of the hierarchy.
     * Only leaf nodes can have a rigid body defined (see isLeaf).
     * Note that it is possible to rewrite the algorithms in this
     * class to handle objects at all levels of the hierarchy,
     * but the code provided ignores this vector unless firstChild
     * is NULL.
     */
    private RigidBody body;

    // ... other BVHNode code as before ...

    /**
     * Holds the node immediately above us in the tree.
     */
    private BVHNode<T> parent;

    /**
     * Creates a new node in the hierarchy with the given parameters.
     */
    public BVHNode(BVHNode<T> parent, T volume) {
        this(parent, volume, null);
    }

    public BVHNode(BVHNode<T> parent, T volume, RigidBody body) {
        this.parent = parent;
        this.volume = volume;
        this.body = body;
        children[0] = children[1] = null;
    }

    /**
     * Checks if this node is at the bottom of the hierarchy.
     */
    public boolean isLeaf() {
        return (body != null);
    }

    /**
     * Checks the potential contacts from this node downwards in
     * the hierarchy, writing them to the given array (up to the
     * given limit). Returns the number of potential contacts it
     * found.
     */
    public int getPotentialContacts(PotentialContact[] contacts, int limit) {
        // Early out if we don't have the room for contacts, or
        // if we're a leaf node.
        if (isLeaf() || limit == 0) return 0;

        // Get the potential contacts of one of our children with
        // the other
        return children[0].getPotentialContactsWith(children[1], contacts, limit);
    }

    /**
     * Inserts the given rigid body, with the given bounding volume,
     * into the hierarchy. This may involve the creation of
     * further bounding volume nodes.
     */
    public void insert(RigidBody newBody, T newVolume) {
        // If we are a leaf, then the only option is to spawn two
        // new children and place the new body in one.
        if (isLeaf()) {
            // Child one is a copy of us.
            children[0] = new BVHNode<T>(this, volume, body);

            // Child two holds the new body
            children[1] = new BVHNode<T>(this, newVolume, newBody);

            // And we now loose the body (we're no longer a leaf)
            this.body = null;

            // We need to recalculate our bounding volume
            recalculateBoundingVolume();
        }

        // Otherwise we need to work out which child gets to keep
        // the inserted body. We give it to whoever would grow the
        // least to incorporate it.
        else {
            if (children[0].volume.getGrowth(newVolume) < children[1].volume.getGrowth(newVolume)) {
                children[0].insert(newBody, newVolume);
            } else {
                children[1].insert(newBody, newVolume);
            }
        }
    }

    /**
     * Deltes this node, removing it first from the hierarchy, along
     * with its associated
     * rigid body and child nodes. This method deletes the node
     * and all its children (but obviously not the rigid bodies). This
     * also has the effect of deleting the sibling of this node, and
     * changing the parent node so that it contains the data currently
     * in that sibling. Finally it forces the hierarchy above the
     * current node to reconsider its bounding volume.
     */

    public void destroy() {
        // If we don't have a parent, then we ignore the sibling
        // processing
        if (parent != null) {
            // Find our sibling
            BVHNode<T> sibling;
            if (parent.children[0] == this) {
                sibling = parent.children[1];
            } else {
                sibling = parent.children[0];
            }

            // Write its data to our parent
            parent.volume = sibling.volume;
            parent.body = sibling.body;
            parent.children[0] = sibling.children[0];
            parent.children[1] = sibling.children[1];

            // Delete the sibling (we blank its parent and
            // children to avoid processing/deleting them)
            sibling.parent = null;
            sibling.body = null;
            sibling.children[0] = null;
            sibling.children[1] = null;
            sibling.destroy();

            // Recalculate the parent's bounding volume
            parent.recalculateBoundingVolume();
        }

        // Delete our children (again we remove their
        // parent data so we don't try to process their siblings
        // as they are deleted).
        if (children[0] != null) {
            children[0].parent = null;
            children[0].destroy();
        }
        if (children[1] != null) {
            children[1].parent = null;
            children[0].destroy();
        }
    }

    /**
     * Checks for overlapping between nodes in the hierarchy. Note
     * that any bounding volume should have an overlaps method implemented
     * that checks for overlapping with another object of its own type.
     */
    protected boolean overlaps(BVHNode<T> other) {
        return volume.overlaps(other.volume);
    }

    /**
     * Checks the potential contacts between this node and the given
     * other node, writing them to the given array (up to the
     * given limit). Returns the number of potential contacts it
     * found.
     */
    public int getPotentialContactsWith(BVHNode<T> other, PotentialContact[] contacts, int limit) {
        // Early out if we don't overlap or if we have no room
        // to report contacts
        if (!overlaps(other) || limit == 0) return 0;

        // If we're both at leaf nodes, then we have a potential contact
        if (isLeaf() && other.isLeaf()) {
            contacts[0].getBody()[0] = body;
            contacts[0].getBody()[1] = other.body;
            return 1;
        }

        // Determine which node to descend into. If either is
        // a leaf, then we descend the other. If both are branches,
        // then we use the one with the largest size.
        if (other.isLeaf() || (!isLeaf() && volume.getSize() >= other.volume.getSize())) {
            // Recurse into ourself
            int count = children[0].getPotentialContactsWith(other, contacts, limit);

            // Check we have enough slots to do the other side too
            if (limit > count) {
                return count + children[1].getPotentialContactsWith(other, subArray(contacts, count), limit - count);
            }
            return count;
        }
        // Recurse into the other node
        int count = getPotentialContactsWith(other.children[0], contacts, limit);

        // Check we have enough slots to do the other side too
        if (limit > count) {
            return count + getPotentialContactsWith(other.children[1], subArray(contacts, count), limit - count);
        }
        return count;
    }

    private PotentialContact[] subArray(PotentialContact[] contacts, int count) {
        return Arrays.copyOfRange(contacts, count, contacts.length);
    }

    /**
     * For non-leaf nodes, this method recalculates the bounding volume
     * based on the bounding volumes of its children.
     */
    public void recalculateBoundingVolume() {
        recalculateBoundingVolume(true);
    }

    void recalculateBoundingVolume(boolean recurse) {
        if (isLeaf()) return;

        // Use the bounding volume combining constructor.
        volume = buildBounding(children[0].volume, children[1].volume);

        // Recurse up the tree
        if (parent != null) {
            parent.recalculateBoundingVolume(true);
        }
    }

    private T buildBounding(T v1, T v2) {
        throw new UnsupportedOperationException();
    }

}
