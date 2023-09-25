package xyz.auriium.mattlib2.components.impl;

import xyz.auriium.mattlib2.Unit;
import xyz.auriium.mattlib2.annotation.Alert;
import xyz.auriium.mattlib2.annotation.Conf;
import xyz.auriium.mattlib2.annotation.Log;
import xyz.auriium.mattlib2.annotation.Tune;
import xyz.auriium.mattlib2.annotation.decorator.Documented;
import xyz.auriium.mattlib2.annotation.decorator.UnitsHint;
import xyz.auriium.mattlib2.components.IComponent;

import java.util.Optional;

/**
 * A Matt//Log Component which describes an arbitrary motor. It tunes and configures useful coefficients related to motors.
 */
public interface MotorComponent extends IComponent {

    //configuration

    @Conf() @Documented("the coefficient which converts a scalar in units of encoder rotations to mechanism rotations")
    double encoderToMechanismCoefficient();

    @Conf() @Documented("i have no idea what this does")
    double timeCoefficient();
    @Conf() @Documented("the coefficient that converts rotations of the mechanism to meters travelled, if this is a linear actuator")
    Optional<Double> rotationToMeterCoefficient();

    @Conf()
    double currentLimit();
    @Tune() @UnitsHint(Unit.MECHANISM_ROTATIONS)
    Optional<Double> forwardLimit_mechanismRot();
    @Tune() @UnitsHint(Unit.MECHANISM_ROTATIONS)
    Optional<Double> reverseLimit_mechanismRot();

    @Tune()
    boolean isInverted();
    @Conf()
    boolean isRampRateModeEnabled();
    @Conf()
    boolean isBreakModeEnabled();
    @Conf()
    boolean hasAbsoluteEncoder();


    //reporting

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
