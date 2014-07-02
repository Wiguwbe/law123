package br.law123.rigidbody;

import br.law123.core.Matrix3;
import br.law123.core.Matrix4;
import br.law123.core.Quaternion;
import br.law123.core.Vector3;

/**
 * Utilitários para {@link RigidBody}.
 * 
 * @author teixeira
 */
public class RigidBodyUtils {

    /**
     * Internal function that checks the validity of an inverse inertia tensor.
     */
    static void checkInverseInertiaTensor(Matrix3 iitWorld) {
        // TODO: Perform a validity check in an assert.
    }

    /**
     * Internal function to do an intertia tensor transform by a quaternion.
     * Note that the implementation of this function was created by an
     * automated code-generator and optimizer.
     */
    static void transformInertiaTensor(Matrix3 iitWorld, Quaternion q, Matrix3 iitBody, Matrix4 rotmat) {
        double t4 = rotmat.getData(0) * iitBody.getData(0) + rotmat.getData(1) * iitBody.getData(3) + rotmat.getData(2) * iitBody.getData(6);
        double t9 = rotmat.getData(0) * iitBody.getData(1) + rotmat.getData(1) * iitBody.getData(4) + rotmat.getData(2) * iitBody.getData(7);
        double t14 = rotmat.getData(0) * iitBody.getData(2) + rotmat.getData(1) * iitBody.getData(5) + rotmat.getData(2) * iitBody.getData(8);
        double t28 = rotmat.getData(4) * iitBody.getData(0) + rotmat.getData(5) * iitBody.getData(3) + rotmat.getData(6) * iitBody.getData(6);
        double t33 = rotmat.getData(4) * iitBody.getData(1) + rotmat.getData(5) * iitBody.getData(4) + rotmat.getData(6) * iitBody.getData(7);
        double t38 = rotmat.getData(4) * iitBody.getData(2) + rotmat.getData(5) * iitBody.getData(5) + rotmat.getData(6) * iitBody.getData(8);
        double t52 = rotmat.getData(8) * iitBody.getData(0) + rotmat.getData(9) * iitBody.getData(3) + rotmat.getData(10) * iitBody.getData(6);
        double t57 = rotmat.getData(8) * iitBody.getData(1) + rotmat.getData(9) * iitBody.getData(4) + rotmat.getData(10) * iitBody.getData(7);
        double t62 = rotmat.getData(8) * iitBody.getData(2) + rotmat.getData(9) * iitBody.getData(5) + rotmat.getData(10) * iitBody.getData(8);

        iitWorld.setData(0, t4 * rotmat.getData(0) + t9 * rotmat.getData(1) + t14 * rotmat.getData(2));
        iitWorld.setData(1, t4 * rotmat.getData(4) + t9 * rotmat.getData(5) + t14 * rotmat.getData(6));
        iitWorld.setData(2, t4 * rotmat.getData(8) + t9 * rotmat.getData(9) + t14 * rotmat.getData(10));
        iitWorld.setData(3, t28 * rotmat.getData(0) + t33 * rotmat.getData(1) + t38 * rotmat.getData(2));
        iitWorld.setData(4, t28 * rotmat.getData(4) + t33 * rotmat.getData(5) + t38 * rotmat.getData(6));
        iitWorld.setData(5, t28 * rotmat.getData(8) + t33 * rotmat.getData(9) + t38 * rotmat.getData(10));
        iitWorld.setData(6, t52 * rotmat.getData(0) + t57 * rotmat.getData(1) + t62 * rotmat.getData(2));
        iitWorld.setData(7, t52 * rotmat.getData(4) + t57 * rotmat.getData(5) + t62 * rotmat.getData(6));
        iitWorld.setData(8, t52 * rotmat.getData(8) + t57 * rotmat.getData(9) + t62 * rotmat.getData(10));
    }

    /**
     * Inline function that creates a transform matrix from a
     * position and orientation.
     */
    static void calculateTransformMatrix(Matrix4 transformMatrix, Vector3 position, Quaternion orientation) {
        transformMatrix.setData(0, 1 - 2 * orientation.getJ() * orientation.getJ() - 2 * orientation.getK() * orientation.getK());
        transformMatrix.setData(1, 2 * orientation.getI() * orientation.getJ() - 2 * orientation.getR() * orientation.getK());
        transformMatrix.setData(2, 2 * orientation.getI() * orientation.getK() + 2 * orientation.getR() * orientation.getJ());
        transformMatrix.setData(3, position.getX());

        transformMatrix.setData(4, 2 * orientation.getI() * orientation.getJ() + 2 * orientation.getR() * orientation.getK());
        transformMatrix.setData(5, 1 - 2 * orientation.getI() * orientation.getI() - 2 * orientation.getK() * orientation.getK());
        transformMatrix.setData(6, 2 * orientation.getJ() * orientation.getK() - 2 * orientation.getR() * orientation.getI());
        transformMatrix.setData(7, position.getY());

        transformMatrix.setData(8, 2 * orientation.getI() * orientation.getK() - 2 * orientation.getR() * orientation.getJ());
        transformMatrix.setData(9, 2 * orientation.getJ() * orientation.getK() + 2 * orientation.getR() * orientation.getI());
        transformMatrix.setData(10, 1 - 2 * orientation.getI() * orientation.getI() - 2 * orientation.getJ() * orientation.getJ());
        transformMatrix.setData(11, position.getZ());
    }

}
