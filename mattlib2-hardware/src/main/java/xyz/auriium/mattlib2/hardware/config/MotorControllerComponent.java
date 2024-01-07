package xyz.auriium.mattlib2.hardware.config;

import xyz.auriium.mattlib2.log.annotation.*;
import xyz.auriium.mattlib2.log.annotation.decorator.Documented;
import yuukonfig.core.annotate.Key;

import java.util.Optional;

public interface MotorControllerComponent extends MotorComponent, PIDComponent{


    /**
     * Use this when you want to reuse a PIDcomponent or a motor component
     * Also use this if you want to load the two separately / reuse stuff
     * @param motorComponent
     * @param pidComponent
     * @return
     */
    static MotorControllerComponent ofSpecific(MotorComponent motorComponent, PIDComponent pidComponent) {
        return new Impl(motorComponent, pidComponent);
    }

    class Impl implements MotorControllerComponent {
        final MotorComponent motorComponent;
        final PIDComponent pidComponent;

        Impl(MotorComponent motorComponent, PIDComponent pidComponent) {
            this.motorComponent = motorComponent;
            this.pidComponent = pidComponent;
        }

        @Override
        @Key("p")
        @Tune
        public int pConstant() {
            return pidComponent.pConstant();
        }

        @Override
        @Key("d")
        @Tune
        public int dConstant() {
            return pidComponent.dConstant();
        }

        @Override
        @Key("i")
        @Tune
        public int iConstant() {
            return pidComponent.iConstant();
        }

        @Override
        @HasUpdated(keysToCheck = {"p", "i", "d"})
        public boolean hasUpdated() {
            return pidComponent.hasUpdated();
        }

        @Override
        @Log
        public void reportError(double error) {
            pidComponent.reportError(error);
        }

        @Override
        @Log
        public void reportOutput(double output) {
            pidComponent.reportOutput(output);
        }

        public static PIDComponent ofSpecific(CommonPIDComponent pidComponent, IndividualPIDComponent individualPIDComponent) {
            return PIDComponent.ofSpecific(pidComponent, individualPIDComponent);
        }

        @Override
        @Documented("the coefficient which converts a scalar in units of encoder rotations to mechanism rotations")
        @Conf
        public double encoderToMechanismCoefficient() {
            return motorComponent.encoderToMechanismCoefficient();
        }

        @Override
        @Documented("i have no idea what this does")
        @Conf
        public double timeCoefficient() {
            return motorComponent.timeCoefficient();
        }

        @Override
        @Documented("the coefficient that converts rotations of the mechanism to meters travelled, if this is a linear actuator")
        @Conf
        public Optional<Double> rotationToMeterCoefficient() {
            return motorComponent.rotationToMeterCoefficient();
        }

        @Override
        @Conf
        public double currentLimit() {
            return motorComponent.currentLimit();
        }

        @Override
        @Conf
        public Optional<Double> forwardLimit_mechanismRot() {
            return motorComponent.forwardLimit_mechanismRot();
        }

        @Override
        @Conf
        public Optional<Double> reverseLimit_mechanismRot() {
            return motorComponent.reverseLimit_mechanismRot();
        }

        @Override
        @Conf
        public boolean isInverted() {
            return motorComponent.isInverted();
        }

        @Override
        @Conf
        public boolean isRampRateModeEnabled() {
            return motorComponent.isRampRateModeEnabled();
        }

        @Override
        @Conf
        public boolean isBreakModeEnabled() {
            return motorComponent.isBreakModeEnabled();
        }

        @Override
        @Conf
        public boolean hasAbsoluteEncoder() {
            return motorComponent.hasAbsoluteEncoder();
        }

        @Override
        @Conf
        public int canId() {
            return motorComponent.canId();
        }

        @Override
        @Log
        public void logVoltageGiven(double voltage) {
            motorComponent.logVoltageGiven(voltage);
        }

        @Override
        @Log
        public void logCurrentDraw(double current) {
            motorComponent.logCurrentDraw(current);
        }

        public static MotorComponent ofSpecific(CommonMotorComponent common, IndividualMotorComponent individual) {
            return MotorComponent.ofSpecific(common, individual);
        }

        @Override
        @SelfPath
        public String selfPath() {
            return motorComponent.selfPath();
        }
    }

}
