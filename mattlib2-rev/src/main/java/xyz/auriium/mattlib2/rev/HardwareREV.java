package xyz.auriium.mattlib2.rev;

import com.revrobotics.CANSparkLowLevel;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import xyz.auriium.mattlib2.hardware.*;
import xyz.auriium.mattlib2.hardware.Exceptions;
import xyz.auriium.mattlib2.hardware.config.MotorComponent;
import xyz.auriium.mattlib2.hardware.config.PIDComponent;
import xyz.auriium.yuukonstants.GenericPath;

import java.util.HashMap;
import java.util.Map;

public class HardwareREV {

    static final Map<Integer, GenericPath> IDS_ALREADY_SEEN = new HashMap<>();


    static CANSparkMax createSparkmax(MotorComponent commonMotorComponent) {

        int canId = commonMotorComponent.id();
        GenericPath possiblyNullPath = IDS_ALREADY_SEEN.get(canId);
        GenericPath pathOfComponent  = commonMotorComponent.selfPath();

        if (possiblyNullPath != null) {
            Exceptions.DUPLICATE_IDS_FOUND(pathOfComponent, canId, possiblyNullPath);
        }


        try {
            return new CANSparkMax(
                    canId,
                    CANSparkLowLevel.MotorType.kBrushless
            );
        } catch (IllegalStateException e) {
            throw xyz.auriium.mattlib2.hardware.Exceptions.NO_CAN_ID(commonMotorComponent.selfPath(), commonMotorComponent.id(), e.getMessage());
        }
    }

    /**
     * Set up a mattlib SparkMax device that is tasked with causing linear motion;
     * useful for swerve drive 'drive' motors, etc
     * @return linear motor
     */
    public static ILinearMotor linearSpark_noPID(MotorComponent motorComponent) {
        int canId = motorComponent.id();
        GenericPath possiblyNullPath = IDS_ALREADY_SEEN.get(canId);
        GenericPath pathOfComponent  = motorComponent.selfPath();

        if (possiblyNullPath != null) {
            Exceptions.DUPLICATE_IDS_FOUND(pathOfComponent, canId, possiblyNullPath);
        }

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
        int canId = motorComponent.id();
        GenericPath possiblyNullPath = IDS_ALREADY_SEEN.get(canId);
        GenericPath pathOfComponent  = motorComponent.selfPath();

        if (possiblyNullPath != null) {
            Exceptions.DUPLICATE_IDS_FOUND(pathOfComponent, canId, possiblyNullPath);
        }


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
        int canId = motorComponent.id();
        GenericPath possiblyNullPath = IDS_ALREADY_SEEN.get(canId);
        GenericPath pathOfComponent  = motorComponent.selfPath();

        if (possiblyNullPath != null) {
            Exceptions.DUPLICATE_IDS_FOUND(pathOfComponent, canId, possiblyNullPath);
        }


        if (motorComponent.rotationToMeterCoefficient().isEmpty()) {
            throw xyz.auriium.mattlib2.hardware.Exceptions.MOTOR_NOT_LINEAR(motorComponent.selfPath());
        }

        CANSparkMax sparkMax = createSparkmax(motorComponent);
        RelativeEncoder relativeEncoder = sparkMax.getEncoder();

        return new BaseSparkController(sparkMax, motorComponent, pdConfig,  relativeEncoder);
    }

    /**
     * Sets up a mattlib SparkMax device that is tasked with controlling rotational motion;
     * useful for things that need to move controlled, like a turret or steer motor
     * @param pdConfig controls
     * @return
     */
    public static IRotationalController rotationalSpark_builtInPID(MotorComponent motorComponent, PIDComponent pdConfig) {
        int canId = motorComponent.id();
        GenericPath possiblyNullPath = IDS_ALREADY_SEEN.get(canId);
        GenericPath pathOfComponent  = motorComponent.selfPath();

        if (possiblyNullPath != null) {
            Exceptions.DUPLICATE_IDS_FOUND(pathOfComponent, canId, possiblyNullPath);
        }


        CANSparkMax sparkMax = createSparkmax(motorComponent);
        RelativeEncoder relativeEncoder = sparkMax.getEncoder();

        return new BaseSparkController(sparkMax, motorComponent, pdConfig, relativeEncoder);

    }


