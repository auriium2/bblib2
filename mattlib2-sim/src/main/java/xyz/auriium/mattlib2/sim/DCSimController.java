package xyz.auriium.mattlib2.sim;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import xyz.auriium.mattlib2.hardware.Exceptions;
import xyz.auriium.mattlib2.hardware.ILinearController;
import xyz.auriium.mattlib2.hardware.IRotationalController;
import xyz.auriium.mattlib2.hardware.config.MotorComponent;
import xyz.auriium.mattlib2.hardware.config.PIDComponent;

public class DCSimController extends DCSimMotor implements ILinearController, IRotationalController {


    /**
     * Units in encoder rotations
     */
    final PIDController pidController;
    final PIDComponent pidComponent;


    public DCSimController(DCMotorSim motorSim, MotorComponent motorComponent, PIDController pidController, PIDComponent pidComponent) {
        super(motorSim, motorComponent);
        this.pidController = pidController; //
        this.pidComponent = pidComponent;

    }

    @Override
    public void tunePeriodic() {
        if (pidComponent.hasUpdated()) {
            pidController.reset();
            pidController.setP(pidComponent.pConstant());
            pidController.setI(pidComponent.iConstant());
            pidController.setD(pidComponent.dConstant());
        }
    }

    @Override
    public void controlToLinearReference(double setpointMechanism_meters) {
        double coef = motorComponent.rotationToMeterCoefficient().orElseThrow(() -> Exceptions.MOTOR_NOT_LINEAR(motorComponent.selfPath()));

        double controlEffort = pidController.calculate(
                this.angularPosition_encoderRotations(),
                setpointMechanism_meters
                / coef
                // / motorComponent.encoderToMechanismCoefficient() WHAT THE FUCK
        );

        this.setToVoltage(controlEffort);
    }

    @Override
    public void controlToLinearReference(double setpointMechanism_meters, double measurementMechanism_meters) {

        double coef = motorComponent.rotationToMeterCoefficient().orElseThrow(() -> Exceptions.MOTOR_NOT_LINEAR(motorComponent.selfPath()));
        double controlEffort = pidController.calculate(
                angularPosition_encoderRotations(),
                setpointMechanism_meters
                / coef
                // / motorComponent.encoderToMechanismCoefficient() WHAT THE FUCK
        );

        this.setToVoltage(controlEffort);
    }

    @Override
    public void controlToNormalizedReference(double setpoint_mechanismNormalizedRotations) {
        controlToNormalizedReference(setpoint_mechanismNormalizedRotations, angularPosition_mechanismRotations());
    }

    @Override
    public void controlToNormalizedReference(double setpoint_mechanismNormalizedRotations, double measurement_mechanismNormalizedRotations) {

        double currentAngle_mechanismNormalizedRotations = measurement_mechanismNormalizedRotations % 1d;
        if (currentAngle_mechanismNormalizedRotations < 0d) {
            currentAngle_mechanismNormalizedRotations += 1d; //no idea why this works
        }

        // take (infinite - normalized) for (current offset) then add (setpoint normalized) for (setpoint infinite)
        double reference_mechanismInfiniteRotations = setpoint_mechanismNormalizedRotations
                + measurement_mechanismNormalizedRotations
                - currentAngle_mechanismNormalizedRotations;


        // more modulus code i don't understand
        if (setpoint_mechanismNormalizedRotations - currentAngle_mechanismNormalizedRotations > 0.5) {
            reference_mechanismInfiniteRotations -= 1d;
        } else if (setpoint_mechanismNormalizedRotations - currentAngle_mechanismNormalizedRotations < -0.5) {
            reference_mechanismInfiniteRotations += 1d;
        }

        controlToInfiniteReference(reference_mechanismInfiniteRotations);

    }

    @Override
    public void controlToInfiniteReference(double setpoint_mechanismRotations) {
        double controlEffort = pidController.calculate(
                this.angularPosition_encoderRotations(),
                setpoint_mechanismRotations
                       //  / motorComponent.encoderToMechanismCoefficient() WHAT THE FUCK
        );

        this.setToVoltage(controlEffort);
    }

    @Override
    public void controlToInfiniteReference(double setpoint_mechanismRotations, double measurement_mechanismRotations) {
        double controlEffort = pidController.calculate(
                measurement_mechanismRotations,
                setpoint_mechanismRotations
                     //   / motorComponent.encoderToMechanismCoefficient() WHAT THE FUCK
        );

        this.setToVoltage(controlEffort);
    }
}

