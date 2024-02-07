package xyz.auriium.mattlib2.hardware;

public interface ILinearVelocityController extends ILinearMotor {

    /**
     * @param setPointMechanism_metersPerSecond a desired velocity to maintain, in terms of mechanism meters per second
     */
    void controlToLinearVelocityReference(double setPointMechanism_metersPerSecond);


    /**
     * @param setPointMechanism_metersPerSecond a desired velocity to maintain, in terms of mechanism meters per second
     * @param arbitraryFF_voltage arbitrary voltage to add before doing PID control, to normalize around dynamics
     */
    void controlToLinearVelocityReferenceArbitrary(double setPointMechanism_metersPerSecond, double arbitraryFF_voltage);
}
