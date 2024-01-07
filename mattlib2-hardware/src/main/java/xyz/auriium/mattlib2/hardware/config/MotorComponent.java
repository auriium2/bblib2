package xyz.auriium.mattlib2.hardware.config;

import java.util.Optional;

/**
 * Load your config as this when you don't want to reuse your motor configs
 */
public interface MotorComponent extends IndividualMotorComponent, CommonMotorComponent {


    /**
     * Use this when you want to reuse the common motor stuff, but not the interesting stuff (motor ids, etc)
     * @param individual
     * @param common
     * @return
     */
    static MotorComponent ofSpecific(CommonMotorComponent common, IndividualMotorComponent individual) {
        return new Impl(individual, common);
    }

    static MotorComponent[] ofRange(CommonMotorComponent common, IndividualMotorComponent[] individuals) {
        MotorComponent[] motorComponents = new MotorComponent[individuals.length];

        for (int i = 0; i < individuals.length; i++) {
            motorComponents[i] = ofSpecific(common, individuals[i]);
        }

        return motorComponents;
    }

    class Impl implements MotorComponent {

        final IndividualMotorComponent individualMotorComponent;
        final CommonMotorComponent commonMotorComponent;

        Impl(IndividualMotorComponent individualMotorComponent, CommonMotorComponent commonMotorComponent) {
            this.individualMotorComponent = individualMotorComponent;
            this.commonMotorComponent = commonMotorComponent;
        }

        @Override
        public int canId() {
            return individualMotorComponent.canId();
        }

        @Override
        public void logVoltageGiven(double voltage) {
            individualMotorComponent.logVoltageGiven(voltage);
        }

        @Override
        public void logCurrentDraw(double current) {
            individualMotorComponent.logCurrentDraw(current);
        }

        @Override
        public double encoderToMechanismCoefficient() {
            return commonMotorComponent.encoderToMechanismCoefficient();
        }

        @Override
        public double timeCoefficient() {
            return commonMotorComponent.timeCoefficient();
        }

        @Override
        public Optional<Double> rotationToMeterCoefficient() {
            return commonMotorComponent.rotationToMeterCoefficient();
        }

        @Override
        public double currentLimit() {
            return commonMotorComponent.currentLimit();
        }

        @Override
        public Optional<Double> forwardLimit_mechanismRot() {
            return commonMotorComponent.forwardLimit_mechanismRot();
        }

        @Override
        public Optional<Double> reverseLimit_mechanismRot() {
            return commonMotorComponent.reverseLimit_mechanismRot();
        }

        @Override
        public boolean isInverted() {
            return commonMotorComponent.isInverted();
        }

        @Override
        public boolean isRampRateModeEnabled() {
            return commonMotorComponent.isRampRateModeEnabled();
        }

        @Override
        public boolean isBreakModeEnabled() {
            return commonMotorComponent.isBreakModeEnabled();
        }

        @Override
        public boolean hasAbsoluteEncoder() {
            return commonMotorComponent.hasAbsoluteEncoder();
        }

        @Override
        public String selfPath() {
            return individualMotorComponent.selfPath();
        }
    }

}
