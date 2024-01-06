package xyz.auriium.mattlib2.rev;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.RelativeEncoder;
import xyz.auriium.mattlib2.MattLoopManager;
import xyz.auriium.mattlib2.hard.*;
import xyz.auriium.mattlib2.log.components.impl.CANNetworkedConfig;
import xyz.auriium.mattlib2.log.components.impl.MotorNetworkedConfig;
import xyz.auriium.mattlib2.log.components.impl.PIDNetworkedConfig;

public class MattREV {


    static CANSparkMax createSparkmax(CANNetworkedConfig canComponent) {
        try {
            return new CANSparkMax(
                    canComponent.canId(),
                    CANSparkMaxLowLevel.MotorType.kBrushless
            );
        } catch (IllegalStateException e) {
            throw xyz.auriium.mattlib2.hard.Exceptions.NO_CAN_ID(canComponent.selfPath(), canComponent.canId());
        }
    }

    /**
     * Set up a mattlib SparkMax device that is tasked with causing linear motion;
     * useful for swerve drive 'drive' motors, etc
     * @param loopManager important for registering the motor's logging tasks and init functions
     * @param canConfig provides CAN information like the ID and access to logging
     * @param motorConfig informs the motor on what constants to use for converting encoder to mechanism, etc
     * @return linear motor
     */
    public static ILinearMotor linearSpark_noPID(MattLoopManager loopManager, CANNetworkedConfig canConfig, MotorNetworkedConfig motorConfig) {
        if (motorConfig.rotationToMeterCoefficient().isEmpty()) {
            throw xyz.auriium.mattlib2.hard.Exceptions.MOTOR_NOT_LINEAR(motorConfig.selfPath());
        }

        CANSparkMax sparkMax = createSparkmax(canConfig);
        RelativeEncoder relativeEncoder = sparkMax.getEncoder();

        return loopManager.registerAndReturn(
                new BaseSparkMotor(sparkMax, canConfig, motorConfig, relativeEncoder)
        );
    }
    /**
     * Set up a mattlib SparkMax device that is tasked with causing rotational motion;
     * useful for things that need to rotate without control, like a dumb flywheel
     * @param loopManager important for registering the motor's logging tasks and init functions
     * @param canConfig provides CAN information like the ID and access to logging
     * @param motorConfig informs the motor on what constants to use for converting encoder to mechanism, etc
     * @return rotational motor
     */
    public static IRotationalMotor rotationalSpark_noPID(MattLoopManager loopManager, CANNetworkedConfig canConfig, MotorNetworkedConfig motorConfig) {
        CANSparkMax sparkMax = createSparkmax(canConfig);
        RelativeEncoder relativeEncoder = sparkMax.getEncoder();

        return loopManager.registerAndReturn(
                new BaseSparkMotor(sparkMax, canConfig, motorConfig, relativeEncoder)
        );
    }

    /**
     * Sets up a mattlib SparkMax device that is tasked with controlling linear motion;
     * useful for things that need to move controlled, like an elevator
     * @param loopManager important for registering the motor's logging tasks and init functions
     * @param canConfig provides CAN information like the ID and access to logging
     * @param motorConfig informs the motor on what constants to use for converting encoder to mechanism, etc
     * @param pdConfig controls
     * @return
     */
    public static ILinearController linearSpark_builtInPID(MattLoopManager loopManager, CANNetworkedConfig canConfig, MotorNetworkedConfig motorConfig, PIDNetworkedConfig pdConfig) {
        if (motorConfig.rotationToMeterCoefficient().isEmpty()) {
            throw xyz.auriium.mattlib2.hard.Exceptions.MOTOR_NOT_LINEAR(motorConfig.selfPath());
        }

        CANSparkMax sparkMax = createSparkmax(canConfig);
        RelativeEncoder relativeEncoder = sparkMax.getEncoder();

        return loopManager.registerAndReturn(
                new BuiltInSparkController(sparkMax, canConfig, motorConfig, pdConfig, relativeEncoder)
        ); //TBD
    }

    /**
     * Sets up a mattlib SparkMax device that is tasked with controlling rotational motion;
     * useful for things that need to move controlled, like a turret or steer motor
     * @param loopManager important for registering the motor's logging tasks and init functions
     * @param canConfig provides CAN information like the ID and access to logging
     * @param motorConfig informs the motor on what constants to use for converting encoder to mechanism, etc
     * @param pdConfig controls
     * @return
     */
    public static IRotationalController rotationalSpark_builtInPID(MattLoopManager loopManager, CANNetworkedConfig canConfig, MotorNetworkedConfig motorConfig, PIDNetworkedConfig pdConfig) {
        CANSparkMax sparkMax = createSparkmax(canConfig);
        RelativeEncoder relativeEncoder = sparkMax.getEncoder();

        return loopManager.registerAndReturn(
                new BuiltInSparkController(sparkMax, canConfig, motorConfig, pdConfig, relativeEncoder)
        ); //TBD
    }

    /**
     * Sets up a mattlib SparkMax device that is tasked with controlling linear motion;
     * useful for things that need to move controlled, like an elevator
     * it is controlled by a local controller and not the onboard pid controller
     * @param loopManager important for registering the motor's logging tasks and init functions
     * @param canConfig provides CAN information like the ID and access to logging
     * @param motorConfig informs the motor on what constants to use for converting encoder to mechanism, etc
     * @param pidControl an external pid controller which will be used to control this motor
     * @return
     */
    public static ILinearController linearSpark_externalPID(MattLoopManager loopManager, CANNetworkedConfig canConfig, MotorNetworkedConfig motorConfig, ILinearPIDControl pidControl) {
        if (motorConfig.rotationToMeterCoefficient().isEmpty()) {
            throw xyz.auriium.mattlib2.hard.Exceptions.MOTOR_NOT_LINEAR(motorConfig.selfPath());
        }

        CANSparkMax sparkMax = createSparkmax(canConfig);
        RelativeEncoder relativeEncoder = sparkMax.getEncoder();

        return loopManager.registerAndReturn(
                new ExternalLinearSparkController(sparkMax, relativeEncoder, canConfig, motorConfig, pidControl)
        ); //TBD
    }

    /**
     * Sets up a mattlib SparkMax device that is tasked with controlling rotational motion;
     * useful for things that need to move controlled, like a turret
     * it is controlled by a local controller and not the onboard pid controller
     * @param loopManager important for registering the motor's logging tasks and init functions
     * @param canConfig provides CAN information like the ID and access to logging
     * @param motorConfig informs the motor on what constants to use for converting encoder to mechanism, etc
     * @param pidControl an external pid controller which will be used to control this motor
     * @return
     */
    public static IRotationalController rotationalSpark_externalPID(MattLoopManager loopManager, CANNetworkedConfig canConfig, MotorNetworkedConfig motorConfig, IRotationalPIDControl pidControl) {
        if (motorConfig.rotationToMeterCoefficient().isEmpty()) {
            throw xyz.auriium.mattlib2.hard.Exceptions.MOTOR_NOT_LINEAR(motorConfig.selfPath());
        }

        CANSparkMax sparkMax = createSparkmax(canConfig);
        RelativeEncoder relativeEncoder = sparkMax.getEncoder();

        return loopManager.registerAndReturn(
                new ExternalRotationalSparkController(sparkMax, canConfig, motorConfig, relativeEncoder, pidControl)
        ); //TBD
    }





}
