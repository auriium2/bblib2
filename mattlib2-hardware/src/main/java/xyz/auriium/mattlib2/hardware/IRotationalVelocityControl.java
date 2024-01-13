package xyz.auriium.mattlib2.hardware;

public interface IRotationalVelocityControl {


    /**
     * @param setPointMechanism_rotationsPerSecond a desired velocity to maintain, in terms of rotations meters per second
     */
    void controlToRotationalVelocityReference(double setPointMechanism_rotationsPerSecond);

}
