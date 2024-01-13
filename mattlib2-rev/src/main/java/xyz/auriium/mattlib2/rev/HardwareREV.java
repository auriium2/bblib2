package xyz.auriium.mattlib2.rev;

import com.revrobotics.CANSparkLowLevel;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import xyz.auriium.mattlib2.hardware.*;
import xyz.auriium.mattlib2.hardware.config.MotorComponent;
import xyz.auriium.mattlib2.hardware.config.PIDComponent;

public class HardwareREV {


    static CANSparkMax createSparkmax(MotorComponent commonMotorComponent) {
        try {
            return new CANSparkMax(
                    commonMotorComponent.id(),
                    CANSparkLowLevel.MotorType.kBrushless
            );
        } catch (IllegalStateException e) {
            throw xyz.auriium.mattlib2.hardware.Exceptions.NO_CAN_ID(commonMotorComponent.selfPath(), commonMotorComponent.id());
        }
    }

    /**
     * Set up a mattlib SparkMax device that is tasked with causing linear motion;
     * useful for swerve drive 'drive' motors, etc
     * @return linear motor
     */
    public static ILinearMotor linearSpark_noPID(MotorComponent motorComponent) {
        if (motorComponent.rotationToMeterCoefficient().isEmpty()) {
            throw xyz.auriium.mattlib2.hardware.Exceptions.MOTOR_NOT_LINEAR(motorComponent.selfPath());
        }

        CANSparkMax sparkMax = createSparkmax(motorComponent);
        RelativeEncoder relativeEncoder = sparkMax.getEncoder();

        return new BaseSparkMotor(sparkMax, motorComponent, relativeEncoder);
    }
    /**
     * Set up a mattlib SparkMax device that is tasked with causing rotational motion;
     * useful for things that need to rotate without control, like a dumb flywheel
     * @return rotational motor
     */
    public static IRotationalMotor rotationalSpark_noPID(MotorComponent motorComponent) {
        CANSparkMax sparkMax = createSparkmax(motorComponent);
        RelativeEncoder relativeEncoder = sparkMax.getEncoder();

        return new BaseSparkMotor(sparkMax, motorComponent, relativeEncoder);
    }

    /**
     * Sets up a mattlib SparkMax device that is tasked with controlling linear motion;
     * useful for things that need to move controlled, like an elevator
     * @param pdConfig controls
     * @return
     */
    public static ILinearController linearSpark_builtInPID(MotorComponent motorComponent, PIDComponent pdConfig) {
        if (motorComponent.rotationToMeterCoefficient().isEmpty()) {
            throw xyz.auriium.mattlib2.hardware.Exceptions.MOTOR_NOT_LINEAR(motorComponent.selfPath());
        }

        CANSparkMax sparkMax = createSparkmax(motorComponent);
        RelativeEncoder relativeEncoder = sparkMax.getEncoder();

        return new BuiltInSparkController(sparkMax, motorComponent, pdConfig,  relativeEncoder);
    }

    /**
     * Sets up a mattlib SparkMax device that is tasked with controlling rotational motion;
     * useful for things that need to move controlled, like a turret or steer motor
     * @param pdConfig controls
     * @return
     */
    public static IRotationalController rotationalSpark_builtInPID(MotorComponent motorComponent, PIDComponent pdConfig) {
        CANSparkMax sparkMax = createSparkmax(motorComponent);
        RelativeEncoder relativeEncoder = sparkMax.getEncoder();

        return new BuiltInSparkController(sparkMax, motorComponent, pdConfig, relativeEncoder);

    }


    /**
     * Sets up a mattlib SparkMax device that is tasked with controlling linear motion;
     * useful for things that need to move controlled, like an elevator
     * @param pdConfig controls
     * @return
     */
    public static ILinearController linearSpark_builtInVelocityPID(MotorComponent motorComponent, PIDComponent pdConfig) {
        if (motorComponent.rotationToMeterCoefficient().isEmpty()) {
            throw xyz.auriium.mattlib2.hardware.Exceptions.MOTOR_NOT_LINEAR(motorComponent.selfPath());
        }

        CANSparkMax sparkMax = createSparkmax(motorComponent);
        RelativeEncoder relativeEncoder = sparkMax.getEncoder();

        return new BuiltInSparkController(sparkMax, motorComponent, pdConfig,  relativeEncoder);
    }



    /**
     * Sets up a mattlib SparkMax device that is tasked with controlling rotational velocity;
     * useful for things that need to move at a specified speed rotationally like a turntable
     * @param pdConfig controls for pid
     * @return
     */
    public static IRotationalVelocityController rotationalSpark_builtInVelocityPID(MotorComponent motorComponent, PIDComponent pdConfig) {
        CANSparkMax sparkMax = createSparkmax(motorComponent);
        RelativeEncoder relativeEncoder = sparkMax.getEncoder();

        return new BuiltInSparkController(sparkMax, motorComponent, pdConfig, relativeEncoder);

    }

    /**
     * Sets up a mattlib SparkMax device that is tasked with controlling linear motion;
     * useful for things that need to move controlled, like an elevator
     * it is controlled by a local controller and not the onboard pid controller
     * @param pidControl an external pid controller which will be used to control this motor
     * @return
     */
    public static ILinearController linearSpark_externalPID(MotorComponent motorComponent, ILinearPositionControl pidControl) {
        if (motorComponent.rotationToMeterCoefficient().isEmpty()) {
            throw xyz.auriium.mattlib2.hardware.Exceptions.MOTOR_NOT_LINEAR(motorComponent.selfPath());
        }

        CANSparkMax sparkMax = createSparkmax(motorComponent);
        RelativeEncoder relativeEncoder = sparkMax.getEncoder();

        return new ExternalLinearSparkController(sparkMax, relativeEncoder, motorComponent, pidControl);

    }


    /**
     * Sets up a mattlib SparkMax device that is tasked with controlling rotational motion;
     * useful for things that need to move controlled, like a turret
     * it is controlled by a local controller and not the onboard pid controller
     * @param pidControl an external pid controller which will be used to control this motor
     * @return
     */
    public static IRotationalController rotationalSpark_externalPID(MotorComponent motorComponent, IRotationalPositionControl pidControl) {
        if (motorComponent.rotationToMeterCoefficient().isEmpty()) {
            throw xyz.auriium.mattlib2.hardware.Exceptions.MOTOR_NOT_LINEAR(motorComponent.selfPath());
        }

        CANSparkMax sparkMax = createSparkmax(motorComponent);
        RelativeEncoder relativeEncoder = sparkMax.getEncoder();

        return new ExternalRotationalSparkController(sparkMax, relativeEncoder, motorComponent, pidControl);

    }





}
