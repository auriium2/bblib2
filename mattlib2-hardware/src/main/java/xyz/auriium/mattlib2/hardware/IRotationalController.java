package xyz.auriium.mattlib2.hardware;

/**
 * What in the fuck
 * Represents a motor controller, which by definition can control actuation, likely has onboard PD control, and has linear/rotation control
 */
public interface IRotationalController extends IRotationalMotor {



    /**
     * You should call this every frame. Don't call the other version if you call this one.
     * @param setpoint_mechanismNormalizedRotations desired position of the mechanism in rotations
     */
    default void controlToNormalizedReference(double setpoint_mechanismNormalizedRotations) {
        controlToNormalizedReferenceArbitrary(setpoint_mechanismNormalizedRotations, 0);
    }

    /**
     * does pid but adds a feedforward first, for stability or something
     * @param setpoint_mechanismNormalizedRotations
     * @param arbitraryFF_volts voltage to add in, probably from a SimpleMotorFeedforward
     */
    void controlToNormalizedReferenceArbitrary(double setpoint_mechanismNormalizedRotations, double arbitraryFF_volts);


    default void controlToInfiniteReference(double setpoint_mechanismRotations) {
        controlToInfiniteReferenceArbitrary(setpoint_mechanismRotations, 0);
    }


    /**
     * does pid but adds a feedforward first, for stability or something
     * @param setpoint_mechanismRotations
     * @param arbitraryFF_volts voltage to add in, probably from a SimpleMotorFeedforward
     */
    void controlToInfiniteReferenceArbitrary(double setpoint_mechanismRotations, double arbitraryFF_volts);

}
