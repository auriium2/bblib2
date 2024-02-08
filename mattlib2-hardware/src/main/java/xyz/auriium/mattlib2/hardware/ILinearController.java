package xyz.auriium.mattlib2.hardware;

import xyz.auriium.mattlib2.loop.IMattlibHooked;

public interface ILinearController extends ILinearMotor, IMattlibHooked {


    /**
     * You should call this every frame. Don't call the other version if you call this one.
     * @param setpointMechanism_meters desired position of the mechanism in meters
     */
    default void controlToLinearReference(double setpointMechanism_meters) {
        controlToLinearReferenceArbitrary(setpointMechanism_meters, 0);
    }

    /**
     * does pid but adds a feedforward first, for stability or something
     * @param setpointMechanism_meters
     * @param arbitraryFF_volts voltage to add in, probably from a SimpleMotorFeedforward
     */
    void controlToLinearReferenceArbitrary(double setpointMechanism_meters, double arbitraryFF_volts);

}
