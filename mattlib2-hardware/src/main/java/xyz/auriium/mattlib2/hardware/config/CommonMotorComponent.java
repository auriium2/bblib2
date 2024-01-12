package xyz.auriium.mattlib2.hardware.config;

import xyz.auriium.mattlib2.log.INetworkedComponent;
import xyz.auriium.mattlib2.log.annote.Conf;

import java.util.Optional;

/**
 * Commonly reused configuration details of a motor
 */
public interface CommonMotorComponent extends INetworkedComponent {

    enum Type {
        BRUSHED,
        BRUSHLESS
    }

    enum Normally {
        OPEN,
        CLOSED
    }


    /**
     *
     * @return brushless or brushed (look it up)
     */
    @Conf("type") default Type typeOfMotor() { return Type.BRUSHLESS; }
    @Conf("sensor2mechanism_coef") double encoderToMechanismCoefficient();

    @Conf("rot2meter_coef") Optional<Double> rotationToMeterCoefficient();
    @Conf("current_limit") Optional<Integer> currentLimit();
    @Conf("forward_limit") Optional<Normally> forwardLimit();
    @Conf("reverse_limit") Optional<Normally> reverseLimit();
    @Conf("forward_soft_limit") Optional<Double> forwardSoftLimit_mechanismRot();
    @Conf("reverse_soft_limit") Optional<Double> reverseSoftLimit_mechanismRot();

    @Conf("inverted") Optional<Boolean> inverted();
    @Conf("open_ramp_rate") Optional<Double> openRampRate_seconds();
    @Conf("closed_ramp_rate") Optional<Double> closedRampRate_seconds();
    @Conf("break_mode_enabled") Optional<Boolean> breakModeEnabled();
    @Conf("has_absolute") Optional<Boolean> hasAbsoluteEncoder();






}
