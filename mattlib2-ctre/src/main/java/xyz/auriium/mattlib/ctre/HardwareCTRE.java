package xyz.auriium.mattlib.ctre;

import com.ctre.phoenix6.hardware.TalonFX;
import xyz.auriium.mattlib2.hardware.*;
import xyz.auriium.mattlib2.hardware.Exceptions;
import xyz.auriium.mattlib2.hardware.config.MotorComponent;
import xyz.auriium.mattlib2.hardware.config.PIDComponent;
import xyz.auriium.yuukonstants.GenericPath;

import java.util.HashMap;
import java.util.Map;

public class HardwareCTRE {

    static final Map<Integer, GenericPath> IDS_ALREADY_SEEN = new HashMap<>();

    public static ILinearMotor linearFX_noPID(MotorComponent motorComponent) {
        int canId = motorComponent.id();
        GenericPath possiblyNullPath = IDS_ALREADY_SEEN.get(canId);
        GenericPath pathOfComponent  = motorComponent.selfPath();

        if (possiblyNullPath != null) {
            Exceptions.DUPLICATE_IDS_FOUND(pathOfComponent, canId, possiblyNullPath);
        }
        if (motorComponent.rotationToMeterCoefficient().isEmpty()) {
            throw xyz.auriium.mattlib2.hardware.Exceptions.MOTOR_NOT_LINEAR(motorComponent.selfPath());
        }

        TalonFX talonFX = new TalonFX(canId);

        return new BaseTalonFXMotor(talonFX, motorComponent);
    }


    public static IRotationalMotor rotationalFX_noPID(MotorComponent motorComponent) {
        int canId = motorComponent.id();
        GenericPath possiblyNullPath = IDS_ALREADY_SEEN.get(canId);
        GenericPath pathOfComponent  = motorComponent.selfPath();

        if (possiblyNullPath != null) {
            Exceptions.DUPLICATE_IDS_FOUND(pathOfComponent, canId, possiblyNullPath);
        }

        TalonFX talonFX = new TalonFX(canId);

        return new BaseTalonFXMotor(talonFX, motorComponent);
    }

    public static ILinearController linearFX_builtInPID(MotorComponent motorComponent, PIDComponent pidComponent) {
        int canId = motorComponent.id();
        GenericPath possiblyNullPath = IDS_ALREADY_SEEN.get(canId);
        GenericPath pathOfComponent  = motorComponent.selfPath();

        if (possiblyNullPath != null) {
            Exceptions.DUPLICATE_IDS_FOUND(pathOfComponent, canId, possiblyNullPath);
        }
        if (motorComponent.rotationToMeterCoefficient().isEmpty()) {
            throw xyz.auriium.mattlib2.hardware.Exceptions.MOTOR_NOT_LINEAR(motorComponent.selfPath());
        }

        TalonFX talonFX = new TalonFX(canId);

        return new BaseTalonFXController(talonFX, motorComponent, pidComponent);
    }

    public static IRotationalController rotationalFX_builtInPID(MotorComponent motorComponent, PIDComponent pidComponent) {
        int canId = motorComponent.id();
        GenericPath possiblyNullPath = IDS_ALREADY_SEEN.get(canId);
        GenericPath pathOfComponent  = motorComponent.selfPath();

        if (possiblyNullPath != null) {
            Exceptions.DUPLICATE_IDS_FOUND(pathOfComponent, canId, possiblyNullPath);
        }

        TalonFX talonFX = new TalonFX(canId);

        return new BaseTalonFXController(talonFX, motorComponent, pidComponent);
    }

    public static ILinearVelocityController linearFX_builtInVelocityPID(MotorComponent motorComponent, PIDComponent pidComponent) {
        int canId = motorComponent.id();
        GenericPath possiblyNullPath = IDS_ALREADY_SEEN.get(canId);
        GenericPath pathOfComponent  = motorComponent.selfPath();

        if (possiblyNullPath != null) {
            Exceptions.DUPLICATE_IDS_FOUND(pathOfComponent, canId, possiblyNullPath);
        }
        if (motorComponent.rotationToMeterCoefficient().isEmpty()) {
            throw xyz.auriium.mattlib2.hardware.Exceptions.MOTOR_NOT_LINEAR(motorComponent.selfPath());
        }

        TalonFX talonFX = new TalonFX(canId);

        return new BaseTalonFXController(talonFX, motorComponent, pidComponent);
    }
}
