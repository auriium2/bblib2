package xyz.auriium.mattlib2.hardware;


import xyz.auriium.mattlib2.Mattlib2Exception;
import xyz.auriium.yuukonstants.GenericPath;

import static java.lang.String.format;

public class Exceptions {

    public static Mattlib2Exception NO_SIMULATION_COEFFICIENTS(GenericPath path, String coef) {
        throw new Mattlib2Exception(
                "hardware/noSimulationCoefficients",
                format("the motor at path [%s] was used in a simulation but did not have the required constant [%s] required for the math behind that simulation", path.tablePath(), coef),
                "please add that constant to the config of that motor"
        );
    }

    public static Mattlib2Exception DUPLICATE_IDS_FOUND(GenericPath pathWhoAsked, int idDupe, GenericPath pathWhoIs) {
        throw new Mattlib2Exception(
                "hardware/duplicateIdsFound",
                format("the motor at path [%s] tried to load can id [%s], but the motor at path [%s] already loaded that can id. If the motors in this error message are the same something horrible has happened", pathWhoAsked.tablePath(), idDupe, pathWhoIs.tablePath()),
                "change the id of the second motor"
        );
    }

    public static final Mattlib2Exception NO_BUILT_IN_ENCODER = new Mattlib2Exception(
            "hardware/noBuiltInEncoder",
            "a pd controller tried to use a built in encoder to define the reference point, but was constructed without any encoder.",
            "either use the withEncoder constructor or call moveToReference with an encoder readout"
    );

    public static Mattlib2Exception MOTOR_NOT_LINEAR(GenericPath motor) {
        return new Mattlib2Exception(
                "hardware/motorNotLinear",
                format("Motor [%s] is linear. This means it needs to rotationToMeterCoef", motor.tablePath()),
                "either add a rotationToMeter coefficient to allow the motor to be considered for linear functions, or use purely rotational functions instead"
        );
    }

    public static Mattlib2Exception NO_CAN_ID(GenericPath path, int id) {
        return new Mattlib2Exception(
                "hardware/noSuchCanID",
                "The motor at path [" + path.tablePath() + "] was configured to use can id " + id +", but no such can id could be found on the can bus",
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


    public static Mattlib2Exception PERCENT_DOMAIN_ERROR(GenericPath path) {
        return new Mattlib2Exception(
                "hardware/percentDomainError",
                format("a call to [%s] used percent mode with a number outside the range 0-1", path.tablePath()),
                "make sure calls to percent mode motors are within range 0-1"
        );
    }
}
