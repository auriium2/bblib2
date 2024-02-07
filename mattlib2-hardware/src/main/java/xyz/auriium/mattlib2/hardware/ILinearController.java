package xyz.auriium.mattlib2.hardware;

import xyz.auriium.mattlib2.IPeriodicLooped;

public interface ILinearController extends ILinearMotor, IPeriodicLooped {


    /**
     * You should call this every frame. Don't call the other version if you call this one.
     * @param setpointMechanism_meters desired position of the mechanism in meters
     */
    void controlToLinearReference(double setpointMechanism_meters);

    /**
     * does pid but adds a feedforward first, for stability or something
     * @param setpointMechanism_meters
     * @param arbitraryFF_volts voltage to add in, probably from a SimpleMotorFeedforward
     */
    void controlToLinearReferenceArbitrary(double setpointMechanism_meters, double arbitraryFF_volts);

}
