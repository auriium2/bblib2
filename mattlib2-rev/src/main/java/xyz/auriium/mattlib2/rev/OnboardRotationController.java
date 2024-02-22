package xyz.auriium.mattlib2.rev;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import edu.wpi.first.math.controller.PIDController;
import xyz.auriium.mattlib2.hardware.IRotationEncoder;
import xyz.auriium.mattlib2.hardware.IRotationalController;
import xyz.auriium.mattlib2.hardware.config.MotorComponent;
import xyz.auriium.mattlib2.hardware.config.PIDComponent;

public class OnboardRotationController extends BaseSparkMotor implements IRotationalController {

    final IRotationEncoder stateObserver;
    final PIDComponent pidComponent;

    public OnboardRotationController(CANSparkMax sparkMax, MotorComponent motorComponent, RelativeEncoder encoder, IRotationEncoder stateObserver, PIDComponent pidComponent) {
        super(sparkMax, motorComponent, encoder);
        this.stateObserver = stateObserver;
        this.pidComponent = pidComponent;
    }

    final PIDController pidController = new PIDController(0,0,0);
    boolean normalizedMode;

    double setpoint_primeUnits = 0;
    double observation_primeUnits = 0;

    @Override public void logPeriodic() {
        pidComponent.reportState(observation_primeUnits);
        pidComponent.reportReference(setpoint_primeUnits);
    }

    @Override
    public void controlToNormalizedReferenceArbitrary(double setpoint_mechanismNormalizedRotations, double arbitraryFF_volts) {
        if (!normalizedMode) {
            normalizedMode = true;
            pidController.enableContinuousInput(0,1);
        }

        double measurement_mechanismNormalizedRotations = stateObserver.angularPosition_normalizedMechanismRotations();
        double feedbackVoltage = pidController.calculate(measurement_mechanismNormalizedRotations, setpoint_mechanismNormalizedRotations);

        setpoint_primeUnits = setpoint_mechanismNormalizedRotations;
        observation_primeUnits = measurement_mechanismNormalizedRotations;

                setToVoltage(feedbackVoltage + arbitraryFF_volts);
    }

    @Override
    public void controlToInfiniteReferenceArbitrary(double setpoint_mechanismRotations, double arbitraryFF_volts) {
        if (normalizedMode) {
            normalizedMode = false;
            pidController.disableContinuousInput();
        }

        double measurement_mechanismArbitraryRotations = stateObserver.angularPosition_mechanismRotations();
        double feedbackVoltage = pidController.calculate(measurement_mechanismArbitraryRotations, setpoint_mechanismRotations);

        setpoint_primeUnits = setpoint_mechanismRotations;
        observation_primeUnits = measurement_mechanismArbitraryRotations;

        setToVoltage(feedbackVoltage + arbitraryFF_volts);
    }
}
