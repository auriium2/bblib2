package xyz.auriium.mattlib2.auto.pid;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import xyz.auriium.mattlib2.hardware.config.PIDComponent;
import xyz.auriium.mattlib2.loop.IMattlibHooked;
import xyz.auriium.yuukonstants.exception.ExplainedException;

/**
 * TODO switch this to use our own logic that isn't stateful
 * TODO this is missing an update callback, so tuning wont work
 */
public class RotationalProfiledController implements IMattlibHooked, IPIDController {

    final PIDComponent component;
    final ProfiledPIDController internalController = new ProfiledPIDController(0,0,0, new TrapezoidProfile.Constraints(0,0));

    public RotationalProfiledController(PIDComponent component) {
        this.component = component;

        mattRegister();
    }

    double lastReference = 0;
    double lastState = 0;

    @Override
    public ExplainedException[] verifyInit() {
        internalController.setPID(component.pConstant(),component.iConstant(),component.dConstant());
        internalController.setTolerance(component.tolerance_pidUnits());
        internalController.enableContinuousInput(-Math.PI, Math.PI);

        return IMattlibHooked.super.verifyInit();
    }


    @Override public void logPeriodic() {
        component.reportState(lastState);
        component.reportReference(lastReference);
    }

    @Override public double controlToReference_primeUnits(double setpoint_primeUnits, double state_primeUnits) {
        lastReference = setpoint_primeUnits;
        lastState = state_primeUnits; //TODO do we want to wrap the state? let's wrap the state!

        return internalController.calculate(state_primeUnits,setpoint_primeUnits);
    }

    @Override public boolean isAtSetpoint() {
        return internalController.atSetpoint();
    }
}