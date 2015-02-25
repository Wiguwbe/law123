package br.law123.rigidbody;

import br.law123.core.Core;
import br.law123.core.Matrix3;
import br.law123.core.Matrix4;
import br.law123.core.Quaternion;
import br.law123.core.Vector3;

/**
 * {@link RigidBody} � o objeto base para tudo relacionado a f�sica.
 */
public class RigidBody implements Cloneable {

    private double inverseMass;

    private Matrix3 inverseInertiaTensor = new Matrix3();
    private Matrix3 inverseInertiaTensorWorld = new Matrix3();

    private double linearDamping;
    private double angularDamping;

    private Vector3 position = new Vector3();
    private Quaternion orientation = new Quaternion();
    private Vector3 velocity = new Vector3();
    private Vector3 rotation = new Vector3();
    private Vector3 acceleration = new Vector3();

    private double motion;

    private boolean isAwake = true;
    private boolean canSleep = true;

    private Matrix4 transformMatrix = new Matrix4();

    private Vector3 forceAccum = new Vector3();
    private Vector3 torqueAccum = new Vector3();

    private Vector3 lastFrameAcceleration = new Vector3();

    /**
     * Realiza os c�lculos de transforma��o de matrix a partir dos dados do corpo. </br>
     * Quando � realizada alguma altera��o direta do estado do corpo, deve se chamar essa opera��o. </br></br>
     * 
     * <li>- Executado automaticamente no processo de integra��o.
     */
    public void calculateDerivedData() {
        orientation.normalise();

        // Calculate the transform matrix for the body.
        RigidBodyUtils.calculateTransformMatrix(transformMatrix, position, orientation);

        // Calculate the inertiaTensor in world space.
        RigidBodyUtils.transformInertiaTensor(inverseInertiaTensorWorld, orientation, inverseInertiaTensor, transformMatrix);

    }

    /**
     * Realiza os c�lculos de integra��o de Newton-Euler, do corpo baseado na dura��o. </br> <br>
     * Newton-Euler � uma aproxima��o linear � integral correta, e por isso pode ser imprecisa ema alguns casos.
     */
    public void integrate(double duration) {
        if (!isAwake) return;

        // Calculate linear acceleration from force inputs.
        lastFrameAcceleration = new Vector3(acceleration);
        lastFrameAcceleration.addScaledVector(forceAccum, inverseMass);

        // Calculate angular acceleration from torque inputs.
        Vector3 angularAcceleration = inverseInertiaTensorWorld.transform(torqueAccum);

        // Adjust velocities
        // Update linear velocity from both acceleration and impulse.
        velocity.addScaledVector(lastFrameAcceleration, duration);

        // Update angular velocity from both acceleration and impulse.
        rotation.addScaledVector(angularAcceleration, duration);

        // Impose drag.
        velocity.multToMe(Math.pow(linearDamping, duration));
        rotation.multToMe(Math.pow(angularDamping, duration));

        // Adjust positions
        // Update linear position.
        position.addScaledVector(velocity, duration);

        // Update angular position.
        orientation.addScaledVector(rotation, duration);

        // Normalise the orientation, and update the matrices with the new
        // position and orientation
        calculateDerivedData();

        // Clear accumulators.
        clearAccumulators();

        // Update the kinetic energy store, and possibly put the body to
        // sleep.
        if (canSleep) {
            double currentMotion = velocity.scalarProduct(velocity) + rotation.scalarProduct(rotation);

            double bias = Math.pow(0.5, duration);
            motion = bias * motion + (1 - bias) * currentMotion;

            double sleepEpsilon = Core.get().getSleepEpsilon();
            if (motion < sleepEpsilon) setAwake(false);
            else if (motion > 10 * sleepEpsilon) motion = 10 * sleepEpsilon;
        }
    }

    /* @} */

    /**
     * Seta a massa do corpo.
     * 
     * @param mass massa.
     */
    public void setMass(double mass) {
        assert (mass != 0);
        this.inverseMass = 1.0 / mass;
    }

    /**
     * Obt�m a massa do corpo.
     * 
     * @return a massa.
     */
    public double getMass() {
        if (inverseMass == 0) {
            return Double.MAX_VALUE;
        }
        return 1.0 / inverseMass;
    }

