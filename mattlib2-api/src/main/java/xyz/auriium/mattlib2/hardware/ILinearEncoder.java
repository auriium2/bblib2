package xyz.auriium.mattlib2.hardware;

/**
 * Represents an encoder, reporting data on a linear axis
 *
 */
public interface ILinearEncoder {

    /**
     * @return The current linear position in meters. Only works if the device is connected to some
     * kind of linear actuator and has a conversion factor.
     */
    double linearMechanismPosition_meters();

}
