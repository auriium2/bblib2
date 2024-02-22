package xyz.auriium.mattlib.ctre;

import com.ctre.phoenix6.Orchestra;
import com.ctre.phoenix6.configs.ClosedLoopGeneralConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import xyz.auriium.mattlib2.hardware.*;
import xyz.auriium.mattlib2.hardware.config.MotorComponent;
import xyz.auriium.mattlib2.hardware.config.PIDComponent;
import xyz.auriium.yuukonstants.exception.ExplainedException;

public class BaseTalonFXController extends BaseTalonFXMotor implements ILinearController, IRotationalController, ILinearVelocityController, IRotationalVelocityController {


    final PIDComponent pidConfig;

    public BaseTalonFXController(TalonFX talonFX, MotorComponent motorComponent, PIDComponent pidConfig) {
        super(talonFX, motorComponent);
        this.pidConfig = pidConfig;
    }

    boolean cachedIsNormalized = false;
    double reference_primeUnits = 0;

    @Override public ExplainedException[] verifyInit() {
        ExplainedException[] old = super.verifyInit();

        var pidConf = new Slot0Configs()
                .withKP(pidConfig.pConstant())
                .withKI(pidConfig.iConstant())
                .withKD(pidConfig.dConstant());

        old = orThrow(talonFX.getConfigurator().apply(pidConf), motorComponent.selfPath(), old);

        return old;
    }

    @Override public void controlToLinearReferenceArbitrary(double setpointMechanism_meters, double arbitraryFF_volts) {
        mode = OperationMode.LINEAR_POS;
        reference_primeUnits = setpointMechanism_meters;
        if (cachedIsNormalized) {
            cachedIsNormalized = false;

            var closedLoopConfig = new ClosedLoopGeneralConfigs();
            closedLoopConfig.ContinuousWrap = false;
            talonFX.getConfigurator().apply(closedLoopConfig);
        }

        var request = new PositionVoltage(setpointMechanism_meters * loadLinearCoef())
                .withSlot(0)
                .withFeedForward(arbitraryFF_volts);

        talonFX.setControl(request);
    }

    @Override public void controlToNormalizedReferenceArbitrary(double setpoint_mechanismNormalizedRotations, double arbitraryFF_volts) {
        mode = OperationMode.NORM_ROTATIONAL_POS;
        reference_primeUnits = setpoint_mechanismNormalizedRotations;
        if (!cachedIsNormalized) {
            cachedIsNormalized = true;

            var closedLoopConfig = new ClosedLoopGeneralConfigs();
            closedLoopConfig.ContinuousWrap = true;
            talonFX.getConfigurator().apply(closedLoopConfig);
        }

        var request = new PositionVoltage(setpoint_mechanismNormalizedRotations * loadLinearCoef())
                .withSlot(0)
                .withFeedForward(arbitraryFF_volts);

        talonFX.setControl(request);

    }

    @Override public void controlToInfiniteReferenceArbitrary(double setpoint_mechanismRotations, double arbitraryFF_volts) {
        mode = OperationMode.INFINITE_ROTATIONAL_POS;
        if (cachedIsNormalized) {
            cachedIsNormalized = false;

            var closedLoopConfig = new ClosedLoopGeneralConfigs();
            closedLoopConfig.ContinuousWrap = false;
            talonFX.getConfigurator().apply(closedLoopConfig);
        }

        var request = new PositionVoltage(setpoint_mechanismRotations * loadLinearCoef())
                .withSlot(0)
                .withFeedForward(arbitraryFF_volts);

        talonFX.setControl(request);
    }

    @Override public void controlToLinearVelocityReferenceArbitrary(double setPointMechanism_metersPerSecond, double arbitraryFF_voltage) {
        reference_primeUnits = setPointMechanism_metersPerSecond;
        mode = OperationMode.LINEAR_VEL;
        var request = new VelocityVoltage(setPointMechanism_metersPerSecond * loadLinearCoef())
                .withSlot(0)
                .withFeedForward(arbitraryFF_voltage);

        talonFX.setControl(request);
    }

    @Override public void controlToRotationalVelocityReferenceArbitrary(double setPointMechanism_rotationsPerSecond, double arbitraryFF_voltage) {
        reference_primeUnits = setPointMechanism_rotationsPerSecond;
        mode = OperationMode.ROTATIONAL_VEL;
        var request = new VelocityVoltage(setPointMechanism_rotationsPerSecond)
                .withSlot(0)
                .withFeedForward(arbitraryFF_voltage);

        talonFX.setControl(request);
    }

    @Override public void logPeriodic() {
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
}