    private void setInverseMass(double inverseMass) {
        this.inverseMass = inverseMass;
    }

    /**
     * Obt�m a massa inversa do corpo.
     * 
     * @return a massa inversa.
     */
    public double getInverseMass() {
        return inverseMass;
    }

    /**
     * Verifica se possui massa finita.
     * 
     * @return <code>true</code> se possui massa finita.
     */
    public boolean hasFiniteMass() {
        return inverseMass >= 0.0f;
    }

    /**
     * Seta o tensor de inercia do corpo.
     * 
     * @param inertiaTensor o tensor.
     * @throws deve ser informado antes de executar qualquer a��o.
     */
    public void setInertiaTensor(Matrix3 inertiaTensor) {
        inverseInertiaTensor.setInverse(inertiaTensor);
        RigidBodyUtils.checkInverseInertiaTensor(inverseInertiaTensor);
    }

    /**
     * Obt�m uma c�pia do tensor de inercia.
     * 
     * @return c�pia o tensor.
     */
    public Matrix3 getInertiaTensor() {
        Matrix3 it = new Matrix3();
        it.setInverse(inverseInertiaTensor);
        return it;
    }

    private void setInverseInertiaTensor(Matrix3 inverseInertiaTensor) {
        RigidBodyUtils.checkInverseInertiaTensor(inverseInertiaTensor);
        this.inverseInertiaTensor = inverseInertiaTensor;
    }

    /**
     * Obt�m o tensor de in�rcia inverso.
     * 
     * @return o tensor.
     */
    public Matrix3 getInverseInertiaTensor() {
        return inverseInertiaTensor;
    }

    /**
     * Seta o amortecimento linear.
     * 
     * @param linearDamping o amortecimento.
     */
    public void setLinearDamping(double linearDamping) {
        this.linearDamping = linearDamping;
    }

    /**
     * Seta o amortecimento andular.
     * 
     * @param linearDamping o amortecimento.
     */
    public void setAngularDamping(double angularDamping) {
        this.angularDamping = angularDamping;
    }

    /**
     * Seta a posi��o do corpo.
     * 
     * @param position a posi��o.
     */
    public void setPosition(Vector3 position) {
        this.position = position;
    }

    /**
     * Obt�m uma c�pia da posi��o.
     * 
     * @return uma c�pia.
     */
    public Vector3 getPosition() {
        return fillVector3(this.position);
    }

    /**
     * Seta a orienta��o. </br>
     * N�o precisa ser normalizada.
     * 
     * @param orientation orienta��o.
     */
    public void setOrientation(Quaternion orientation) {
        this.orientation = orientation;
        this.orientation.normalise();
    }

    /**
     * Obt�m uma c�pia da orienta��o.
     * 
     * @return
     */
    public Quaternion getOrientation() {
        Quaternion o = new Quaternion();
        fillQuaternion(this.orientation, o);
        return o;
    }

    /**
     * Preenche a matrix com os dados transformados da posi��o e orienta��o do corpo para utiliza��o em aplica��o
     * OpenGL.
     * 
     * @param matrix matriz que deve ser preenchida.
     */
    public void getGLTransform(float[] matrix) {
        matrix[0] = (float) transformMatrix.getData(0);
        matrix[1] = (float) transformMatrix.getData(4);
        matrix[2] = (float) transformMatrix.getData(8);
        matrix[3] = 0;

        matrix[4] = (float) transformMatrix.getData(1);
        matrix[5] = (float) transformMatrix.getData(5);
        matrix[6] = (float) transformMatrix.getData(9);
        matrix[7] = 0;

        matrix[8] = (float) transformMatrix.getData(2);
        matrix[9] = (float) transformMatrix.getData(6);
        matrix[10] = (float) transformMatrix.getData(10);
        matrix[11] = 0;

        matrix[12] = (float) transformMatrix.getData(3);
        matrix[13] = (float) transformMatrix.getData(7);
        matrix[14] = (float) transformMatrix.getData(11);
        matrix[15] = 1;
    }

