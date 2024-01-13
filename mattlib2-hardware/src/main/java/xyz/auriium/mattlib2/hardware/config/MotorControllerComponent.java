package xyz.auriium.mattlib2.hardware.config;

import xyz.auriium.mattlib2.log.annote.HasUpdated;
import xyz.auriium.mattlib2.log.annote.SelfPath;
import xyz.auriium.mattlib2.log.decorator.Documented;
import yuukonfig.core.annotate.Key;
import yuukonstants.GenericPath;

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
        public int pConstant() {
            return pidComponent.pConstant();
        }

        @Override
        public int dConstant() {
            return pidComponent.dConstant();
        }

        @Override
        public int iConstant() {
            return pidComponent.iConstant();
        }

        @Override
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
        public double encoderToMechanismCoefficient() {
            return motorComponent.encoderToMechanismCoefficient();
        }

        @Override
        public Optional<Double> rotationToMeterCoefficient() {
            return motorComponent.rotationToMeterCoefficient();
        }

        @Override
        public Optional<Integer> currentLimit() {
            return motorComponent.currentLimit();
        }

        @Override
        public Optional<Normally> forwardLimit() {
            return Optional.empty();
        }

        @Override
        public Optional<Normally> reverseLimit() {
            return Optional.empty();
        }

        @Override
        
        public Optional<Double> forwardSoftLimit_mechanismRot() {
            return motorComponent.forwardSoftLimit_mechanismRot();
        }

        @Override
        
        public Optional<Double> reverseSoftLimit_mechanismRot() {
            return motorComponent.reverseSoftLimit_mechanismRot();
        }

        @Override
        
        public Optional<Boolean> inverted() {
            return motorComponent.inverted();
        }

        @Override
        
        public Optional<Double> openRampRate_seconds() {
            return motorComponent.openRampRate_seconds();
        }

        @Override
        public Optional<Double> closedRampRate_seconds() {
            return motorComponent.closedRampRate_seconds();
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
        
        public void reportVoltageGiven(double voltage) {
            motorComponent.reportVoltageGiven(voltage);
        }

        @Override
        
        public void reportCurrentDraw(double current) {
            motorComponent.reportCurrentDraw(current);
        }

        @Override
        public void reportTemperature(double temperatureCelsius) {
            motorComponent.reportTemperature(temperatureCelsius);
        }

        public static MotorComponent ofSpecific(CommonMotorComponent common, IndividualMotorComponent individual) {
            return MotorComponent.ofSpecific(common, individual);
        }

        @Override
        @SelfPath
        public GenericPath selfPath() {
            return motorComponent.selfPath();
        }
    }

}
