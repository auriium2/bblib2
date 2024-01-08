package xyz.auriium.mattlib2.hardware;

public interface IRotationalPositionControl {


    /**
     * You should call this every frame. Don't call the other version if you call this one.
     * @param setpoint_mechanismNormalizedRotations desired position of the mechanism in meters
     */
    void controlToNormalizedReference(double setpoint_mechanismNormalizedRotations);

    /**
     * You should call this every frame. Don't call the other version if you call this one.
     * @param setpoint_mechanismNormalizedRotations position of the mechanism in meters
     * @param measurement_mechanismNormalizedRotations position of a substitute encoder readout.
     *                                                 Using this after using moveToReference with internal encoder (not substitute) will cause derivative whiplash
     */
    void controlToNormalizedReference(double setpoint_mechanismNormalizedRotations, double measurement_mechanismNormalizedRotations);


    void controlToInfiniteReference(double setpoint_mechanismRotations);

    void controlToInfiniteReference(double setpoint_mechanismRotations, double measurement_mechanismRotations);
}