    /**
     * Obt�m a matriz de transforma��o do corpo.
     * 
     * @return a matriz.
     */
    public Matrix4 getTransform() {
        return transformMatrix;
    }

    /**
     * Converte um ponto no mundo em um ponto do espado local do corpo.
     * 
     * @param point ponto a converter..
     * 
     * @return ponto convertido.
     */
    public Vector3 getPointInWorldSpace(Vector3 point) {
        return transformMatrix.transform(point);
    }

    /**
     * Seta a velocidade inicial.
     * 
     * @param velocity a velocidade.
     */
    public void setVelocity(Vector3 velocity) {
        this.velocity = velocity;
    }

    /**
     * Obt�m a matriz de velocidade do corpo.
     * 
     * @return a velocidade.
     */
    public Vector3 getVelocity() {
        return velocity;
    }

    /**
     * Adiciona velocidade ao corpo.
     * 
     * @param deltaVelocity velocidade a adicionar.
     */
    public void addVelocity(Vector3 deltaVelocity) {
        velocity.sumToMe(deltaVelocity);
    }

    /**
     * Seta a rota��o do corpo.
     * 
     * @param rotation a rota��o.
     */
    public void setRotation(Vector3 rotation) {
        this.rotation = rotation;
    }

    /**
     * Obt�m a rota��o do corpo.
     * 
     * @return a rota��o.
     */
    public Vector3 getRotation() {
        return rotation;
    }

    /**
     * Adiciona rota��o ao corpo.
     * 
     * @param deltaRotation rota��o a adicionar.
     */
    public void addRotation(Vector3 deltaRotation) {
        rotation.sumToMe(deltaRotation);
    }

    /**
     * Verifica se o corpo est� acordado e respondendo a integra��o.
     * 
     * @return true se �_�, false se -_-.
     */
    public boolean getAwake() {
        return isAwake;
    }

    /**
     * Acoooooorda doido!
     */
    public void setAwake() {
        setAwake(true);
    }

    /**
     * Seta o novo estado do corpo.</br>
     * Se o corpo � dormido, as velocidades do mesmo s�o canceladas para n�o interferir na integra��o.
     * 
     * @param awake novo estado.
     */
    public void setAwake(boolean awake) {
        if (awake) {
            isAwake = true;

            // Add a bit of motion to avoid it falling asleep immediately.
            motion = Core.get().getSleepEpsilon() * 2.0f;
        } else {
            isAwake = false;
            velocity.clear();
            rotation.clear();
        }
    }

    /**
     * Seta se pode dormir. </br>
     * Se est� dormindo e n�o pode, d� um tapa e acorda memo.
     * 
     * @param canSleep novo estado de dormismo.
     */
    public void setCanSleep(boolean canSleep) {
        this.canSleep = canSleep;

        if (!canSleep && !isAwake) setAwake();
    }

    /**
     * Obt�m a acelera��o linear corrente que foi preenchida na �ltima integra��o. </br>
     * � baseada no espa�o global.
     * 
     * @return a acelera��o linear.
     */
    public Vector3 getLastFrameAcceleration() {
        return lastFrameAcceleration;
    }

    private void clearAccumulators() {
        forceAccum.clear();
        torqueAccum.clear();
    }

    /**
     * Adiciona for�a.
     * 
     * @param force for�a a aplicar.
     */
    public void addForce(Vector3 force) {
        forceAccum.sumToMe(force);
        isAwake = true;
    }

    /**
     * Adiciona for�a a um ponto. </br>
     * 
     * @param force for�a.
     * 
     * @param point ponto.
     */
    public void addForceAtPoint(Vector3 force, Vector3 point) {
        // Convert to coordinates relative to center of mass.
        Vector3 pt = point;
        pt.subToMe(position);

        forceAccum.sumToMe(force);
        torqueAccum.sumToMe(pt.rest(force));

        isAwake = true;
    }

    /**
     * Adiciona for�a a um ponto do corpo. </br>
     * 
     * @param force for�a.
     * 
     * @param point ponto.
     */
    public void addForceAtBodyPoint(Vector3 force, Vector3 point) {
        // Convert to coordinates relative to center of mass.
        Vector3 pt = getPointInWorldSpace(point);
        addForceAtPoint(force, pt);

    }

