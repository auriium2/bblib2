package xyz.auriium.mattlib2.hardware;

public interface ILinearVelocityControl {

    /**
     * @param setPointMechanism_metersPerSecond a desired velocity to maintain, in terms of mechanism meters per second
     */
    void controlToLinearVelocityReference(double setPointMechanism_metersPerSecond);

}
