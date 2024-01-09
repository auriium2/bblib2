package xyz.auriium.mattlib2.hardware.config;

import xyz.auriium.mattlib2.log.Tune;
import xyz.auriium.mattlib2.log.decorator.Documented;
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
        @xyz.auriium.mattlib2.log.Tune
        public int pConstant() {
            return pidComponent.pConstant();
        }

        @Override
        @Key("d")
        @xyz.auriium.mattlib2.log.Tune
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
        @xyz.auriium.mattlib2.log.HasUpdated(keysToCheck = {"p", "i", "d"})
        public boolean hasUpdated() {
            return pidComponent.hasUpdated();
        }

        @Override
        
        public void reportError(double error) {
            pidComponent.reportError(error);
        }

        @Override
        
        public void reportOutput(double output) {
            pidComponent.reportOutput(output);
        }

        public static PIDComponent ofSpecific(CommonPIDComponent pidComponent, IndividualPIDComponent individualPIDComponent) {
            return PIDComponent.ofSpecific(pidComponent, individualPIDComponent);
        }

        @Override
        public Type typeOfMotor() {
            return motorComponent.typeOfMotor();
        }

        @Override
        @Documented("the coefficient which converts a scalar in units of encoder rotations to mechanism rotations")
        public double encoderToMechanismCoefficient() {
            return motorComponent.encoderToMechanismCoefficient();
        }

        @Override
        @Documented("i have no idea what this does")
        
        public double timeCoefficient() {
            return motorComponent.timeCoefficient();
        }

        @Override
        @Documented("the coefficient that converts rotations of the mechanism to meters travelled, if this is a linear actuator")
        
        public Optional<Double> rotationToMeterCoefficient() {
            return motorComponent.rotationToMeterCoefficient();
        }

        @Override
        
        public Optional<Double> currentLimit() {
            return motorComponent.currentLimit();
        }

        @Override
        
        public Optional<Double> forwardLimit_mechanismRot() {
            return motorComponent.forwardLimit_mechanismRot();
        }

        @Override
        
        public Optional<Double> reverseLimit_mechanismRot() {
            return motorComponent.reverseLimit_mechanismRot();
        }

        @Override
        
        public Optional<Boolean> inverted() {
            return motorComponent.inverted();
        }

        @Override
        
        public Optional<Boolean> rampRateLimitEnabled() {
            return motorComponent.rampRateLimitEnabled();
        }

        @Override
        
        public Optional<Boolean> breakModeEnabled() {
            return motorComponent.breakModeEnabled();
        }

        @Override
        
        public Optional<Boolean> hasAbsoluteEncoder() {
            return motorComponent.hasAbsoluteEncoder();
        }

        @Override
        
        public int id() {
            return motorComponent.id();
        }

        @Override
        
        public void logVoltageGiven(double voltage) {
            motorComponent.logVoltageGiven(voltage);
        }

        @Override
        
        public void logCurrentDraw(double current) {
            motorComponent.logCurrentDraw(current);
        }

        public static MotorComponent ofSpecific(CommonMotorComponent common, IndividualMotorComponent individual) {
            return MotorComponent.ofSpecific(common, individual);
        }

        @Override
        @xyz.auriium.mattlib2.log.SelfPath
        public String selfPath() {
            return motorComponent.selfPath();
        }
    }

}
