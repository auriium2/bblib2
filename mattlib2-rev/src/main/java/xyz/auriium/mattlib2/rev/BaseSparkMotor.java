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
    }

    double linearCoef = 0;
    boolean linearCoefSet = false;

    //PeriodicLooped stuff

    double outputCurrent = 0;
    double outputVoltage = 0;
    double temperature = 0;

    double loadLinearCoef() {
        if (!linearCoefSet) {
            linearCoefSet = true;
            Optional<Double> coefOptional = motorComponent.rotationToMeterCoefficient();
            if (coefOptional.isEmpty()) throw xyz.auriium.mattlib2.hardware.Exceptions.MOTOR_NOT_LINEAR(motorComponent.selfPath());
            linearCoef = coefOptional.get();
        }

        return linearCoef;
    }

    @Override
    public Optional<ExplainedException> verifyInit() {
        REVLibError err = sparkMax.restoreFactoryDefaults();
        if (err != REVLibError.kOk) {
            return Optional.of(Exceptions.GENERIC_REV_ERROR( motorComponent.selfPath().tablePath() ));
        }

        //NEVER PUYT FUCKING CODE BEHIND THIS LINE BECAUSE IF YOU DO IT WILL WASTE <4> hours
        //CHANGE THIS NUMBER WHEN YOUR TIME GETS WASTED

        boolean isInverted = motorComponent.inverted().orElse(false);
        sparkMax.setInverted(isInverted);

        encoder.setPositionConversionFactor(motorComponent.encoderToMechanismCoefficient());
        encoder.setVelocityConversionFactor(motorComponent.encoderToMechanismCoefficient() / 60.0); //divide by 60 to get rotations per second


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
        outputVoltage = sparkMax.getAppliedOutput();
        outputCurrent = sparkMax.getOutputCurrent();
        temperature = sparkMax.getMotorTemperature();
    }

    @Override
    public void logPeriodic() {

        motorComponent.reportCurrentDraw(outputCurrent);
        motorComponent.reportVoltageGiven(outputVoltage);
        motorComponent.reportTemperature(outputVoltage);
        motorComponent.reportMechanismRotations(angularPosition_mechanismRotations());
        motorComponent.reportMechanismRotationsBound(angularPosition_normalizedMechanismRotations());

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
        encoder.setPosition(offset_mechanismRotations);
    }

    @Override
    public void forceLinearOffset(double linearOffset_mechanismMeters) {
        double convertedEncoderPosition = linearOffset_mechanismMeters / loadLinearCoef();
        encoder.setPosition(convertedEncoderPosition);
    }

    @Override
    public double angularPosition_encoderRotations() {
        return encoder.getPosition() / motorComponent.encoderToMechanismCoefficient();
    }

    @Override
    public double angularPosition_mechanismRotations() {
        return encoder.getPosition();
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
        return encoder.getVelocity();
    }

    @Override
    public double angularVelocity_encoderRotationsPerSecond() {
        throw new UnsupportedOperationException("cant tell you bc factor");
    }

    @Override
    public double linearPosition_mechanismMeters() {
        return encoder.getPosition() * loadLinearCoef();
    }

    @Override
    public double linearVelocity_mechanismMetersPerSecond() {
        return angularVelocity_mechanismRotationsPerSecond() * loadLinearCoef();
    }

    @Override
    public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
        if (clazz == CANSparkMax.class) {
            return clazz.cast( sparkMax );
        }

        throw new UnsupportedOperationException("no such type");
    }
}
