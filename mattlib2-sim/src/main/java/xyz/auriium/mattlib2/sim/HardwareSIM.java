package xyz.auriium.mattlib2.sim;

import edu.wpi.first.math.MatBuilder;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N2;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import xyz.auriium.mattlib2.hardware.ILinearMotor;
import xyz.auriium.mattlib2.hardware.IRotationalMotor;
import xyz.auriium.mattlib2.hardware.config.MotorComponent;

public class HardwareSIM {

    public static ILinearMotor linearSpark_noPID(MotorComponent motorComponent, SimComponent simComponent, DCMotor gearbox) {
        if (motorComponent.rotationToMeterCoefficient().isEmpty()) {
            throw xyz.auriium.mattlib2.hardware.Exceptions.MOTOR_NOT_LINEAR(motorComponent.selfPath());
        }

        Matrix<N2, N1> mat = VecBuilder.fill(
                simComponent.positionStandardDeviation(),
                simComponent.velocityStandardDeviation()
        );

        DCMotorSim motorSim = new DCMotorSim(
                gearbox,
                1d / motorComponent.encoderToMechanismCoefficient(),
                simComponent.massMomentInertia(),
                mat
        );

        return new DCSimController(motorSim, motorComponent);
    }

    public static IRotationalMotor rotationalSpark_noPID(MotorComponent motorComponent, SimComponent simComponent, DCMotor gearbox) {
        Matrix<N2, N1> mat = VecBuilder.fill(
                simComponent.positionStandardDeviation(),
                simComponent.velocityStandardDeviation()
        );

        //TODO figure out how to make this modular
        DCMotorSim motorSim = new DCMotorSim(
                gearbox,
                1d / motorComponent.encoderToMechanismCoefficient(),
                simComponent.massMomentInertia(),
                mat
        );

        return new DCSimController(motorSim, motorComponent);
    }

}
