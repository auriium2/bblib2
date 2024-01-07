package xyz.auriium.mattlib2.hardware;

import xyz.auriium.mattlib2.IPeriodicLooped;

public interface ILinearPIDControl extends IPeriodicLooped {

    /**
     * You should call this every frame. Don't call the other version if you call this one.
     * @param setpointMechanism_meters desired position of the mechanism in meters
     */
    void controlToLinearReference(double setpointMechanism_meters);

    /**
     * You should call this every frame. Don't call the other version if you call this one.
     * @param setpointMechanism_meters position of the mechanism in meters
     * @param measurementMechanism_meters position of a substitute encoder readout.
     *                                                 Using this after using moveToReference with internal encoder (not substitute) will cause derivative whiplash
     */
    void controlToLinearReference(double setpointMechanism_meters, double measurementMechanism_meters);

}
