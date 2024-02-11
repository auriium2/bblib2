package xyz.auriium.mattlib2.rev;

import com.revrobotics.*;
import xyz.auriium.mattlib2.loop.IMattlibHooked;
import xyz.auriium.mattlib2.hardware.ILinearMotor;
import xyz.auriium.mattlib2.hardware.IRotationalMotor;
import xyz.auriium.mattlib2.hardware.OperationMode;
import xyz.auriium.mattlib2.hardware.config.CommonMotorComponent;
import xyz.auriium.mattlib2.hardware.config.MotorComponent;
import xyz.auriium.mattlib2.utils.AngleUtil;
import xyz.auriium.yuukonstants.GenericPath;
import xyz.auriium.yuukonstants.exception.ExplainedException;
import yuukonfig.core.ArrayUtil;

import java.util.Optional;


/**
 * Implementation of the spark motor
 */
class BaseSparkMotor implements ILinearMotor, IRotationalMotor, IMattlibHooked {

    final CANSparkMax sparkMax;
    final MotorComponent motorComponent;
    final RelativeEncoder encoder;

    BaseSparkMotor(CANSparkMax sparkMax, MotorComponent motorComponent, RelativeEncoder encoder) {
        this.sparkMax = sparkMax;
        this.motorComponent = motorComponent;
        this.encoder = encoder;

        mattRegister();
    }

    OperationMode mode = OperationMode.NOT_SET;
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

    public static ExplainedException[] orThrow(REVLibError code, GenericPath path, ExplainedException[] arr) {
        if (code != REVLibError.kOk) {
            return ArrayUtil.combine(arr, Exceptions.REV_ERROR(code, path));
        } else {
            return arr;
        }
    }


    @Override
    public ExplainedException[] verifyInit() {

        ExplainedException[] toThrow = new ExplainedException[0];
        GenericPath path = motorComponent.selfPath();
/*
        for (int i = 0; i < CANSparkBase.FaultID.values().length; i++) {
            var fault = CANSparkBase.FaultID.fromId(i);
            if (i == 3) continue;
            if (sparkMax.getFault(fault)) {
                toThrow = ArrayUtil.combine(toThrow, Exceptions.REV_FAULT(fault, path));
            }
        }*/


        toThrow = orThrow(sparkMax.getLastError(), path, toThrow); //initial error check
        toThrow = orThrow(sparkMax.restoreFactoryDefaults(), path, toThrow);
        //NEVER PUYT FUCKING CODE BEHIND THIS LINE BECAUSE IF YOU DO IT WILL WASTE <4> hours
        //CHANGE THIS NUMBER WHEN YOUR TIME GETS WASTED

        boolean isInverted = motorComponent.inverted().orElse(false);
        sparkMax.setInverted(isInverted);

        toThrow = orThrow(
                encoder.setPositionConversionFactor(motorComponent.encoderToMechanismCoefficient()),
                path,
                toThrow
        );

        toThrow = orThrow(
                encoder.setVelocityConversionFactor(motorComponent.encoderToMechanismCoefficient() / 60.0),
                path,
                toThrow
        );

        toThrow = orThrow(
                sparkMax.enableVoltageCompensation(12),
                path,
                toThrow
        );

        //cyrrent limits
        int currentLimit = motorComponent.currentLimit().orElse(70);
        toThrow = orThrow(
                sparkMax.setSmartCurrentLimit(currentLimit),
                path,
                toThrow
        );

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
        motorComponent.breakModeEnabled().ifPresentOrElse(
                breakMode -> sparkMax.setIdleMode(breakMode ? CANSparkBase.IdleMode.kBrake : CANSparkBase.IdleMode.kCoast),
                () -> sparkMax.setIdleMode(CANSparkBase.IdleMode.kCoast)
        );

        motorComponent.openRampRate_seconds().ifPresent(sparkMax::setOpenLoopRampRate);
        motorComponent.closedRampRate_seconds().ifPresent(sparkMax::setClosedLoopRampRate);


        sparkMax.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus0, 200);
        sparkMax.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus1, 20);
        sparkMax.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus2, 10);
        sparkMax.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus3, 100);
        sparkMax.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus4, 100);
        sparkMax.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus5, 500);
        sparkMax.setPeriodicFramePeriod(CANSparkLowLevel.PeriodicFrame.kStatus6, 500);

        return toThrow;
    }

    @Override
    public void verify2Init() {
        sparkMax.burnFlash();
    }

    @Override
    public void logicPeriodic() {
        outputVoltage = sparkMax.getAppliedOutput() * 12;
        outputCurrent = sparkMax.getOutputCurrent();
        temperature = sparkMax.getMotorTemperature();
    }

    @Override
    public void logPeriodic() {

        motorComponent.reportCurrentDraw(outputCurrent);
        motorComponent.reportVoltageGiven(outputVoltage);
        motorComponent.reportTemperature(temperature);

        motorComponent.reportMechanismRotations(angularPosition_mechanismRotations());
        motorComponent.reportMechanismVelocity(angularVelocity_mechanismRotationsPerSecond());

    }

    //Other stuff

    @Override
    public void setToVoltage(double voltage) {
        mode = OperationMode.VOLTAGE;
        sparkMax.setVoltage(voltage);
    }


    @Override
    public void setToPercent(double percent_zeroToOne) {
        mode = OperationMode.DUTY;
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
        return encoder.getVelocity() / motorComponent.encoderToMechanismCoefficient(); //pre-converted in init, so we actually work backwards
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
