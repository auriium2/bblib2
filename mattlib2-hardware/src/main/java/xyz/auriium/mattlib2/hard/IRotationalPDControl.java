package xyz.auriium.mattlib2.hard;

public interface IRotationalPDControl {


    /**
     * You should call this every frame. Don't call the other version if you call this one.
     * @param setpointMechanism_normalizedRotations desired position of the mechanism in meters
     */
    void controlToRotationalReference(double setpointMechanism_normalizedRotations);

    /**
     * You should call this every frame. Don't call the other version if you call this one.
     * @param setpointMechanism_normalizedRotations position of the mechanism in meters
     * @param measurementMechanism_normalizedRotations position of a substitute encoder readout.
     *                                                 Using this after using moveToReference with internal encoder (not substitute) will cause derivative whiplash
     */
    void controlToRotationalReference(double setpointMechanism_normalizedRotations, double measurementMechanism_normalizedRotations);

}
