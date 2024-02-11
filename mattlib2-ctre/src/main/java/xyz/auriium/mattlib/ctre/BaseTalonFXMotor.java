package xyz.auriium.mattlib.ctre;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.*;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.ForwardLimitTypeValue;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.signals.ReverseLimitTypeValue;
import xyz.auriium.mattlib2.hardware.ILinearMotor;
import xyz.auriium.mattlib2.hardware.IRotationalMotor;
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

        var fbConfig = new FeedbackConfigs()
                .withSensorToMechanismRatio(motorComponent.encoderToMechanismCoefficient());

        exceptions = orThrow(
                talonFX.getConfigurator().apply(fbConfig),
                motorComponent.selfPath(),
                exceptions
        );

        //current limits

        double currentLimit = motorComponent.currentLimit().orElse(70);

        var clConfig = new CurrentLimitsConfigs()
                .withStatorCurrentLimit(currentLimit);

        exceptions = orThrow(
                talonFX.getConfigurator().apply(clConfig),
                motorComponent.selfPath(),
                exceptions
        );

        //hardware limits

        var hwlsm = new HardwareLimitSwitchConfigs();
        var fwLimit = motorComponent.forwardLimit();
        var rvLimit = motorComponent.reverseLimit();
        if (fwLimit.isPresent()) {
            hwlsm = hwlsm
                    .withForwardLimitEnable(true)
                    .withForwardLimitType(fwLimit.get() == CommonMotorComponent.Normally.OPEN ? ForwardLimitTypeValue.NormallyOpen : ForwardLimitTypeValue.NormallyClosed);
        }
        if (rvLimit.isPresent()) {
            hwlsm = hwlsm
                    .withReverseLimitEnable(true)
                    .withReverseLimitType(rvLimit.get() == CommonMotorComponent.Normally.OPEN ? ReverseLimitTypeValue.NormallyOpen : ReverseLimitTypeValue.NormallyClosed);
        }

        exceptions = orThrow(
                talonFX.getConfigurator().apply(hwlsm),
                motorComponent.selfPath(),
                exceptions
        );



        //software limits

        Optional<Double> fwLimitSoft = motorComponent.forwardSoftLimit_mechanismRot();
        Optional<Double> rvLimitSoft = motorComponent.reverseSoftLimit_mechanismRot();


        var swConfig = new SoftwareLimitSwitchConfigs ()
                .withForwardSoftLimitEnable(fwLimitSoft.isPresent())
                .withReverseSoftLimitEnable(rvLimitSoft.isPresent());

        if (fwLimitSoft.isPresent()) {
            swConfig = swConfig.withForwardSoftLimitThreshold(fwLimitSoft.get());
        }
        if (rvLimitSoft.isPresent()) {
            swConfig = swConfig.withReverseSoftLimitThreshold(rvLimitSoft.get());
        }

        exceptions = orThrow(
                talonFX.getConfigurator().apply(swConfig),
                motorComponent.selfPath(),
                exceptions
        );

        //idle mode + inverted

        boolean inverted = motorComponent.inverted().orElse(false);
        boolean breakModeEnabled = motorComponent.breakModeEnabled().orElse(false);

        var mocConfig = new MotorOutputConfigs()
                .withInverted(inverted ? InvertedValue.CounterClockwise_Positive : InvertedValue.Clockwise_Positive)
                .withNeutralMode(breakModeEnabled ? NeutralModeValue.Brake : NeutralModeValue.Coast);

        exceptions = orThrow(
                talonFX.getConfigurator().apply(mocConfig),
                motorComponent.selfPath(),
                exceptions
        );

        motorComponent.openRampRate_seconds().ifPresent(rate -> {
            OpenLoopRampsConfigs rr = new OpenLoopRampsConfigs()
                    .withDutyCycleOpenLoopRampPeriod(rate)
                    .withVoltageOpenLoopRampPeriod(rate);
            talonFX.getConfigurator().apply(rr);
        });

        motorComponent.closedRampRate_seconds().ifPresent(rate -> {
            ClosedLoopRampsConfigs rr = new ClosedLoopRampsConfigs()
                    .withDutyCycleClosedLoopRampPeriod(rate)
                    .withVoltageClosedLoopRampPeriod(rate);
            talonFX.getConfigurator().apply(rr);
        });


        currentNow = talonFX.getTorqueCurrent();
        voltageOutput = talonFX.getMotorVoltage();
        temperature = talonFX.getDeviceTemp();

        position_mechanismRotations = talonFX.getPosition();
        velocity_mechanismRotationsPerSecond = talonFX.getVelocity();

        //loading status signals

        return exceptions;
    }

    StatusSignal<Double> currentNow;
    StatusSignal<Double> voltageOutput;
    StatusSignal<Double> temperature;
    StatusSignal<Double> position_mechanismRotations;
    StatusSignal<Double> velocity_mechanismRotationsPerSecond;

    @Override
    public void logicPeriodic() {

        BaseStatusSignal.refreshAll(
                currentNow,
                voltageOutput,
                temperature,
                position_mechanismRotations,
                velocity_mechanismRotationsPerSecond
        );

    }

    @Override
    public void logPeriodic() {
        motorComponent.reportCurrentDraw(currentNow.getValue());
        motorComponent.reportTemperature(temperature.getValue());
        motorComponent.reportVoltageGiven(voltageOutput.getValue());
        motorComponent.reportMechanismRotations(position_mechanismRotations.getValue());
        motorComponent.reportMechanismVelocity(velocity_mechanismRotationsPerSecond.getValue());
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
        talonFX.setVoltage(voltage);
    }

    @Override
    public void setToPercent(double percent_zeroToOne) {
        talonFX.set(percent_zeroToOne);
    }

    @Override public double reportCurrentNow_amps() {
        return currentNow.getValue();
    }

    @Override public double reportVoltageNow() {
        return voltageOutput.getValue();
    }

    @Override public double reportTemperatureNow() {
        return temperature.getValue();
    }

    @Override public void forceLinearOffset(double linearOffset_mechanismMeters) {
        forceRotationalOffset(linearOffset_mechanismMeters / loadLinearCoef());
    }

    @Override public double linearPosition_mechanismMeters() {
        return position_mechanismRotations.getValue() * loadLinearCoef();
    }

    @Override public double linearVelocity_mechanismMetersPerSecond() {
        return velocity_mechanismRotationsPerSecond.getValue() * loadLinearCoef();
    }

    @Override public void forceRotationalOffset(double offset_mechanismRotations) {
        var fbc = new FeedbackConfigs()
                .withSensorToMechanismRatio(motorComponent.encoderToMechanismCoefficient())
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
}
