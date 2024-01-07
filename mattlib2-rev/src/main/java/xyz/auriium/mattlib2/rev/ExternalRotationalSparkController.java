package xyz.auriium.mattlib2.rev;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import xyz.auriium.mattlib2.hardware.IRotationalController;
import xyz.auriium.mattlib2.hardware.IRotationalPIDControl;
import xyz.auriium.mattlib2.hardware.config.CommonMotorComponent;
import xyz.auriium.mattlib2.hardware.config.IndividualMotorComponent;
import xyz.auriium.mattlib2.hardware.config.MotorComponent;

public class ExternalRotationalSparkController extends BaseSparkMotor implements IRotationalController {
    final IRotationalPIDControl externalControl;

    ExternalRotationalSparkController(
            CANSparkMax sparkMax,
            RelativeEncoder encoder,
            MotorComponent motorComponent,
            IRotationalPIDControl externalControl
    ) {
        super(sparkMax, motorComponent, encoder);

        this.externalControl = externalControl;
    }

    @Override
    public void controlToNormalizedReference(double setpoint_mechanismNormalizedRotations) {
        externalControl.controlToNormalizedReference(setpoint_mechanismNormalizedRotations);
    }

    @Override
    public void controlToNormalizedReference(double setpoint_mechanismNormalizedRotations, double measurement_mechanismNormalizedRotations) {
        externalControl.controlToNormalizedReference(setpoint_mechanismNormalizedRotations, measurement_mechanismNormalizedRotations);
    }

    @Override
    public void controlToInfiniteReference(double setpoint_mechanismRotations) {
        externalControl.controlToInfiniteReference(setpoint_mechanismRotations);
    }

    @Override
    public void controlToInfiniteReference(double setpoint_mechanismRotations, double measurement_mechanismRotations) {
        externalControl.controlToInfiniteReference(setpoint_mechanismRotations, measurement_mechanismRotations);
    }
}
