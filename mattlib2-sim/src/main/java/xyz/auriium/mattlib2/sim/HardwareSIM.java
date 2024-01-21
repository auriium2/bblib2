package xyz.auriium.mattlib2.sim;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N2;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import xyz.auriium.mattlib2.hardware.*;
import xyz.auriium.mattlib2.hardware.config.CommonMotorComponent;
import xyz.auriium.mattlib2.hardware.config.MotorComponent;
import xyz.auriium.mattlib2.hardware.config.PIDComponent;

public class HardwareSIM {

    public static ILinearMotor linearSIM_noPID(MotorComponent motorComponent, DCMotor gearbox) {
        if (motorComponent.rotationToMeterCoefficient().isEmpty()) {
            throw xyz.auriium.mattlib2.hardware.Exceptions.MOTOR_NOT_LINEAR(motorComponent.selfPath());
        }

        Matrix<N2, N1> mat = VecBuilder.fill(
                motorComponent.positionStandardDeviation().orElse(0d),
                motorComponent.velocityStandardDeviation().orElse(0d)
        );

        DCMotorSim motorSim = new DCMotorSim(
                gearbox,
                1d / motorComponent.encoderToMechanismCoefficient(),
                motorComponent.massMomentInertia().orElseThrow(() -> Exceptions.NO_SIMULATION_COEFFICIENTS(motorComponent.selfPath(), CommonMotorComponent.ROTATIONAL_INERTIA)),
                mat
        );


        return new DCSimMotor(motorSim, motorComponent);
    }

    public static IRotationalMotor rotationalSIM_noPID(MotorComponent motorComponent, DCMotor gearbox) {
        Matrix<N2, N1> mat = VecBuilder.fill(
                motorComponent.positionStandardDeviation().orElse(0d),
                motorComponent.velocityStandardDeviation().orElse(0d)
        );

        DCMotorSim motorSim = new DCMotorSim(
                gearbox,
                1d / motorComponent.encoderToMechanismCoefficient(),
                motorComponent.massMomentInertia().orElseThrow(() -> Exceptions.NO_SIMULATION_COEFFICIENTS(motorComponent.selfPath(), CommonMotorComponent.ROTATIONAL_INERTIA)),
                mat
        );


        return new DCSimMotor(motorSim, motorComponent);
    }

    public static ILinearController linearSIM_pid(MotorComponent motorComponent, PIDComponent pidComponent, DCMotor gearbox) {
        if (motorComponent.rotationToMeterCoefficient().isEmpty()) {
            throw xyz.auriium.mattlib2.hardware.Exceptions.MOTOR_NOT_LINEAR(motorComponent.selfPath());
        }

        Matrix<N2, N1> mat = VecBuilder.fill(
                motorComponent.positionStandardDeviation().orElse(0d),
                motorComponent.velocityStandardDeviation().orElse(0d)
        );

        DCMotorSim motorSim = new DCMotorSim(
                gearbox,
                1d / motorComponent.encoderToMechanismCoefficient(),
                motorComponent.massMomentInertia().orElseThrow(() -> Exceptions.NO_SIMULATION_COEFFICIENTS(motorComponent.selfPath(), CommonMotorComponent.ROTATIONAL_INERTIA)),
                mat
        );


        return new DCSimController(
                motorSim,
                motorComponent,
                new PIDController(
                        pidComponent.pConstant(),
                        pidComponent.iConstant(),
                        pidComponent.dConstant()
                ),
                pidComponent);
    }

    public static IRotationalController rotationalSIM_pid(MotorComponent motorComponent, PIDComponent pidComponent, DCMotor gearbox) {

        Matrix<N2, N1> mat = VecBuilder.fill(
                motorComponent.positionStandardDeviation().orElse(0d),
                motorComponent.velocityStandardDeviation().orElse(0d)
        );

        DCMotorSim motorSim = new DCMotorSim(
                gearbox,
                1d / motorComponent.encoderToMechanismCoefficient(),
                motorComponent.massMomentInertia().orElseThrow(() -> Exceptions.NO_SIMULATION_COEFFICIENTS(motorComponent.selfPath(), CommonMotorComponent.ROTATIONAL_INERTIA)),
                mat
        );

        return new DCSimController(
                motorSim,
                motorComponent,
                new PIDController(
                        pidComponent.pConstant(),
                        pidComponent.iConstant(),
                        pidComponent.dConstant()
                ),
                pidComponent
        );
    }

}
