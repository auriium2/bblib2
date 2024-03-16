package xyz.auriium.mattlib2.rev;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import xyz.auriium.mattlib2.hardware.IRotationEncoder;
import xyz.auriium.mattlib2.hardware.IRotationalController;
import xyz.auriium.mattlib2.hardware.config.MotorComponent;
import xyz.auriium.mattlib2.hardware.config.PIDComponent;
import xyz.auriium.yuukonstants.exception.ExplainedException;

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

    @Override public ExplainedException[] verifyInit() {
        var ee =  super.verifyInit();

        pidController.setPID(
                pidComponent.pConstant(),
                pidComponent.iConstant(),
                pidComponent.dConstant()
        );

        pidComponent.tolerance_pidUnits().ifPresent(pidController::setTolerance);

        return ee;
    }

    @Override public void logPeriodic() {
        super.logPeriodic();

        pidComponent.reportState(observation_primeUnits);
        pidComponent.reportReference(setpoint_primeUnits);
    }

    @Override public void tunePeriodic() {
        super.tunePeriodic();

        //TODO
/*
        if (pidComponent.hasUpdated()) {
            pidController.setP(pidComponent.pConstant());
            pidController.setI(pidComponent.iConstant());
            pidController.setD(pidComponent.dConstant());
            pidComponent.tolerance_pidUnits().ifPresent(pidController::setTolerance);
        }*/
    }

    @Override public void stopActuator() {
        super.stopActuator();

        pidController.reset();
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
