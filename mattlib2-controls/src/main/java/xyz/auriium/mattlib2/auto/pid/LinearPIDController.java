package xyz.auriium.mattlib2.auto.pid;

import edu.wpi.first.math.controller.PIDController;
import xyz.auriium.mattlib2.hardware.config.PIDComponent;
import xyz.auriium.mattlib2.loop.IMattlibHooked;
import xyz.auriium.yuukonstants.exception.ExplainedException;

/**
 * TODO switch this to use our own logic that isn't stateful
 * TODO this is missing an update callback, so tuning wont work
 */
public class LinearPIDController implements IMattlibHooked, IPIDController {

    final PIDComponent component;
    final PIDController internalController = new PIDController(0,0,0);

    public LinearPIDController(PIDComponent component) {
        this.component = component;

        mattRegister();
    }

    double lastReference = 0;
    double lastState = 0;

    @Override
    public ExplainedException[] verifyInit() {
        internalController.setPID(component.pConstant(),component.iConstant(),component.dConstant());
        internalController.setTolerance(component.tolerance_pidUnits());

        return IMattlibHooked.super.verifyInit();
    }

    @Override public void logPeriodic() {
        component.reportState(lastState);
        component.reportReference(lastReference);
    }

    @Override public double controlToReference_primeUnits(double setpoint_primeUnits, double state_primeUnits) {
        lastReference = setpoint_primeUnits;
        lastState = state_primeUnits;

        return internalController.calculate(state_primeUnits,setpoint_primeUnits);
    }

    @Override public boolean isAtSetpoint() {
        return internalController.atSetpoint();
    }
}
