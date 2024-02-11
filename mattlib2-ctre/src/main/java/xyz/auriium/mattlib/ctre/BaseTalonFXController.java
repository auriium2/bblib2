package xyz.auriium.mattlib.ctre;

import com.ctre.phoenix6.configs.ClosedLoopGeneralConfigs;
import com.ctre.phoenix6.configs.FeedbackConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import xyz.auriium.mattlib2.hardware.ILinearController;
import xyz.auriium.mattlib2.hardware.ILinearVelocityController;
import xyz.auriium.mattlib2.hardware.IRotationalController;
import xyz.auriium.mattlib2.hardware.IRotationalVelocityController;
import xyz.auriium.mattlib2.hardware.config.MotorComponent;
import xyz.auriium.mattlib2.hardware.config.PIDComponent;
import xyz.auriium.yuukonstants.exception.ExplainedException;

public class BaseTalonFXController extends BaseTalonFXMotor implements ILinearController, IRotationalController, ILinearVelocityController, IRotationalVelocityController {

    final PIDComponent component;

    public BaseTalonFXController(TalonFX talonFX, MotorComponent motorComponent, PIDComponent component) {
        super(talonFX, motorComponent);
        this.component = component;
    }

    boolean cachedIsNormalized = false;

    @Override public ExplainedException[] verifyInit() {
        ExplainedException[] old = super.verifyInit();

        var pidConf = new Slot0Configs()
                .withKP(component.pConstant())
                .withKI(component.iConstant())
                .withKD(component.dConstant());

        old = orThrow(talonFX.getConfigurator().apply(pidConf), motorComponent.selfPath(), old);

        return old;
    }



    @Override public void controlToLinearReferenceArbitrary(double setpointMechanism_meters, double arbitraryFF_volts) {
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
        var request = new VelocityVoltage(setPointMechanism_metersPerSecond * loadLinearCoef())
                .withSlot(0)
                .withFeedForward(arbitraryFF_voltage);

        talonFX.setControl(request);
    }

    @Override public void controlToRotationalVelocityReferenceArbitrary(double setPointMechanism_rotationsPerSecond, double arbitraryFF_voltage) {
        var request = new VelocityVoltage(setPointMechanism_rotationsPerSecond)
                .withSlot(0)
                .withFeedForward(arbitraryFF_voltage);

        talonFX.setControl(request);
    }
}
