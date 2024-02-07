package xyz.auriium.mattlib2.hardware;

public interface IRotationalVelocityController extends IRotationalMotor {

    /**
     * @param setPointMechanism_rotationsPerSecond a desired velocity to maintain, in terms of rotations meters per second
     */
    void controlToRotationalVelocityReference(double setPointMechanism_rotationsPerSecond);

    /**
     * @param setPointMechanism_rotationsPerSecond a desired velocity to maintain, in terms of rotations meters per second
     * @param arbitraryFF_voltage arbitrary voltage to add before doing PID control, to normalize around dynamics
     */
    void controlToRotationalVelocityReferenceArbitrary(double setPointMechanism_rotationsPerSecond, double arbitraryFF_voltage);

}
