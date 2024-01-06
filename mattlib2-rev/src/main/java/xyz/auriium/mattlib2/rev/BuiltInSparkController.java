package xyz.auriium.mattlib2.rev;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import xyz.auriium.mattlib2.hard.ILinearController;
import xyz.auriium.mattlib2.hard.IRotationalController;
import xyz.auriium.mattlib2.log.components.impl.CANNetworkedConfig;
import xyz.auriium.mattlib2.log.components.impl.MotorNetworkedConfig;
import xyz.auriium.mattlib2.log.components.impl.PIDNetworkedConfig;

import java.util.Optional;

public class BuiltInSparkController extends BaseSparkMotor implements ILinearController, IRotationalController {

    final SparkMaxPIDController localPidController;
    final PIDNetworkedConfig PIDNetworkedConfig;

    BuiltInSparkController(CANSparkMax sparkMax, CANNetworkedConfig canConfig, MotorNetworkedConfig motorConfig, PIDNetworkedConfig pdConfig, RelativeEncoder encoder) {
        super(sparkMax, canConfig, motorConfig, encoder);
        localPidController = sparkMax.getPIDController();
        PIDNetworkedConfig = pdConfig;
    }

    //Logging stuff

    @Override
    public void init() {
        localPidController.setP(PIDNetworkedConfig.pConstant());
        localPidController.setI(PIDNetworkedConfig.iConstant());
        localPidController.setD(PIDNetworkedConfig.dConstant());
    }

    @Override
    public void tunePeriodic() {
        if (PIDNetworkedConfig.hasUpdated()) {
            localPidController.setP(PIDNetworkedConfig.pConstant());
            localPidController.setI(PIDNetworkedConfig.iConstant());
            localPidController.setD(PIDNetworkedConfig.dConstant());
        }
    }

    //Controller stuff

    @Override
    public void controlToLinearReference(double setpointMechanism_meters) {
        Optional<Double> coefOptional = motorConfig.rotationToMeterCoefficient();
        if (coefOptional.isEmpty()) throw xyz.auriium.mattlib2.hard.Exceptions.MOTOR_NOT_LINEAR(motorConfig.selfPath());
        double convertedEncoderPosition = setpointMechanism_meters / motorConfig.rotationToMeterCoefficient().orElseThrow() / motorConfig.encoderToMechanismCoefficient();

        localPidController.setReference(convertedEncoderPosition, CANSparkMax.ControlType.kPosition);
    }

    @Override
    public void controlToLinearReference(double setpointMechanism_meters, double measurementMechanism_meters) {
        throw Exceptions.CANNOT_EXTERNAL_FEEDBACK_INTERNAL;
    }

    @Override
    public void controlToRotationalReference(double setpoint_mechanismNormalizedRotations) {
        //This appears to be like the continuous mode code but explicit. I do not understand it.

        double currentAngle_mechanismInfiniteRotations = angularPosition_mechanismRotations();
        double currentAngle_mechanismNormalizedRotations = currentAngle_mechanismInfiniteRotations % 1d;
        if (currentAngle_mechanismNormalizedRotations < 0d) {
            currentAngle_mechanismNormalizedRotations += 1d; //no idea why this works
        }

        // take (infinite - normalized) for (current offset) then add (setpoint normalized) for (setpoint infinite)
        double reference_mechanismInfiniteRotations = setpoint_mechanismNormalizedRotations
                + currentAngle_mechanismInfiniteRotations
                - currentAngle_mechanismNormalizedRotations;


        // more modulus code i don't understand
        if (setpoint_mechanismNormalizedRotations - currentAngle_mechanismNormalizedRotations > 0.5) {
            reference_mechanismInfiniteRotations -= 1d;
        } else if (setpoint_mechanismNormalizedRotations - currentAngle_mechanismNormalizedRotations < -0.5) {
            reference_mechanismInfiniteRotations += 1d;
        }

        controlToInfiniteRotationalReference(reference_mechanismInfiniteRotations);
    }

    @Override
    public void controlToRotationalReference(double setpoint_mechanismNormalizedRotations, double measurement_mechanismNormalizedRotations) {
        throw Exceptions.CANNOT_EXTERNAL_FEEDBACK_INTERNAL;
    }

    @Override
    public void controlToInfiniteRotationalReference(double setpoint_mechanismRotations) {
        double setpoint_encoderRotations = setpoint_mechanismRotations / motorConfig.encoderToMechanismCoefficient();

        localPidController.setReference(setpoint_encoderRotations, CANSparkMax.ControlType.kPosition);
    }

    @Override
    public void controlToInfiniteRotationalReference(double setpoint_mechanismRotations, double measurement_mechanismRotations) {
        throw Exceptions.CANNOT_EXTERNAL_FEEDBACK_INTERNAL;
    }
}