    /**
     * Sets up a mattlib SparkMax device that is tasked with controlling linear motion;
     * useful for things that need to move controlled, like an elevator
     * @param pdConfig controls
     * @return
     */
    public static ILinearController linearSpark_builtInVelocityPID(MotorComponent motorComponent, PIDComponent pdConfig) {
        int canId = motorComponent.id();
        GenericPath possiblyNullPath = IDS_ALREADY_SEEN.get(canId);
        GenericPath pathOfComponent  = motorComponent.selfPath();

        if (possiblyNullPath != null) {
            Exceptions.DUPLICATE_IDS_FOUND(pathOfComponent, canId, possiblyNullPath);
        }

        if (motorComponent.rotationToMeterCoefficient().isEmpty()) {
            throw xyz.auriium.mattlib2.hardware.Exceptions.MOTOR_NOT_LINEAR(motorComponent.selfPath());
        }

        CANSparkMax sparkMax = createSparkmax(motorComponent);
        RelativeEncoder relativeEncoder = sparkMax.getEncoder();

        return new BaseSparkController(sparkMax, motorComponent, pdConfig,  relativeEncoder);
    }



    /**
     * Sets up a mattlib SparkMax device that is tasked with controlling rotational velocity;
     * useful for things that need to move at a specified speed rotationally like a turntable
     * @param pdConfig controls for pid
     * @return
     */
    public static IRotationalVelocityController rotationalSpark_builtInVelocityPID(MotorComponent motorComponent, PIDComponent pdConfig) {
        int canId = motorComponent.id();
        GenericPath possiblyNullPath = IDS_ALREADY_SEEN.get(canId);
        GenericPath pathOfComponent  = motorComponent.selfPath();

        if (possiblyNullPath != null) {
            Exceptions.DUPLICATE_IDS_FOUND(pathOfComponent, canId, possiblyNullPath);
        }

        CANSparkMax sparkMax = createSparkmax(motorComponent);
        RelativeEncoder relativeEncoder = sparkMax.getEncoder();

        return new BaseSparkController(sparkMax, motorComponent, pdConfig, relativeEncoder);

    }

    /**
     * Sets up a mattlib SparkMax device that is tasked with controlling linear motion;
     * useful for things that need to move controlled, like an elevator
     * it is controlled by a local controller and not the onboard pid controller
     * @param pidControl an external pid controller which will be used to control this motor
     * @return
     */
    public static ILinearController linearSpark_externalPID(MotorComponent motorComponent, ILinearPositionControl pidControl) {
        int canId = motorComponent.id();
        GenericPath possiblyNullPath = IDS_ALREADY_SEEN.get(canId);
        GenericPath pathOfComponent  = motorComponent.selfPath();

        if (possiblyNullPath != null) {
            Exceptions.DUPLICATE_IDS_FOUND(pathOfComponent, canId, possiblyNullPath);
        }

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
        int canId = motorComponent.id();
        GenericPath possiblyNullPath = IDS_ALREADY_SEEN.get(canId);
        GenericPath pathOfComponent  = motorComponent.selfPath();

        if (possiblyNullPath != null) {
            Exceptions.DUPLICATE_IDS_FOUND(pathOfComponent, canId, possiblyNullPath);
        }

        if (motorComponent.rotationToMeterCoefficient().isEmpty()) {
            throw xyz.auriium.mattlib2.hardware.Exceptions.MOTOR_NOT_LINEAR(motorComponent.selfPath());
        }

        CANSparkMax sparkMax = createSparkmax(motorComponent);
        RelativeEncoder relativeEncoder = sparkMax.getEncoder();

        return new ExternalRotationalSparkController(sparkMax, relativeEncoder, motorComponent, pidControl);

    }





}
