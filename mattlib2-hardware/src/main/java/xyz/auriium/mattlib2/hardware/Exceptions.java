package xyz.auriium.mattlib2.hardware;


import xyz.auriium.mattlib2.Mattlib2Exception;

import static java.lang.String.format;

public class Exceptions {

    public static final Mattlib2Exception NO_BUILT_IN_ENCODER = new Mattlib2Exception(
            "hardware/noBuiltInEncoder",
            "a pd controller tried to use a built in encoder to define the reference point, but was constructed without any encoder.",
            "either use the withEncoder constructor or call moveToReference with an encoder readout"
    );

    public static final Mattlib2Exception MOTOR_NOT_LINEAR(String motor) {
        return new Mattlib2Exception(
                "hardware/motorNotLinear",
                format("Motor %s was configured as purely rotational, and did not have a rotationToMeter coefficient, however, somebody tried to use a function with the word Linear in it, which needs the rotationToMeter coefficient to convert correctly.", motor),
                "either add a rotationToMeter coefficient to allow the motor to be considered for linear functions, or use purely rotational functions instead"
        );
    }

    public static Mattlib2Exception NO_CAN_ID(String path, int id) {
        return new Mattlib2Exception(
                "hardware/noSuchCanID",
                "The motor at path " + path + "was configured to use can id " + id +", but no such can id could be found on the can bus",
                "fix the can id of the device, or find the actual id of the device and change the config to this"
        );
    }

    public static Mattlib2Exception CANNOT_FORCE_ABSOLUTE = new Mattlib2Exception(
            "hardware/cannotForceAbsolute",
            "Absolute motors cannot be forced to an offset, yet you seem to have tried to do just that",
            "do not call forceOffset on an absolute encoder"
    );

    public static Mattlib2Exception CANNOT_VELOCITY_ABSOLUTE = new Mattlib2Exception(
            "hardware/cannotVelocityAbsolute",
            "Absolute motors cannot report a velocity, yet you seem to have tried to do just that",
            "do not call any velocity functions on an absolute encoder"
    );

}
