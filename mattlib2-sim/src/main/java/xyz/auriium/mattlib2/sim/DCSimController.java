package xyz.auriium.mattlib2.sim;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import xyz.auriium.mattlib2.hardware.Exceptions;
import xyz.auriium.mattlib2.hardware.ILinearController;
import xyz.auriium.mattlib2.hardware.IRotationalController;
import xyz.auriium.mattlib2.hardware.config.MotorComponent;

public class DCSimController extends DCSimMotor implements ILinearController, IRotationalController {


    /**
     * Units in encoder rotations
     */
    final PIDController pidController;
    public DCSimController(DCMotorSim motorSim, MotorComponent motorComponent, PIDController pidController) {
        super(motorSim, motorComponent);
        this.pidController = pidController; //
    }



    @Override
    public void controlToLinearReference(double setpointMechanism_meters) {
        if (pidController.isContinuousInputEnabled()) {
            pidController.disableContinuousInput();
        }

        double coef = motorComponent.rotationToMeterCoefficient().orElseThrow(() -> Exceptions.MOTOR_NOT_LINEAR(motorComponent.selfPath()));

        double controlEffort = pidController.calculate(
                this.angularPosition_encoderRotations(),
                setpointMechanism_meters
                / coef
                / motorComponent.encoderToMechanismCoefficient()
        );

        this.setToVoltage(controlEffort);
    }

    @Override
    public void controlToLinearReference(double setpointMechanism_meters, double measurementMechanism_meters) {
        if (pidController.isContinuousInputEnabled()) {
            pidController.disableContinuousInput();
        }


        double coef = motorComponent.rotationToMeterCoefficient().orElseThrow(() -> Exceptions.MOTOR_NOT_LINEAR(motorComponent.selfPath()));
        double controlEffort = pidController.calculate(
                angularPosition_encoderRotations(),
                setpointMechanism_meters
                / coef
                / motorComponent.encoderToMechanismCoefficient()
        );

        this.setToVoltage(controlEffort);
    }

    @Override
    public void controlToNormalizedReference(double setpoint_mechanismNormalizedRotations) {
        if (!pidController.isContinuousInputEnabled()) {
            pidController.enableContinuousInput(0,1);
        }

        double controlEffort = pidController.calculate(
                this.angularPosition_encoderRotations(),
                setpoint_mechanismNormalizedRotations
                        / motorComponent.encoderToMechanismCoefficient()
        );

        this.setToVoltage(controlEffort);

    }

    @Override
    public void controlToNormalizedReference(double setpoint_mechanismNormalizedRotations, double measurement_mechanismNormalizedRotations) {
        if (!pidController.isContinuousInputEnabled()) {
            pidController.enableContinuousInput(0,1);
        }

        double controlEffort = pidController.calculate(
                measurement_mechanismNormalizedRotations,
                setpoint_mechanismNormalizedRotations
                        / motorComponent.encoderToMechanismCoefficient()
        );

        this.setToVoltage(controlEffort);
    }

    @Override
    public void controlToInfiniteReference(double setpoint_mechanismRotations) {
        if (pidController.isContinuousInputEnabled()) {
            pidController.disableContinuousInput();
        }

        double controlEffort = pidController.calculate(
                this.angularPosition_encoderRotations(),
                setpoint_mechanismRotations
                        / motorComponent.encoderToMechanismCoefficient()
        );

        this.setToVoltage(controlEffort);
    }

    @Override
    public void controlToInfiniteReference(double setpoint_mechanismRotations, double measurement_mechanismRotations) {
        if (pidController.isContinuousInputEnabled()) {
            pidController.disableContinuousInput();
        }

        double controlEffort = pidController.calculate(
                measurement_mechanismRotations,
                setpoint_mechanismRotations
                        / motorComponent.encoderToMechanismCoefficient()
        );

        this.setToVoltage(controlEffort);
    }
}