    /**
     * Adiciona torque ao corpo. </br>
     * For�a deve ser expressada em rela��o ao mundo.
     * 
     * @param torque torque.
     */
    public void addTorque(Vector3 torque) {
        torqueAccum.sumToMe(torque);
        isAwake = true;
    }

    /**
     * Seta a acelera��o.
     * 
     * @param acceleration
     */
    public void setAcceleration(Vector3 acceleration) {
        this.acceleration = acceleration;
    }

    /**
     * Obt�m a acelera��o.
     * 
     * @return a acelera��o.
     */
    public Vector3 getAcceleration() {
        return acceleration;
    }

    private Vector3 fillVector3(Vector3 origin) {
        Vector3 destin = new Vector3();
        fillVector3(origin, destin);
        return destin;

    }

    private void fillVector3(Vector3 origin, Vector3 destin) {
        destin.setX(origin.getX());
        destin.setY(origin.getY());
        destin.setZ(origin.getZ());
    }

    private void fillMatrix3(Matrix3 origin, Matrix3 destin) {
        for (int i = 0; i < 9; i++) {
            destin.setData(i, origin.getData(i));
        }
    }

    private void fillQuaternion(Quaternion origin, Quaternion destin) {
        destin.setR(origin.getR());
        destin.setI(origin.getI());
        destin.setJ(origin.getJ());
        destin.setK(origin.getK());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("InverseMass: ").append(inverseMass).append("\n");
        sb.append("InverseInertiaTensor: ").append(inverseInertiaTensor).append("\n");
        sb.append("LinearDamping: ").append(linearDamping).append("\n");
        sb.append("AngularDamping: ").append(angularDamping).append("\n");
        sb.append("Position: ").append(position).append("\n");
        sb.append("Orientation: ").append(orientation).append("\n");
        sb.append("Velocity: ").append(velocity).append("\n");
        sb.append("Rotation: ").append(rotation).append("\n");
        sb.append("InverseInertiaTensorWorld: ").append(inverseInertiaTensorWorld).append("\n");
        sb.append("Motion: ").append(motion).append("\n");
        sb.append("IsAwake: ").append(isAwake).append("\n");
        sb.append("CanSleep: ").append(canSleep).append("\n");
        sb.append("TransformMatrix: ").append(transformMatrix).append("\n");
        sb.append("ForceAccum: ").append(forceAccum).append("\n");
        sb.append("TorqueAccum: ").append(torqueAccum).append("\n");
        sb.append("Acceleration: ").append(acceleration).append("\n");
        sb.append("LastFrameAcceleration: ").append(lastFrameAcceleration).append("\n");

        return sb.toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        RigidBody rb = new RigidBody();

        rb.setInverseMass(inverseMass);
        rb.setInverseInertiaTensor(new Matrix3(inverseInertiaTensor));
        rb.setLinearDamping(linearDamping);
        rb.setAngularDamping(angularDamping);
        rb.setPosition(new Vector3(position));
        rb.setOrientation(new Quaternion(orientation));
        rb.setVelocity(new Vector3(velocity));
        rb.setRotation(new Vector3(rotation));
        rb.inverseInertiaTensorWorld = new Matrix3(inverseInertiaTensorWorld);

        rb.motion = motion;
        rb.setAwake(isAwake);
        rb.setCanSleep(canSleep);

        rb.transformMatrix = new Matrix4();
        rb.transformMatrix.setData(transformMatrix.getData());

        rb.forceAccum = new Vector3(forceAccum);
        rb.torqueAccum = new Vector3(torqueAccum);
        rb.setAcceleration(new Vector3(acceleration));

        rb.lastFrameAcceleration = new Vector3(lastFrameAcceleration);

        return rb;
    }

    /**
     * Obt�m o tensor inercial relativo ao mundo.
     * 
     * @return o tensor.
     */
    public Matrix3 getInverseInertiaTensorWorld() {
        Matrix3 m = new Matrix3();
        fillMatrix3(this.inverseInertiaTensorWorld, m);
        return m;
    }

}
