package xyz.auriium.mattlib2.rev;

import xyz.auriium.mattlib2.Mattlib2Exception;
import yuukonstants.GenericPath;

import static java.lang.String.format;

public class Exceptions {

    public static final Mattlib2Exception VOLTAGE_COMPENSATION_FAILED(String motor) {
        return new Mattlib2Exception(
                "rev/voltageCompensationFailed",
                format("something went wrong setting up voltage compensation for the motor %s", motor),
                "panic and cry"
        );
    }
    public static final Mattlib2Exception GENERIC_REV_ERROR(String motor) {
        return new Mattlib2Exception(
                "rev/genericRevError",
                format("something went wrong setting up a rev config command for the motor %s", motor),
                "contact matt"
        );
    }

    public static final Mattlib2Exception CANNOT_EXTERNAL_FEEDBACK_INTERNAL = new Mattlib2Exception(
            "rev/cannotExternalFeedbackInternal",
            "you tried to use PD control on a motor but provided an external sensor feedback. This normally works, except this motor is configured to use the internal pid controller which does not support using external encoder values",
            "do not try to use the external value supply when you command this motor to a reference"
    );

    public static final Mattlib2Exception MUST_DEFINE_LIMIT_TYPE(GenericPath path) {
        return new Mattlib2Exception(
                "rev/mustDefineLimitType",
                format("the motor at [%s] is using limit switches, but does not define the field <switch_normally> to tell whether the limit switch is normally open or not", path.getAsTablePath()),
                "please add the field switch_normally to the config along with whether the limit switch is normally open or not"
        );
    }




}
