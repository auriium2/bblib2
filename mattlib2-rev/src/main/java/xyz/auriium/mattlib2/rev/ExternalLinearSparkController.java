package xyz.auriium.mattlib2.rev;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import xyz.auriium.mattlib2.hard.ILinearController;
import xyz.auriium.mattlib2.hard.ILinearPIDControl;
import xyz.auriium.mattlib2.log.components.impl.CANNetworkedConfig;
import xyz.auriium.mattlib2.log.components.impl.MotorNetworkedConfig;

public class ExternalLinearSparkController extends BaseSparkMotor implements ILinearController {

    final ILinearPIDControl linearController;

    ExternalLinearSparkController(
            CANSparkMax sparkMax,
            RelativeEncoder relativeEncoder,
            CANNetworkedConfig canConfig,
            MotorNetworkedConfig motorConfig,
            ILinearPIDControl externalController
    ) {

        super(sparkMax, canConfig, motorConfig, relativeEncoder);

        linearController = externalController;
    }

    @Override
    public void controlToLinearReference(double setpointMechanism_meters) {
        linearController.controlToLinearReference(setpointMechanism_meters);
    }

    @Override
    public void controlToLinearReference(double setpointMechanism_meters, double measurementMechanism_meters) {
        linearController.controlToLinearReference(setpointMechanism_meters, measurementMechanism_meters);
    }
}
