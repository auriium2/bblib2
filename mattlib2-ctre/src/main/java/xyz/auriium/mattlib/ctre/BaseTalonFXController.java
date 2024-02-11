package xyz.auriium.mattlib.ctre;

import com.ctre.phoenix6.hardware.TalonFX;
import xyz.auriium.mattlib2.hardware.ILinearController;
import xyz.auriium.mattlib2.hardware.IRotationalController;
import xyz.auriium.mattlib2.hardware.config.MotorComponent;

public class BaseTalonFXController extends BaseTalonFXMotor implements ILinearController, IRotationalController {
    public BaseTalonFXController(TalonFX talonFX, MotorComponent motorComponent) {
        super(talonFX, motorComponent);
    }

    @Override public void controlToLinearReferenceArbitrary(double setpointMechanism_meters, double arbitraryFF_volts) {

    }

    @Override public void controlToNormalizedReferenceArbitrary(double setpoint_mechanismNormalizedRotations, double arbitraryFF_volts) {

    }

    @Override public void controlToInfiniteReferenceArbitrary(double setpoint_mechanismRotations, double arbitraryFF_volts) {

    }
}
