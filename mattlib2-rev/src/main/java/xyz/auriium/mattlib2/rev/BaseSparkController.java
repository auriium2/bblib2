package xyz.auriium.mattlib2.rev;

import com.revrobotics.*;
import xyz.auriium.mattlib2.hardware.*;
import xyz.auriium.mattlib2.hardware.config.MotorComponent;
import xyz.auriium.mattlib2.hardware.config.PIDComponent;
import xyz.auriium.mattlib2.loop.simple.ISimpleSubroutine;
import xyz.auriium.yuukonstants.exception.ExplainedException;

import java.util.Optional;

public class BaseSparkController extends BaseSparkMotor implements ILinearController, IRotationalController, IRotationalVelocityController, ILinearVelocityController {

    final SparkPIDController localPidController;
    final PIDComponent pidConfig;

    BaseSparkController(CANSparkMax sparkMax, MotorComponent motorComponent, PIDComponent pdConfig, RelativeEncoder encoder) {
        super(sparkMax, motorComponent, encoder);
        localPidController = sparkMax.getPIDController();
        pidConfig = pdConfig;
    }

    boolean cachedIsNormalized = false;
    double reference_primeUnits = 0;
    double state_primeUnits = 0;


    @Override
    public ExplainedException[] verifyInit() {
        var opt = super.verifyInit();
        if (opt.length > 0) return opt;

        localPidController.setFeedbackDevice(encoder);

        localPidController.setP(pidConfig.pConstant(), 0);
        localPidController.setI(pidConfig.iConstant(), 0);
        localPidController.setD(pidConfig.dConstant(), 0);

        return new ExplainedException[0];
    }

    @Override public void verify2Init() {
        super.verify2Init();
    }

    @Override
    public void tunePeriodic() {
        super.tunePeriodic();
/*
        if (pidConfig.hasUpdated()) {
            localPidController.setP(pidConfig.pConstant(), 0);
            localPidController.setI(pidConfig.iConstant(), 0);
            localPidController.setD(pidConfig.dConstant(), 0);
        }*/
    }

    @Override
    public void logPeriodic() {
        super.logPeriodic();

        pidConfig.reportReference(reference_primeUnits);
        switch (mode) {
            default -> {}
            case LINEAR_POS -> pidConfig.reportState(linearPosition_mechanismMeters());
            case INFINITE_ROTATIONAL_POS -> pidConfig.reportState(angularPosition_mechanismRotations());
            case NORM_ROTATIONAL_POS -> pidConfig.reportState(angularPosition_normalizedMechanismRotations());
            case LINEAR_VEL -> pidConfig.reportState(linearVelocity_mechanismMetersPerSecond());
            case ROTATIONAL_VEL -> pidConfig.reportState(angularVelocity_mechanismRotationsPerSecond());
        }

    }

    @Override
    public void controlToLinearReferenceArbitrary(double setpointMechanism_meters, double arbitraryFF_volts) {
        mode = OperationMode.LINEAR_POS;
        reference_primeUnits = setpointMechanism_meters;

        if (cachedIsNormalized) {
            cachedIsNormalized = false;
            localPidController.setPositionPIDWrappingEnabled(false);
        }

        localPidController.setReference(setpointMechanism_meters / loadLinearCoef(), CANSparkBase.ControlType.kPosition, 0, arbitraryFF_volts, SparkPIDController.ArbFFUnits.kVoltage);
    }


    @Override
    public void controlToNormalizedReferenceArbitrary(double setpoint_mechanismNormalizedRotations, double arbitraryFF_volts) {
        mode = OperationMode.NORM_ROTATIONAL_POS;
        reference_primeUnits = setpoint_mechanismNormalizedRotations;

        if (!cachedIsNormalized) {
            cachedIsNormalized = true;
            localPidController.setPositionPIDWrappingEnabled(true);
            localPidController.setPositionPIDWrappingMaxInput(1);
            localPidController.setPositionPIDWrappingMinInput(0);
        }

        localPidController.setReference(setpoint_mechanismNormalizedRotations, CANSparkBase.ControlType.kPosition, 0, arbitraryFF_volts, SparkPIDController.ArbFFUnits.kVoltage);
    }

    @Override
    public void controlToInfiniteReferenceArbitrary(double setpoint_mechanismRotations, double arbitraryFF_volts) {
        mode = OperationMode.INFINITE_ROTATIONAL_POS;
        reference_primeUnits = setpoint_mechanismRotations;

        if (cachedIsNormalized) {
            cachedIsNormalized = false;
            localPidController.setPositionPIDWrappingEnabled(false);
        }

        localPidController.setReference(
                setpoint_mechanismRotations,
                CANSparkBase.ControlType.kPosition,
                0,
                arbitraryFF_volts,
                SparkPIDController.ArbFFUnits.kVoltage
        );
    }


    @Override
    public void controlToLinearVelocityReferenceArbitrary(double setPointMechanism_metersPerSecond, double arbitraryFF_voltage) {
        mode = OperationMode.LINEAR_VEL;
        reference_primeUnits = setPointMechanism_metersPerSecond;

        double nativeReference_rotationsPerSecond = setPointMechanism_metersPerSecond / loadLinearCoef();
        localPidController.setReference(nativeReference_rotationsPerSecond, CANSparkBase.ControlType.kVelocity, 0, arbitraryFF_voltage, SparkPIDController.ArbFFUnits.kVoltage);
    }

    @Override
    public void controlToRotationalVelocityReferenceArbitrary(double setPointMechanism_rotationsPerSecond, double arbitraryFF_voltage) {
        mode = OperationMode.ROTATIONAL_VEL;
        reference_primeUnits = setPointMechanism_rotationsPerSecond;

        localPidController.setReference(setPointMechanism_rotationsPerSecond, CANSparkBase.ControlType.kVelocity);
    }
}
