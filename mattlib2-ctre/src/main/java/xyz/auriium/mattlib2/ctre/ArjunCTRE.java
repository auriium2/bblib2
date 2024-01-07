package xyz.auriium.mattlib2.ctre;

import edu.wpi.first.wpilibj.motorcontrol.Talon;
import xyz.auriium.mattlib2.MattLoopManager;
import xyz.auriium.mattlib2.hard.ILinearController;
import xyz.auriium.mattlib2.hard.ILinearMotor;
import xyz.auriium.mattlib2.hard.IRotationalController;
import xyz.auriium.mattlib2.hard.IRotationalMotor;
import xyz.auriium.mattlib2.log.components.impl.CANNetworkedConfig;
import xyz.auriium.mattlib2.log.components.impl.MotorNetworkedConfig;


public class ArjunCTRE {

    static Talon createTalon(CANNetworkedConfig canComponent) {
        try {
            return new Talon(
                    canComponent.canId()
            );
        } catch (IllegalStateException e) {
            throw xyz.auriium.mattlib2.hard.Exceptions.NO_CAN_ID(canComponent.selfPath(), canComponent.canId());
        }
    }

    public static ILinearMotor linearTalon_noPID(MattLoopManager loopManager, CANNetworkedConfig canConfig, MotorNetworkedConfig motorConfig) {
        if (motorConfig.rotationToMeterCoefficient().isEmpty()) {
            throw xyz.auriium.mattlib2.hard.Exceptions.MOTOR_NOT_LINEAR(motorConfig.selfPath());
        }

        Talon talon = createTalon(canConfig);
        return ;
    }
    /**
     * Set up a mattlib SparkMax device that is tasked with controlling rotational motion
     * Useful for swerve drive 'steer' motors, etc
     * @param loopManager important for registering the motor's logging tasks and init functions
     * @param canConfig provides CAN information like the ID and access to logging
     * @param motorConfig the motor config
     * @return rotational motor
     */
    public static IRotationalMotor rotationalTalon_noPID(MattLoopManager loopManager, CANNetworkedConfig canConfig, MotorNetworkedConfig motorConfig) {

    }

    public static ILinearController linearTalon_builtInPD(MattLoopManager loopManager, CANNetworkedConfig canConfig, MotorNetworkedConfig motorConfig, PIDConfig pdConfig) {

    }

    public static IRotationalController rotationalTalon_builtInPD(MattLoopManager loopManager, CANNetworkedConfig canConfig, MotorNetworkedConfig motorConfig, PIDConfig pdConfig) {

    }
}
