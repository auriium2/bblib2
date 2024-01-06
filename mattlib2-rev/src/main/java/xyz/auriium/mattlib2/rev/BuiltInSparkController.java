package xyz.auriium.mattlib2.rev;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import xyz.auriium.mattlib2.hard.ILinearController;
import xyz.auriium.mattlib2.hard.IRotationalController;
import xyz.auriium.mattlib2.log.components.impl.CANNetworkedConfig;
import xyz.auriium.mattlib2.log.components.impl.MotorNetworkedConfig;
import xyz.auriium.mattlib2.log.components.impl.PIDConfig;

import java.util.Optional;

public class BuiltInSparkController extends BaseSparkMotor implements ILinearController, IRotationalController {

    final SparkMaxPIDController localPidController;
    final PIDConfig PIDConfig;

    BuiltInSparkController(CANSparkMax sparkMax, CANNetworkedConfig canConfig, MotorNetworkedConfig motorConfig, PIDConfig pdConfig, RelativeEncoder encoder) {
        super(sparkMax, canConfig, motorConfig, encoder);
        localPidController = sparkMax.getPIDController();
        PIDConfig = pdConfig;
    }

    //Logging stuff

    @Override
    public void init() {
        localPidController.setP(PIDConfig.pConstant());
        localPidController.setI(PIDConfig.iConstant());
        localPidController.setD(PIDConfig.dConstant());
    }

    @Override
    public void tunePeriodic() {
        if (PIDConfig.hasUpdated()) {
            localPidController.setP(PIDConfig.pConstant());
            localPidController.setI(PIDConfig.iConstant());
            localPidController.setD(PIDConfig.dConstant());
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

    }

    @Override
    public void controlToRotationalReference(double setpointMechanism_normalizedRotations) {

    }

    @Override
    public void controlToRotationalReference(double setpointMechanism_normalizedRotations, double measurementMechanism_normalizedRotations) {

    }
}
