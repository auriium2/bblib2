package xyz.auriium.mattlib2.rev;

import com.revrobotics.*;
import xyz.auriium.mattlib2.IPeriodicLooped;
import xyz.auriium.mattlib2.hardware.ILinearMotor;
import xyz.auriium.mattlib2.hardware.IRotationalMotor;
import xyz.auriium.mattlib2.hardware.config.CommonMotorComponent;
import xyz.auriium.mattlib2.hardware.config.MotorComponent;
import xyz.auriium.mattlib2.utils.AngleUtil;
import xyz.auriium.yuukonstants.exception.ExplainedException;

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
        //System.out.println("register called on " + " with inverted: " + motorComponent.selfPath());
    }

    boolean isInverted = false;

    //PeriodicLooped stuff

    double outputCurrent = 0;
    double outputVoltage = 0;
    double temperature = 0;

    @Override
    public Optional<ExplainedException> verifyInit() {

        System.out.println("ITS BEING RUN YAAAAAAY");

        //if (true) throw new IllegalStateException("VERY SURE ITS BEING RUN");

        isInverted = motorComponent.inverted().orElse(false);
        System.out.println(motorComponent.selfPath().tablePath() + " is inverted?: " + isInverted);

        REVLibError err = sparkMax.restoreFactoryDefaults();
        if (err != REVLibError.kOk) {
            return Optional.of(Exceptions.GENERIC_REV_ERROR( motorComponent.selfPath().tablePath() ));
        }

        REVLibError vcError = sparkMax.enableVoltageCompensation(12);
        if (vcError != REVLibError.kOk) {
            return Optional.of(Exceptions.VOLTAGE_COMPENSATION_FAILED( motorComponent.selfPath().tablePath() ));
        }

        //cyrrent limits
        motorComponent.currentLimit().ifPresent(sparkMax::setSmartCurrentLimit);

        //hard and soft limits

        motorComponent.forwardLimit().ifPresent(normally -> {
            SparkLimitSwitch.Type type;
            if (normally == CommonMotorComponent.Normally.CLOSED) {
                type = SparkLimitSwitch.Type.kNormallyClosed;
            } else {
                type = SparkLimitSwitch.Type.kNormallyOpen;
            }
            sparkMax.getForwardLimitSwitch(type).enableLimitSwitch(true);
        });
        motorComponent.reverseLimit().ifPresent(normally -> {
            SparkLimitSwitch.Type type;
            if (normally == CommonMotorComponent.Normally.CLOSED) {
                type = SparkLimitSwitch.Type.kNormallyClosed;
            } else {
                type = SparkLimitSwitch.Type.kNormallyOpen;
            }
            sparkMax.getReverseLimitSwitch(type).enableLimitSwitch(true);
        });


        motorComponent.forwardSoftLimit_mechanismRot().ifPresent(limit -> {
            sparkMax.setSoftLimit(CANSparkBase.SoftLimitDirection.kForward, (float) (limit * motorComponent.encoderToMechanismCoefficient()));
            sparkMax.enableSoftLimit(CANSparkBase.SoftLimitDirection.kForward, true);
        });
        motorComponent.reverseSoftLimit_mechanismRot().ifPresent(limit -> {
            sparkMax.setSoftLimit(CANSparkBase.SoftLimitDirection.kReverse, (float) (limit * motorComponent.encoderToMechanismCoefficient()));
            sparkMax.enableSoftLimit(CANSparkBase.SoftLimitDirection.kReverse, true);
        });
        motorComponent.breakModeEnabled().ifPresent(breakMode -> {
            sparkMax.setIdleMode(breakMode ? CANSparkBase.IdleMode.kBrake : CANSparkBase.IdleMode.kCoast);
        });

        motorComponent.openRampRate_seconds().ifPresent(sparkMax::setOpenLoopRampRate);
        motorComponent.closedRampRate_seconds().ifPresent(sparkMax::setClosedLoopRampRate);







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

        System.out.println("PENIS GUY");
        motorComponent.reportCurrentDraw(outputCurrent);
        motorComponent.reportVoltageGiven(outputVoltage);
    }

    //Other stuff

    @Override
    public void setToVoltage(double voltage) {
        double sign = isInverted ? -1d : 1d;

        sparkMax.setVoltage(sign * voltage);
    }


    @Override
    public void setToPercent(double percent_zeroToOne) {
        double sign = isInverted ? -1d : 1d;

        sparkMax.setVoltage(sign * percent_zeroToOne * 12);
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
        return AngleUtil.normalizeRotations(angularPosition_mechanismRotations());
    }

    @Override
    public double angularPosition_normalizedEncoderRotations() {
        return AngleUtil.normalizeRotations(angularPosition_encoderRotations());
    }

    @Override
    public double angularVelocity_mechanismRotationsPerSecond() {
        return angularVelocity_encoderRotationsPerSecond() * motorComponent.encoderToMechanismCoefficient();
    }

    @Override
    public double angularVelocity_encoderRotationsPerSecond() {
        return encoder.getVelocity() / 60d;
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
