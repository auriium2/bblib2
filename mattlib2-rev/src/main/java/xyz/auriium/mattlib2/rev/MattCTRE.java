package xyz.auriium.mattlib2.rev;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.RelativeEncoder;
import xyz.auriium.mattlib2.MattLoopManager;
import xyz.auriium.mattlib2.hard.ILinearController;
import xyz.auriium.mattlib2.hard.ILinearMotor;
import xyz.auriium.mattlib2.hard.IRotationalMotor;
import xyz.auriium.mattlib2.log.components.impl.CANNetworkedConfig;
import xyz.auriium.mattlib2.log.components.impl.MotorNetworkedConfig;
import xyz.auriium.mattlib2.log.components.impl.PIDConfig;

public class MattCTRE {


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
     * Set up a mattlib SparkMax device that is tasked with controlling linear motion
     * Useful for swerve drive 'drive' motors, etc
     * @param loopManager important for registering the motor's logging tasks and init functions
     * @param canConfig provides CAN information like the ID and access to logging
     * @param motorConfig the motor config
     * @return linear motor
     */
    public static ILinearMotor linearSpark_noPID(MattLoopManager loopManager, CANNetworkedConfig canConfig, MotorNetworkedConfig motorConfig) {
        if (motorConfig.rotationToMeterCoefficient().isEmpty()) {
            throw xyz.auriium.mattlib2.hard.Exceptions.MOTOR_NOT_LINEAR(motorConfig.selfPath());
        }

        CANSparkMax sparkMax = createSparkmax(canConfig);
        RelativeEncoder relativeEncoder = sparkMax.getEncoder();

        return loopManager.registerAndReturn(new BaseSparkMotor(sparkMax, canConfig, motorConfig, relativeEncoder));
    }
    /**
     * Set up a mattlib SparkMax device that is tasked with controlling rotational motion
     * Useful for swerve drive 'steer' motors, etc
     * @param loopManager important for registering the motor's logging tasks and init functions
     * @param canConfig provides CAN information like the ID and access to logging
     * @param motorConfig the motor config
     * @return rotational motor
     */
    public static IRotationalMotor rotationalSpark_noPID(MattLoopManager loopManager, CANNetworkedConfig canConfig, MotorNetworkedConfig motorConfig) {
        CANSparkMax sparkMax = createSparkmax(canConfig);
        RelativeEncoder relativeEncoder = sparkMax.getEncoder();

        return loopManager.registerAndReturn(new BaseSparkMotor(sparkMax, canConfig, motorConfig, relativeEncoder));
    }

    public static ILinearController linearSpark_builtInPD(MattLoopManager loopManager, CANNetworkedConfig canConfig, MotorNetworkedConfig motorConfig, PIDConfig pdConfig) {
        if (motorConfig.rotationToMeterCoefficient().isEmpty()) {
            throw xyz.auriium.mattlib2.hard.Exceptions.MOTOR_NOT_LINEAR(motorConfig.selfPath());
        }

        CANSparkMax sparkMax = createSparkmax(canConfig);
        RelativeEncoder relativeEncoder = sparkMax.getEncoder();

        return null; //TBD
    }



}
