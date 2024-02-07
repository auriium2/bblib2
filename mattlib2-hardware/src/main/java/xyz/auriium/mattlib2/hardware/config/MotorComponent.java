package xyz.auriium.mattlib2.hardware.config;

import xyz.auriium.mattlib2.Mattlib;
import xyz.auriium.yuukonstants.GenericPath;

import java.util.Optional;

/**
 * Load your config as this when you don't want to reuse your motor configs
 * To see what config data this loads, check out {@link CommonMotorComponent} and {@link IndividualMotorComponent}
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
    /**
     * @param common
     * @param individuals
     * @return an array of motorcomponents indexed by the respective individual motor component indices, all supplied by a common motorcomponent
     */
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
        public int id() {
            return individualMotorComponent.id();
        }

        @Override
        public void reportVoltageGiven(double voltage) {
            individualMotorComponent.reportVoltageGiven(voltage);
        }

        @Override
        public void reportCurrentDraw(double current) {
            individualMotorComponent.reportCurrentDraw(current);
        }

        @Override public void reportTemperature(double temperatureCelsius) {
            individualMotorComponent.reportTemperature(temperatureCelsius);
        }

        @Override public void reportMechanismRotations(double mechanismRotations) {
            individualMotorComponent.reportMechanismRotations(mechanismRotations);
        }


        @Override public void reportMechanismVelocity(double mechanismRotationsPerSecond) {
            individualMotorComponent.reportMechanismVelocity(mechanismRotationsPerSecond);
        }


        @Override
        public Optional<Type> typeOfMotor() {
            return commonMotorComponent.typeOfMotor();
        }

        @Override
        public double encoderToMechanismCoefficient() {
            return commonMotorComponent.encoderToMechanismCoefficient();
        }

        @Override
        public Optional<Double> rotationToMeterCoefficient() {
            return commonMotorComponent.rotationToMeterCoefficient();
        }

        @Override
        public Optional<Integer> currentLimit() {
            return commonMotorComponent.currentLimit();
        }

        @Override
        public Optional<Normally> forwardLimit() {
            return commonMotorComponent.forwardLimit();
        }

        @Override
        public Optional<Normally> reverseLimit() {
            return commonMotorComponent.reverseLimit();
        }

        @Override
        public Optional<Double> forwardSoftLimit_mechanismRot() {
            return commonMotorComponent.forwardSoftLimit_mechanismRot();
        }

        @Override
        public Optional<Double> reverseSoftLimit_mechanismRot() {
            return commonMotorComponent.reverseSoftLimit_mechanismRot();
        }

        @Override
        public Optional<Boolean> inverted() {
            return commonMotorComponent.inverted();
        }

        @Override
        public Optional<Double> openRampRate_seconds() {
            return commonMotorComponent.openRampRate_seconds();
        }

        @Override
        public Optional<Double> closedRampRate_seconds() {
            return commonMotorComponent.closedRampRate_seconds();
        }

        @Override
        public Optional<Boolean> breakModeEnabled() {
            return commonMotorComponent.breakModeEnabled();
        }

        @Override
        public Optional<Boolean> hasAbsoluteEncoder() {
            return commonMotorComponent.hasAbsoluteEncoder();
        }

        @Override public Optional<Double> massMomentInertia() {
            return commonMotorComponent.massMomentInertia();
        }

        @Override public Optional<Double> positionStandardDeviation() {
            return commonMotorComponent.positionStandardDeviation();
        }

        @Override public Optional<Double> velocityStandardDeviation() {
            return commonMotorComponent.velocityStandardDeviation();
        }

        @Override
        public GenericPath selfPath() {
            return individualMotorComponent.selfPath();
        }
    }

}
