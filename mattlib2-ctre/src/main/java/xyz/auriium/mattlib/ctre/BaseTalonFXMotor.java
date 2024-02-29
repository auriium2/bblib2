package xyz.auriium.mattlib.ctre;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.*;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.*;
import xyz.auriium.mattlib2.hardware.ILinearMotor;
import xyz.auriium.mattlib2.hardware.IRotationalMotor;
import xyz.auriium.mattlib2.hardware.OperationMode;
import xyz.auriium.mattlib2.hardware.config.CommonMotorComponent;
import xyz.auriium.mattlib2.hardware.config.MotorComponent;
import xyz.auriium.mattlib2.loop.IMattlibHooked;
import xyz.auriium.mattlib2.utils.AngleUtil;
import xyz.auriium.yuukonstants.GenericPath;
import xyz.auriium.yuukonstants.exception.ExplainedException;
import yuukonfig.core.ArrayUtil;

import java.util.Optional;

public class BaseTalonFXMotor implements IMattlibHooked, ILinearMotor, IRotationalMotor {

    final TalonFX talonFX;
    final MotorComponent motorComponent;

    public BaseTalonFXMotor(TalonFX talonFX, MotorComponent motorComponent) {
        this.talonFX = talonFX;
        this.motorComponent = motorComponent;

        mattRegister();
    }

    public static ExplainedException[] orThrow(StatusCode code, GenericPath path, ExplainedException[] arr) {
        if (!code.isOK()) {
            return ArrayUtil.combine(arr, Exceptions.CTRE_ERROR(code, path));
        } else {
            return arr;
        }
    }

    @Override
    public ExplainedException[] verifyInit() {

        ExplainedException[] exceptions = new ExplainedException[0];

        //set feedback config

        var sumConfig = new TalonFXConfiguration();

        sumConfig.Feedback.SensorToMechanismRatio =  1/ motorComponent.encoderToMechanismCoefficient();
        sumConfig.CurrentLimits.StatorCurrentLimit = motorComponent.currentLimit().orElse(70);

        //hardware limits

        var fwLimit = motorComponent.forwardLimit();
        var rvLimit = motorComponent.reverseLimit();
        if (fwLimit.isPresent()) {
            sumConfig.HardwareLimitSwitch.ForwardLimitEnable = true;
            sumConfig.HardwareLimitSwitch.ForwardLimitType = fwLimit.get() == CommonMotorComponent.Normally.OPEN ? ForwardLimitTypeValue.NormallyOpen : ForwardLimitTypeValue.NormallyClosed;
        }
        if (rvLimit.isPresent()) {
            sumConfig.HardwareLimitSwitch.ReverseLimitEnable = true;
            sumConfig.HardwareLimitSwitch.ReverseLimitType = rvLimit.get() == CommonMotorComponent.Normally.OPEN ? ReverseLimitTypeValue.NormallyOpen : ReverseLimitTypeValue.NormallyClosed;
        }

        //software limits

        Optional<Double> fwLimitSoft = motorComponent.forwardSoftLimit_mechanismRot();
        Optional<Double> rvLimitSoft = motorComponent.reverseSoftLimit_mechanismRot();


        sumConfig.SoftwareLimitSwitch.ForwardSoftLimitEnable = fwLimitSoft.isPresent();
        sumConfig.SoftwareLimitSwitch.ReverseSoftLimitEnable = rvLimitSoft.isPresent();

        fwLimitSoft.ifPresent(aDouble -> sumConfig.SoftwareLimitSwitch.ForwardSoftLimitThreshold = aDouble);
        rvLimitSoft.ifPresent(aDouble -> sumConfig.SoftwareLimitSwitch.ReverseSoftLimitThreshold = aDouble);

        sumConfig.MotorOutput.Inverted = motorComponent.inverted().orElse(false) ? InvertedValue.CounterClockwise_Positive : InvertedValue.Clockwise_Positive;
        sumConfig.MotorOutput.NeutralMode = motorComponent.breakModeEnabled().orElse(false) ? NeutralModeValue.Brake : NeutralModeValue.Coast;

        motorComponent.openRampRate_seconds().ifPresent(rate -> {

            sumConfig.OpenLoopRamps.DutyCycleOpenLoopRampPeriod = rate;
            sumConfig.OpenLoopRamps.VoltageOpenLoopRampPeriod = rate;

        });

        motorComponent.closedRampRate_seconds().ifPresent(rate -> {

            sumConfig.ClosedLoopRamps.DutyCycleClosedLoopRampPeriod = rate;
            sumConfig.ClosedLoopRamps.VoltageClosedLoopRampPeriod = rate;

        });
        exceptions = orThrow(
                talonFX.getConfigurator().apply(sumConfig, 0.1),
                motorComponent.selfPath(),
                exceptions
        );

        currentNow = talonFX.getTorqueCurrent();
        voltageOutput = talonFX.getMotorVoltage();
        temperature = talonFX.getDeviceTemp();

        position_mechanismRotations = talonFX.getPosition();
        velocity_mechanismRotationsPerSecond = talonFX.getVelocity();

        forwardLimitSwitchHit = talonFX.getForwardLimit();
        reverseLimitSwitchHit = talonFX.getReverseLimit();

        if (motorComponent.fwReset_mechanismRot().isPresent() || motorComponent.rvReset_mechanismRot().isPresent()) {
            BaseStatusSignal.setUpdateFrequencyForAll(25,
                    forwardLimitSwitchHit,
                    reverseLimitSwitchHit
            );
        } else {
            BaseStatusSignal.setUpdateFrequencyForAll(5,
                    forwardLimitSwitchHit,
                    reverseLimitSwitchHit
            );
        }

        if (motorComponent.fwReset_mechanismRot().isPresent() && motorComponent.forwardLimit().isPresent()) {
            botherRunningFwLimitLoop = true;
        }

        if (motorComponent.rvReset_mechanismRot().isPresent() && motorComponent.reverseLimit().isPresent()) {
            botherRunningRvLimitLoop = true;
        }



        BaseStatusSignal.setUpdateFrequencyForAll(
                12.5,
                currentNow,
                voltageOutput,
                temperature

        );

        BaseStatusSignal.setUpdateFrequencyForAll(
                50,
                velocity_mechanismRotationsPerSecond,
                position_mechanismRotations
        );


        return exceptions;
    }

