package xyz.auriium.mattlib2.ctre;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.motorcontrol.Talon;
import xyz.auriium.mattlib2.IPeriodicLooped;
import xyz.auriium.mattlib2.hard.ILinearMotor;
import xyz.auriium.mattlib2.hard.IRotationalMotor;
import xyz.auriium.mattlib2.log.components.impl.CANNetworkedConfig;
import xyz.auriium.mattlib2.log.components.impl.MotorNetworkedConfig;

class BaseTalonMotor implements ILinearMotor, IRotationalMotor, IPeriodicLooped {

    final Talon talon;
    final CANNetworkedConfig canConfig;
    final MotorNetworkedConfig motorConfig;


    BaseTalonMotor(Talon talon, CANNetworkedConfig canConfig, MotorNetworkedConfig motorConfig) {
        this.talon = talon;
        this.canConfig = canConfig;
        this.motorConfig = motorConfig;
    }

    final
    @Override
    public void setToVoltage(double voltage) {
    }

    @Override
    public void setToPercent(double percent_zeroToOne) {

    }

    @Override
    public double reportCurrentNow_amps() {
        return 0;
    }

    @Override
    public double reportVoltageNow() {
        return 0;
    }

    @Override
    public void forceLinearOffset(double linearOffset_mechanismMeters) {

    }

    @Override
    public double linearPosition_mechanismMeters() {
        return 0;
    }

    @Override
    public double linearVelocity_mechanismMetersPerSecond() {
        return 0;
    }

    @Override
    public void forceRotationalOffset(double offset_mechanismRotations) {

    }

    @Override
    public double angularPosition_encoderRotations() {
        return 0;
    }

    @Override
    public double angularPosition_mechanismRotations() {
        return 0;
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
    public double angularvelocity_encoderRotationsPerSecond() {
        return 0;
    }
}
