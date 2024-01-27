package xyz.auriium.mattlib2.rev;

import com.revrobotics.*;
import xyz.auriium.mattlib2.hardware.*;
import xyz.auriium.mattlib2.hardware.config.MotorComponent;
import xyz.auriium.mattlib2.hardware.config.PIDComponent;
import xyz.auriium.yuukonstants.exception.ExplainedException;

import java.util.Optional;

public class BaseSparkController extends BaseSparkMotor implements ILinearController, IRotationalController, IRotationalVelocityController, ILinearVelocityController {

    final SparkPIDController localPidController;
    final PIDComponent PIDNetworkedConfig;

    BaseSparkController(CANSparkMax sparkMax, MotorComponent motorComponent, PIDComponent pdConfig, RelativeEncoder encoder) {
        super(sparkMax, motorComponent, encoder);
        localPidController = sparkMax.getPIDController();
        PIDNetworkedConfig = pdConfig;
    }

    //logging stuff

    @Override
    public Optional<ExplainedException> verifyInit() {
        var opt = super.verifyInit();
        if (opt.isPresent()) return opt;

        localPidController.setFeedbackDevice(encoder);

        localPidController.setP(PIDNetworkedConfig.pConstant());
        localPidController.setI(PIDNetworkedConfig.iConstant());
        localPidController.setD(PIDNetworkedConfig.dConstant());

        return Optional.empty();
    }


    @Override
    public void tunePeriodic() {
        if (PIDNetworkedConfig.hasUpdated()) {
            localPidController.setP(PIDNetworkedConfig.pConstant());
            localPidController.setI(PIDNetworkedConfig.iConstant());
            localPidController.setD(PIDNetworkedConfig.dConstant());
        }
    }

    @Override
    public void logPeriodic() {
        super.logPeriodic();
    }

    boolean cachedIsNormalized = false;
    //Controller stuff

    @Override
    public void controlToLinearReference(double setpointMechanism_meters) {
        if (cachedIsNormalized) {
            cachedIsNormalized = false;
            localPidController.setPositionPIDWrappingEnabled(false);
        }

        double convertedMechanismRotations = setpointMechanism_meters / linearCoef;
        localPidController.setReference(convertedMechanismRotations, CANSparkMax.ControlType.kPosition);
    }

    @Override
    public void controlToLinearReference(double setpointMechanism_meters, double measurementMechanism_meters) {
        throw Exceptions.CANNOT_EXTERNAL_FEEDBACK_INTERNAL;
    }

    @Override
    public void controlToNormalizedReference(double setpoint_mechanismNormalizedRotations) {
        if (!cachedIsNormalized) {
            cachedIsNormalized = true;
            localPidController.setPositionPIDWrappingEnabled(true);
            localPidController.setPositionPIDWrappingMaxInput(1);
            localPidController.setPositionPIDWrappingMinInput(0);
        }

        localPidController.setReference(setpoint_mechanismNormalizedRotations, CANSparkBase.ControlType.kPosition);
    }

    @Override
    public void controlToNormalizedReference(double setpoint_mechanismNormalizedRotations, double measurement_mechanismNormalizedRotations) {
        throw Exceptions.CANNOT_EXTERNAL_FEEDBACK_INTERNAL;
    }

    @Override
    public void controlToInfiniteReference(double setpoint_mechanismRotations) {
        if (cachedIsNormalized) {
            cachedIsNormalized = false;
            localPidController.setPositionPIDWrappingEnabled(false);
        }

        localPidController.setReference(
                setpoint_mechanismRotations,
                CANSparkBase.ControlType.kPosition
        );
    }

    @Override
    public void controlToInfiniteReference(double setpoint_mechanismRotations, double measurement_mechanismRotations) {
        throw Exceptions.CANNOT_EXTERNAL_FEEDBACK_INTERNAL;
    }

    @Override
    public void controlToLinearVelocityReference(double setPointMechanism_metersPerSecond) {
        double nativeRPS = setPointMechanism_metersPerSecond / linearCoef;
        localPidController.setReference(nativeRPS, CANSparkBase.ControlType.kVelocity);
    }

    @Override
    public void controlToRotationalVelocityReference(double setPointMechanism_rotationsPerSecond) {
        localPidController.setReference(setPointMechanism_rotationsPerSecond, CANSparkBase.ControlType.kVelocity);
    }
}