    StatusSignal<Double> currentNow;
    StatusSignal<Double> voltageOutput;
    StatusSignal<Double> temperature;
    StatusSignal<Double> position_mechanismRotations;
    StatusSignal<Double> velocity_mechanismRotationsPerSecond;

    StatusSignal<ForwardLimitValue> forwardLimitSwitchHit;
    StatusSignal<ReverseLimitValue> reverseLimitSwitchHit;

    OperationMode mode = OperationMode.NOT_SET;

    boolean botherRunningFwLimitLoop = false;
    boolean botherRunningRvLimitLoop = false;


    @Override
    public void logicPeriodic() {

        BaseStatusSignal.refreshAll(
                currentNow,
                voltageOutput,
                temperature,
                position_mechanismRotations,
                velocity_mechanismRotationsPerSecond
        );

        if (botherRunningFwLimitLoop || botherRunningRvLimitLoop) {
            BaseStatusSignal.refreshAll(
                    forwardLimitSwitchHit,
                    reverseLimitSwitchHit
            );
        }

        if (botherRunningFwLimitLoop) {
            var forwardNormally = motorComponent.forwardLimit().get();

            boolean suddenlyClosed = forwardNormally == CommonMotorComponent.Normally.CLOSED && forwardLimitSwitchHit.getValue() == ForwardLimitValue.Open;
            boolean suddenlyOpen = forwardNormally == CommonMotorComponent.Normally.OPEN && forwardLimitSwitchHit.getValue() == ForwardLimitValue.ClosedToGround;

            if (suddenlyOpen || suddenlyClosed) {
                talonFX.setPosition(motorComponent.fwReset_mechanismRot().get());
            }
        }

        if (botherRunningRvLimitLoop) {
            var reverseNormally = motorComponent.reverseLimit().get();

            boolean suddenlyClosed = reverseNormally == CommonMotorComponent.Normally.CLOSED && reverseLimitSwitchHit.getValue() == ReverseLimitValue.Open;
            boolean suddenlyOpen = reverseNormally == CommonMotorComponent.Normally.OPEN && reverseLimitSwitchHit.getValue() == ReverseLimitValue.ClosedToGround;

            if (suddenlyOpen || suddenlyClosed) {
                talonFX.setPosition(motorComponent.rvReset_mechanismRot().get());
            }
        }

        talonFX.optimizeBusUtilization();


    }

