package xyz.auriium.mattlib2.rev;

import com.revrobotics.*;
import xyz.auriium.mattlib2.IPeriodicLooped;
import xyz.auriium.mattlib2.hard.ILinearMotor;
import xyz.auriium.mattlib2.hard.IRotationalMotor;
import xyz.auriium.mattlib2.log.components.impl.CANNetworkedConfig;
import xyz.auriium.mattlib2.log.components.impl.MotorNetworkedConfig;

import java.util.Optional;


/**
 * Implementation of the spark motor
 */
class BaseSparkMotor implements ILinearMotor, IRotationalMotor, IPeriodicLooped {

    final CANSparkMax sparkMax;
    final CANNetworkedConfig canConfig;
    final MotorNetworkedConfig motorConfig;
    final RelativeEncoder encoder;

    BaseSparkMotor(CANSparkMax sparkMax, CANNetworkedConfig canConfig, MotorNetworkedConfig motorConfig, RelativeEncoder encoder) {
        this.sparkMax = sparkMax;
        this.canConfig = canConfig;
        this.motorConfig = motorConfig;
        this.encoder = encoder;
    }

    //PeriodicLooped stuff

    double outputCurrent = 0;
    double outputVoltage = 0;

    @Override
    public Optional<Exception> verifyInit() {

        REVLibError err = sparkMax.restoreFactoryDefaults();
        if (err != REVLibError.kOk) {
            return Optional.of(Exceptions.GENERIC_REV_ERROR( canConfig.selfPath() ));
        }

        REVLibError vcError = sparkMax.enableVoltageCompensation(12);
        if (vcError != REVLibError.kOk) {
            return Optional.of(Exceptions.VOLTAGE_COMPENSATION_FAILED( canConfig.selfPath() ));
        }


        return Optional.empty();
    }

    @Override
    public void robotPeriodic() {
        outputVoltage = sparkMax.getBusVoltage();
        outputCurrent = sparkMax.getOutputCurrent();
    }

    @Override
    public void logPeriodic() {
        motorConfig.logCurrentDraw(outputCurrent);
        motorConfig.logVoltageGiven(outputVoltage);
    }

    //Other stuff

    @Override
    public void setToVoltage(double voltage) {
        sparkMax.setVoltage(voltage);
    }


    @Override
    public void setToPercent(double percent_zeroToOne) {
        sparkMax.setVoltage(percent_zeroToOne * 12);
    }

    @Override
    public double reportCurrentNow_amps() {
        return outputCurrent;
    }


    @Override
    public double reportVoltageNow() {
        return outputVoltage;
    }

    @Override
    public void forceRotationalOffset(double offset_mechanismRotations) {
        double convertedEncoderPosition = offset_mechanismRotations / motorConfig.rotationToMeterCoefficient().orElseThrow();

        encoder.setPosition(convertedEncoderPosition);
    }

    @Override
    public void forceLinearOffset(double linearOffset_mechanismMeters) {
        Optional<Double> coefOptional = motorConfig.rotationToMeterCoefficient();
        if (coefOptional.isEmpty()) throw xyz.auriium.mattlib2.hard.Exceptions.MOTOR_NOT_LINEAR(motorConfig.selfPath());

        double convertedEncoderPosition = linearOffset_mechanismMeters / motorConfig.rotationToMeterCoefficient().orElseThrow() / motorConfig.encoderToMechanismCoefficient();

        encoder.setPosition(convertedEncoderPosition);
    }

    @Override
    public double angularPosition_encoderRotations() {
        return encoder.getPosition();
    }

    @Override
    public double angularPosition_mechanismRotations() {
        return encoder.getPosition() * motorConfig.encoderToMechanismCoefficient() ;
    }

    @Override
    public double angularPosition_normalizedMechanismRotations() {
        double mechanismPosition_wrapped = encoder.getPosition() * motorConfig.encoderToMechanismCoefficient() % 1;

        if (mechanismPosition_wrapped < 0) {
            mechanismPosition_wrapped += 1;
        }

        return mechanismPosition_wrapped;
    }

    @Override
    public double angularPosition_normalizedEncoderRotations() {
        double encoderPosition_wrapped = encoder.getPosition() % 1;

        if (encoderPosition_wrapped < 0) {
            encoderPosition_wrapped += 1;
        }

        return encoderPosition_wrapped;
    }

    @Override
    public double angularVelocity_mechanismRotationsPerSecond() {
        return angularvelocity_encoderRotationsPerSecond() * motorConfig.encoderToMechanismCoefficient();
    }

    @Override
    public double angularvelocity_encoderRotationsPerSecond() {
        return encoder.getVelocity() * 60d;
    }


    @Override
    public double linearPosition_mechanismMeters() {
        Optional<Double> coefOptional = motorConfig.rotationToMeterCoefficient();
        if (coefOptional.isEmpty()) throw xyz.auriium.mattlib2.hard.Exceptions.MOTOR_NOT_LINEAR(motorConfig.selfPath());
        double coef = coefOptional.orElseThrow();

        return encoder.getPosition() * coef * motorConfig.encoderToMechanismCoefficient();
    }

    @Override
    public double linearVelocity_mechanismMetersPerSecond() {
        Optional<Double> coefOptional = motorConfig.rotationToMeterCoefficient();
        if (coefOptional.isEmpty()) throw xyz.auriium.mattlib2.hard.Exceptions.MOTOR_NOT_LINEAR(motorConfig.selfPath());
        double coef = coefOptional.orElseThrow();

        return angularVelocity_mechanismRotationsPerSecond() * coef;
    }

}
