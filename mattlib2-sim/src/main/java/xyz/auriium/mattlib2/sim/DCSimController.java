package xyz.auriium.mattlib2.sim;

import edu.wpi.first.math.MathShared;
import edu.wpi.first.math.MathSharedStore;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import xyz.auriium.mattlib2.IPeriodicLooped;
import xyz.auriium.mattlib2.hardware.ILinearMotor;
import xyz.auriium.mattlib2.hardware.IRotationalMotor;
import xyz.auriium.mattlib2.hardware.config.MotorComponent;

public class DCSimController implements ILinearMotor, IRotationalMotor, IPeriodicLooped {

    final DCMotorSim motorSim;
    final MotorComponent motorComponent;

    public DCSimController(DCMotorSim motorSim, MotorComponent motorComponent) {
        this.motorSim = motorSim;
        this.motorComponent = motorComponent;

        mattRegister();
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


    double rotationalOffset_encoderRotations = 0;

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
        return motorSim.getAngularPositionRotations();
    }

    @Override
    public double angularPosition_mechanismRotations() {
        return motorSim.getAngularPositionRotations() * motorComponent.encoderToMechanismCoefficient();
    }

    @Override
    public double angularPosition_normalizedMechanismRotations() {
        return 0;
    }

    @Override
    public double angularPosition_normalizedEncoderRotations() {
        return 0;
    }

    @Override
    public double angularVelocity_mechanismRotationsPerSecond() {
        return 0;
    }

    @Override
    public double angularVelocity_encoderRotationsPerSecond() {
        return 0;
    }
}