    @Override
    public void logPeriodic() {
        motorComponent.reportCurrentDraw(currentNow.getValue());
        motorComponent.reportTemperature(temperature.getValue());
        motorComponent.reportVoltageGiven(voltageOutput.getValue());
        motorComponent.reportMechanismRotations(position_mechanismRotations.getValue());
        motorComponent.reportMechanismVelocity(velocity_mechanismRotationsPerSecond.getValue());

        if (botherRunningFwLimitLoop) {
            var forwardNormally = motorComponent.forwardLimit().get();

            boolean suddenlyClosed = forwardNormally == CommonMotorComponent.Normally.CLOSED && reverseLimitSwitchHit.getValue() == ReverseLimitValue.Open;
            boolean suddenlyOpen = forwardNormally == CommonMotorComponent.Normally.OPEN && reverseLimitSwitchHit.getValue() == ReverseLimitValue.ClosedToGround;

            motorComponent.reportFwLimitTriggered(suddenlyClosed || suddenlyOpen);

        }

        if (botherRunningRvLimitLoop) {
            var reverseNormally = motorComponent.reverseLimit().get();

            boolean suddenlyClosed = reverseNormally == CommonMotorComponent.Normally.CLOSED && reverseLimitSwitchHit.getValue() == ReverseLimitValue.Open;
            boolean suddenlyOpen = reverseNormally == CommonMotorComponent.Normally.OPEN && reverseLimitSwitchHit.getValue() == ReverseLimitValue.ClosedToGround;

            motorComponent.reportRvLimitTriggered(suddenlyClosed || suddenlyOpen);
        }
    }


    double linearCoef = 0;
    boolean linearCoefSet = false;
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
    public void setToVoltage(double voltage) {
        mode = OperationMode.VOLTAGE;
        talonFX.setVoltage(voltage);
    }

    @Override public void stopActuator() {
        talonFX.stopMotor();
    }

    @Override
    public void setToPercent(double percent_zeroToOne) {
        mode = OperationMode.DUTY;
        talonFX.set(percent_zeroToOne);
    }

    @Override public double reportCurrentNow_amps() {
        return currentNow.getValueAsDouble();
    }

    @Override public double reportVoltageNow() {
        return voltageOutput.getValueAsDouble();
    }

    @Override public double reportTemperatureNow() {
        return temperature.getValueAsDouble();
    }

    @Override public void forceLinearOffset(double linearOffset_mechanismMeters) {
        forceRotationalOffset(linearOffset_mechanismMeters / loadLinearCoef());
    }

    @Override public double linearPosition_mechanismMeters() {
        return position_mechanismRotations.getValueAsDouble() * loadLinearCoef();
    }

    @Override public double linearVelocity_mechanismMetersPerSecond() {
        return velocity_mechanismRotationsPerSecond.getValueAsDouble() * loadLinearCoef();
    }

    @Override public void forceRotationalOffset(double offset_mechanismRotations) {
        var fbc = new FeedbackConfigs()
                .withSensorToMechanismRatio(1 / motorComponent.encoderToMechanismCoefficient())
                .withFeedbackRotorOffset(offset_mechanismRotations);

        talonFX.getConfigurator().apply(fbc);
    }

    @Override public double angularPosition_encoderRotations() {
        return position_mechanismRotations.getValue() / motorComponent.encoderToMechanismCoefficient();
    }

    @Override public double angularPosition_mechanismRotations() {
        return position_mechanismRotations.getValue();
    }

    @Override public double angularPosition_normalizedMechanismRotations() {
        return AngleUtil.normalizeRotations(angularPosition_mechanismRotations());
    }

    @Override public double angularPosition_normalizedEncoderRotations() {
        return AngleUtil.normalizeRotations(angularPosition_encoderRotations());
    }

    @Override public double angularVelocity_mechanismRotationsPerSecond() {
        return velocity_mechanismRotationsPerSecond.getValue();
    }

    @Override public double angularVelocity_encoderRotationsPerSecond() {
        return velocity_mechanismRotationsPerSecond.getValue() / loadLinearCoef();
    }

    @Override public <T> T rawAccess(Class<T> clazz) throws UnsupportedOperationException {
        if (clazz == TalonFX.class) {
            return clazz.cast(talonFX);
        }

        throw new UnsupportedOperationException();
    }
}
