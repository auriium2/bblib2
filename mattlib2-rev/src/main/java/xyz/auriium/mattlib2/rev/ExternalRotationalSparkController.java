package xyz.auriium.mattlib2.rev;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import xyz.auriium.mattlib2.hard.IRotationalController;
import xyz.auriium.mattlib2.hard.IRotationalPIDControl;
import xyz.auriium.mattlib2.log.components.impl.CANNetworkedConfig;
import xyz.auriium.mattlib2.log.components.impl.MotorNetworkedConfig;

public class ExternalRotationalSparkController extends BaseSparkMotor implements IRotationalController {
    final IRotationalPIDControl externalControl;

    ExternalRotationalSparkController(
            CANSparkMax sparkMax,
            CANNetworkedConfig canConfig,
            MotorNetworkedConfig motorConfig,
            RelativeEncoder encoder,
            IRotationalPIDControl externalControl
    ) {
        super(sparkMax, canConfig, motorConfig, encoder);

        this.externalControl = externalControl;
    }

    @Override
    public void controlToRotationalReference(double setpoint_mechanismNormalizedRotations) {
        externalControl.controlToRotationalReference(setpoint_mechanismNormalizedRotations);
    }

    @Override
    public void controlToRotationalReference(double setpoint_mechanismNormalizedRotations, double measurement_mechanismNormalizedRotations) {
        externalControl.controlToRotationalReference(setpoint_mechanismNormalizedRotations, measurement_mechanismNormalizedRotations);
    }

    @Override
    public void controlToInfiniteRotationalReference(double setpoint_mechanismRotations) {
        externalControl.controlToInfiniteRotationalReference(setpoint_mechanismRotations);
    }

    @Override
    public void controlToInfiniteRotationalReference(double setpoint_mechanismRotations, double measurement_mechanismRotations) {
        externalControl.controlToInfiniteRotationalReference(setpoint_mechanismRotations, measurement_mechanismRotations);
    }
}
