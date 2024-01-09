package xyz.auriium.mattlib2.hardware.config;

import xyz.auriium.mattlib2.log.INetworkedComponent;
import xyz.auriium.mattlib2.log.Conf;

import java.util.Optional;

/**
 * Commonly reused configuration details of a motor
 */
public interface CommonMotorComponent extends INetworkedComponent {

    enum Type {
        BRUSHED,
        BRUSHLESS
    }

    @Conf("type")
    Type typeOfMotor();

    @Conf("sensor2mechanism_coef")
    double encoderToMechanismCoefficient();

    @Conf("time_coef")
    double timeCoefficient();

    @Conf("rot2meter_coef")
    Optional<Double> rotationToMeterCoefficient();

    @Conf("current_limit")
    Optional<Double> currentLimit();

    @Conf("forward_limit")
    Optional<Double> forwardLimit_mechanismRot();
    @Conf("reverse_limit")
    Optional<Double> reverseLimit_mechanismRot();

    @Conf("inverted")
    Optional<Boolean> inverted();
    @Conf("ramp_limit_enabled")
    Optional<Boolean> rampRateLimitEnabled();
    @Conf("break_mode_enabled")
    Optional<Boolean> breakModeEnabled();
    @Conf("has_absolute")
    Optional<Boolean> hasAbsoluteEncoder();






}
