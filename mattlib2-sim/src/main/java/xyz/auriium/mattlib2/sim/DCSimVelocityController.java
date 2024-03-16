package xyz.auriium.mattlib2.sim;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import xyz.auriium.mattlib2.hardware.*;
import xyz.auriium.mattlib2.hardware.config.MotorComponent;
import xyz.auriium.mattlib2.hardware.config.PIDComponent;

public class DCSimVelocityController extends DCSimMotor implements ILinearVelocityController, IRotationalVelocityController {


    /**
     * Units in encoder rotations
     */
    final PIDController pidController;
    final PIDComponent pidComponent;


    public DCSimVelocityController(DCMotorSim motorSim, MotorComponent motorComponent, PIDController pidController, PIDComponent pidComponent) {
        super(motorSim, motorComponent);
        this.pidController = pidController; //
        this.pidComponent = pidComponent;

    }

    @Override
    public void tunePeriodic() {/*
        if (pidComponent.hasUpdated()) {
            pidController.reset();
            pidController.setP(pidComponent.pConstant());
            pidController.setI(pidComponent.iConstant());
            pidController.setD(pidComponent.dConstant());
        }*/
    }

    @Override public void controlToLinearVelocityReferenceArbitrary(double setPointMechanism_metersPerSecond, double arbFF) {
        var u = pidController.calculate(linearVelocity_mechanismMetersPerSecond(), setPointMechanism_metersPerSecond);
        setToVoltage(u + arbFF);
    }

    @Override public void controlToRotationalVelocityReferenceArbitrary(double setPointMechanism_rotationsPerSecond, double arbFF) {
        var u =pidController.calculate(angularVelocity_mechanismRotationsPerSecond(),setPointMechanism_rotationsPerSecond);
        setToVoltage(u + arbFF);
    }
}

