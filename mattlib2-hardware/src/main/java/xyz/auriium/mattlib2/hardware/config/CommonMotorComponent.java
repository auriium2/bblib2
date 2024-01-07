package xyz.auriium.mattlib2.hardware.config;

import xyz.auriium.mattlib2.log.INetworkedComponent;
import xyz.auriium.mattlib2.log.annotation.Conf;
import xyz.auriium.mattlib2.log.annotation.decorator.Documented;

import java.util.Optional;

/**
 * Commonly reused configuration details of a motor
 */
public interface CommonMotorComponent extends INetworkedComponent {



    @Conf() @Documented("the coefficient which converts a scalar in units of encoder rotations to mechanism rotations")
    double encoderToMechanismCoefficient();

    @Conf() @Documented("i have no idea what this does")
    double timeCoefficient();
    @Conf() @Documented("the coefficient that converts rotations of the mechanism to meters travelled, if this is a linear actuator")
    Optional<Double> rotationToMeterCoefficient();

    @Conf()
    double currentLimit();
    @Conf()
    Optional<Double> forwardLimit_mechanismRot();
    @Conf()
    Optional<Double> reverseLimit_mechanismRot();

    @Conf()
    boolean isInverted();
    @Conf()
    boolean isRampRateModeEnabled();
    @Conf()
    boolean isBreakModeEnabled();
    @Conf()
    boolean hasAbsoluteEncoder();






}
