package xyz.auriium.mattlib2.hardware;

public interface IRotationalVelocityController extends IRotationalMotor {

    /**
     * @param setPointMechanism_rotationsPerSecond a desired velocity to maintain, in terms of rotations meters per second
     */
    default void controlToRotationalVelocityReference(double setPointMechanism_rotationsPerSecond) {
        controlToRotationalVelocityReferenceArbitrary(setPointMechanism_rotationsPerSecond, 0);
    }

    /**
     * @param setPointMechanism_rotationsPerSecond a desired velocity to maintain, in terms of rotations meters per second
     * @param arbitraryFF_voltage arbitrary voltage to add before doing PID control, to normalize around dynamics
     */
    void controlToRotationalVelocityReferenceArbitrary(double setPointMechanism_rotationsPerSecond, double arbitraryFF_voltage);

}
