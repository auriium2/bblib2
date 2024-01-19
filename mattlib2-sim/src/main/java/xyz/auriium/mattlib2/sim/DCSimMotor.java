package xyz.auriium.mattlib2.sim;

import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import xyz.auriium.mattlib2.IPeriodicLooped;
import xyz.auriium.mattlib2.hardware.ILinearMotor;
import xyz.auriium.mattlib2.hardware.IRotationalMotor;
import xyz.auriium.mattlib2.hardware.config.MotorComponent;
import xyz.auriium.mattlib2.utils.AngleUtil;
import xyz.auriium.yuukonstants.exception.ExplainedException;

import java.util.Optional;

public class DCSimMotor implements ILinearMotor, IRotationalMotor, IPeriodicLooped {

    final DCMotorSim motorSim;
    final MotorComponent motorComponent;

    public DCSimMotor(DCMotorSim motorSim, MotorComponent motorComponent) {
        this.motorSim = motorSim;
        this.motorComponent = motorComponent;

        mattRegister();
    }

    boolean inverted = false;
    double rotationalOffset_encoderRotations = 0;

    @Override
    public Optional<ExplainedException> verifyInit() {
        motorComponent.inverted().ifPresent(b -> inverted = b);


        return Optional.empty();
    }

    @Override
    public void logicPeriodic() {
        motorSim.setInputVoltage(voltageNow);
        motorSim.update(0.02);
    }

    double voltageNow = 0;

    @Override
    public void setToVoltage(double voltage) {
        voltageNow = voltage;
        motorSim.setInputVoltage(voltage);
    }

    @Override
    public void setToPercent(double percent_zeroToOne) {
        motorSim.setInputVoltage(percent_zeroToOne * 12);
    }

    @Override
    public double reportCurrentNow_amps() {
        return motorSim.getCurrentDrawAmps();
    }

    @Override
    public double reportVoltageNow() {
        return voltageNow;
    }

    @Override
    public double reportTemperatureNow() {
        return 0;
    }


    @Override
    public void forceLinearOffset(double linearOffset_mechanismMeters) {
        rotationalOffset_encoderRotations = linearOffset_mechanismMeters
                / motorComponent.rotationToMeterCoefficient().orElseThrow(() -> xyz.auriium.mattlib2.hardware.Exceptions.MOTOR_NOT_LINEAR(motorComponent.selfPath()))
                / motorComponent.encoderToMechanismCoefficient();
    }

    @Override
    public double linearPosition_mechanismMeters() {
        double rot2linear = motorComponent.rotationToMeterCoefficient().orElseThrow(() -> xyz.auriium.mattlib2.hardware.Exceptions.MOTOR_NOT_LINEAR(motorComponent.selfPath()));

        return motorSim.getAngularPositionRotations()
                * motorComponent.encoderToMechanismCoefficient()
                * rot2linear;
    }

    @Override
    public double linearVelocity_mechanismMetersPerSecond() {
        double rot2linear = motorComponent.rotationToMeterCoefficient().orElseThrow(() -> xyz.auriium.mattlib2.hardware.Exceptions.MOTOR_NOT_LINEAR(motorComponent.selfPath()));

        return motorSim.getAngularVelocityRPM()
                * motorComponent.encoderToMechanismCoefficient()
                * rot2linear
                / 60.0; //rpm -> rps
    }

    @Override
    public void forceRotationalOffset(double offset_mechanismRotations) {
        rotationalOffset_encoderRotations = offset_mechanismRotations
                / motorComponent.encoderToMechanismCoefficient();
    }

    @Override
    public double angularPosition_encoderRotations() {
        return motorSim.getAngularPositionRotations() + rotationalOffset_encoderRotations;
    }

    @Override
    public double angularPosition_mechanismRotations() {
        return angularPosition_encoderRotations() * motorComponent.encoderToMechanismCoefficient();
    }

    @Override
    public double angularPosition_normalizedMechanismRotations() {
        return AngleUtil.normalizeRotations(angularPosition_mechanismRotations());
    }

    @Override
    public double angularPosition_normalizedEncoderRotations() {
        return AngleUtil.normalizeRotations(angularPosition_encoderRotations());
    }

    @Override
    public double angularVelocity_mechanismRotationsPerSecond() {
        return motorSim.getAngularVelocityRPM() * motorComponent.encoderToMechanismCoefficient() / 60d;
    }

    @Override
    public double angularVelocity_encoderRotationsPerSecond() {
        return motorSim.getAngularVelocityRPM() / 60d;
    }
}
