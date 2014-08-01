package br.law123.forcegenerator;

import java.util.ArrayList;
import java.util.List;

import br.law123.forcegenerator.rigidbody.ForceGenerator;
import br.law123.rigidbody.RigidBody;

/**
 * Holds all the force generators and the bodies they apply to.
 */
public class ForceRegistry {

    /**
     * Holds the list of registrations.
     */
    private final List<ForceRegistration> registrations = new ArrayList<ForceRegistration>();

    /**
     * Registers the given force generator to apply to the
     * given body.
     */
    public void add(RigidBody body, ForceGenerator fg) {
        registrations.add(new ForceRegistration(body, fg));
    }

    /**
     * Removes the given registered pair from the registry.
     * If the pair is not registered, this method will have
     * no effect.
     */
    void remove(RigidBody body, ForceGenerator fg) {
        ForceRegistration remove = null;
        for (ForceRegistration i : registrations) {
            if (i.getBody().equals(body) && i.getFg().equals(fg)) {
                remove = i;
                break;
            }
        }
        if (remove != null) {
            registrations.remove(remove);
        }
    }

    /**
     * Clears all registrations from the registry. This will
     * not delete the bodies or the force generators
     * themselves, just the records of their connection.
     */
    public void clear() {
        registrations.clear();
    }

    /**
     * Calls all the force generators to update the forces of
     * their corresponding bodies.
     */
    public void updateForces(double duration) {
        for (ForceRegistration i : registrations) {
            i.getFg().updateForce(i.getBody(), duration);
        }
    }
}
