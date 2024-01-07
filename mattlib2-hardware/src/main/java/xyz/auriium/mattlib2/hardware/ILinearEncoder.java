package xyz.auriium.mattlib2.hardware;

import xyz.auriium.mattlib2.IRaw;

/**
 * Represents an encoder, reporting data on a linear axis
 *
 */
public interface ILinearEncoder extends IRaw {

    /**
     *
     * @param linearOffset_mechanismMeters The linear distance the encoder should think it is at currently, in meters
     */
    void forceLinearOffset(double linearOffset_mechanismMeters);

    /**
     * @return The current linear position in meters, relative to the initialization position. Only works if the device is connected to some
     * kind of linear actuator and has a conversion factor.
     */
    double linearPosition_mechanismMeters();

    /**
     * @return velocity of the mechanism, in meters per second. Only works if the device is connected to some kind of linear actuator and has a conversion factor
     */
    double linearVelocity_mechanismMetersPerSecond();


}
