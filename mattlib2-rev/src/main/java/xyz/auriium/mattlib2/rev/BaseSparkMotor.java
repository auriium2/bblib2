package xyz.auriium.mattlib2.rev;

import com.revrobotics.CANSparkMax;
import com.revrobotics.REVLibError;
import com.revrobotics.RelativeEncoder;
import xyz.auriium.mattlib2.IPeriodicLooped;
import xyz.auriium.mattlib2.hardware.ILinearMotor;
import xyz.auriium.mattlib2.hardware.IRotationalMotor;
import xyz.auriium.mattlib2.hardware.config.MotorComponent;
import yuukonstants.exception.ExplainedException;

import java.util.Optional;


/**
 * Implementation of the spark motor
 */
class BaseSparkMotor implements ILinearMotor, IRotationalMotor, IPeriodicLooped {

    final CANSparkMax sparkMax;
    final MotorComponent motorComponent;
    final RelativeEncoder encoder;

    BaseSparkMotor(CANSparkMax sparkMax, MotorComponent motorComponent, RelativeEncoder encoder) {
        this.sparkMax = sparkMax;
        this.motorComponent = motorComponent;
        this.encoder = encoder;

        mattRegister();
    }

    //PeriodicLooped stuff

    double outputCurrent = 0;
    double outputVoltage = 0;
    double temperature = 0;

    @Override
    public Optional<ExplainedException> verifyInit() {

        REVLibError err = sparkMax.restoreFactoryDefaults();
        if (err != REVLibError.kOk) {
            return Optional.of(Exceptions.GENERIC_REV_ERROR( motorComponent.selfPath() ));
        }

        REVLibError vcError = sparkMax.enableVoltageCompensation(12);
        if (vcError != REVLibError.kOk) {
            return Optional.of(Exceptions.VOLTAGE_COMPENSATION_FAILED( motorComponent.selfPath() ));
        }



        return Optional.empty();
    }

    @Override
    public void logicPeriodic() {
        outputVoltage = sparkMax.getBusVoltage();
        outputCurrent = sparkMax.getOutputCurrent();
        temperature = sparkMax.getMotorTemperature();
    }

    @Override
    public void logPeriodic() {
        motorComponent.logCurrentDraw(outputCurrent);
        motorComponent.logVoltageGiven(outputVoltage);
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
    public double reportTemperatureNow() {
        return temperature;
    }

    @Override
    public void forceRotationalOffset(double offset_mechanismRotations) {
        double convertedEncoderPosition = offset_mechanismRotations / motorComponent.rotationToMeterCoefficient().orElseThrow();

        encoder.setPosition(convertedEncoderPosition);
    }

    @Override
    public void forceLinearOffset(double linearOffset_mechanismMeters) {
        Optional<Double> coefOptional = motorComponent.rotationToMeterCoefficient();
        if (coefOptional.isEmpty()) throw xyz.auriium.mattlib2.hardware.Exceptions.MOTOR_NOT_LINEAR(motorComponent.selfPath());

        double convertedEncoderPosition = linearOffset_mechanismMeters / motorComponent.rotationToMeterCoefficient().orElseThrow() / motorComponent.encoderToMechanismCoefficient();

        encoder.setPosition(convertedEncoderPosition);
    }

    @Override
    public double angularPosition_encoderRotations() {
        return encoder.getPosition();
    }

    @Override
    public double angularPosition_mechanismRotations() {
        return encoder.getPosition() * motorComponent.encoderToMechanismCoefficient() ;
    }

    @Override
    public double angularPosition_normalizedMechanismRotations() {
        double mechanismPosition_wrapped = encoder.getPosition() * motorComponent.encoderToMechanismCoefficient() % 1;

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
        return angularvelocity_encoderRotationsPerSecond() * motorComponent.encoderToMechanismCoefficient();
    }

    @Override
    public double angularvelocity_encoderRotationsPerSecond() {
        return encoder.getVelocity() * 60d;
    }


    @Override
    public double linearPosition_mechanismMeters() {
        Optional<Double> coefOptional = motorComponent.rotationToMeterCoefficient();
        if (coefOptional.isEmpty()) throw xyz.auriium.mattlib2.hardware.Exceptions.MOTOR_NOT_LINEAR(motorComponent.selfPath());
        double coef = coefOptional.orElseThrow();

        return encoder.getPosition() * coef * motorComponent.encoderToMechanismCoefficient();
    }

    @Override
    public double linearVelocity_mechanismMetersPerSecond() {
        Optional<Double> coefOptional = motorComponent.rotationToMeterCoefficient();
        if (coefOptional.isEmpty()) throw xyz.auriium.mattlib2.hardware.Exceptions.MOTOR_NOT_LINEAR(motorComponent.selfPath());
        double coef = coefOptional.orElseThrow();

        return angularVelocity_mechanismRotationsPerSecond() * coef;
    }

    @Override
    public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
        if (clazz == CANSparkMax.class) {
            return clazz.cast( sparkMax );
        }

        throw new UnsupportedOperationException("no such type");
    }
}
