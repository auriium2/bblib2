package xyz.auriium.mattlib2.rev;

import com.revrobotics.CANSparkBase;
import com.revrobotics.REVLibError;
import xyz.auriium.mattlib2.Mattlib2Exception;
import xyz.auriium.yuukonstants.GenericPath;
import xyz.auriium.yuukonstants.exception.ExplainedException;

import static java.lang.String.format;

public class Exceptions {

    public static ExplainedException REV_ERROR(REVLibError code, GenericPath path) {
        throw new Mattlib2Exception(
                "revError",
                format("The rev library threw an error with status [%s] and status code [%s] when configuring motor [%s]", code.name(), code.value, path.tablePath()),
                "Try looking up the error on a site called Google.com"
        );
    }

    public static ExplainedException REV_FAULT(CANSparkBase.FaultID fault, GenericPath path) {
        throw new Mattlib2Exception(
                "revFault",
                format("The rev library reports a fault with status [%s] and status code [%s] when checking motor [%s]", fault.name(), fault.value, path.tablePath()),
                "Try looking up the error on a site called Google.com"
        );
    }

    public static final Mattlib2Exception CANNOT_EXTERNAL_FEEDBACK_INTERNAL = new Mattlib2Exception(
            "rev/cannotExternalFeedbackInternal",
            "you tried to use PD control on a motor but provided an external sensor feedback. This normally works, except this motor is configured to use the internal pid controller which does not support using external encoder values",
            "do not try to use the external value supply when you command this motor to a reference"
    );

    public static Mattlib2Exception MUST_DEFINE_LIMIT_TYPE(GenericPath path) {
        return new Mattlib2Exception(
                "rev/mustDefineLimitType",
                format("the motor at [%s] is using limit switches, but does not define the field <switch_normally> to tell whether the limit switch is normally open or not", path.tablePath()),
                "please add the field switch_normally to the config along with whether the limit switch is normally open or not"
        );
    }




}
