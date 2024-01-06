package xyz.auriium.mattlib2.log.components.impl;

import xyz.auriium.mattlib2.log.annotation.Alert;
import xyz.auriium.mattlib2.log.annotation.Conf;
import xyz.auriium.mattlib2.log.annotation.Log;
import xyz.auriium.mattlib2.log.annotation.Tune;
import xyz.auriium.mattlib2.log.annotation.decorator.Documented;
import xyz.auriium.mattlib2.log.components.INetworkedConfig;

import java.util.Optional;

/**
 * A Matt//Log Component which describes an arbitrary motor. It tunes and configures useful coefficients related to motors.
 */
public interface MotorNetworkedConfig extends INetworkedConfig {

    //configuration

    @Conf() @Documented("the coefficient which converts a scalar in units of encoder rotations to mechanism rotations")
    double encoderToMechanismCoefficient();

    @Conf() @Documented("i have no idea what this does")
    double timeCoefficient();
    @Conf() @Documented("the coefficient that converts rotations of the mechanism to meters travelled, if this is a linear actuator")
    Optional<Double> rotationToMeterCoefficient();

    @Conf()
    double currentLimit();
    @Tune()
    Optional<Double> forwardLimit_mechanismRot();
    @Tune()
    Optional<Double> reverseLimit_mechanismRot();

    @Tune()
    boolean isInverted();
    @Conf()
    boolean isRampRateModeEnabled();
    @Conf()
    boolean isBreakModeEnabled();
    @Conf()
    boolean hasAbsoluteEncoder();


    @Log
    void logVoltageGiven(double voltage);

    @Log
    void logCurrentDraw(double current);

    @Alert("The motor is overheating!")
    void overheatAlert();

    @Alert("A tuned value caused a motor to break!")
    void invalidTuneAlert();

    @Alert("The motor is stalling!")
    void stallAlert();

    @Alert("The motor is over rated current!")
    void currentAlert();

    @Alert("A brownout has caused this motor to cry!")
    void brownoutAlert();


}
