package xyz.auriium.mattlib2.rev;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import xyz.auriium.mattlib2.hardware.ILinearController;
import xyz.auriium.mattlib2.hardware.ILinearPIDControl;
import xyz.auriium.mattlib2.hardware.config.CommonMotorComponent;
import xyz.auriium.mattlib2.hardware.config.IndividualMotorComponent;
import xyz.auriium.mattlib2.hardware.config.MotorComponent;

public class ExternalLinearSparkController extends BaseSparkMotor implements ILinearController {

    final ILinearPIDControl linearController;

    ExternalLinearSparkController(
            CANSparkMax sparkMax,
            RelativeEncoder relativeEncoder,
            MotorComponent motorComponent,
            ILinearPIDControl externalController
    ) {

        super(sparkMax, motorComponent, relativeEncoder);

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
