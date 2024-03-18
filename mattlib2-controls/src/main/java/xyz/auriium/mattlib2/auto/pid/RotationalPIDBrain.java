package xyz.auriium.mattlib2.auto.pid;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import xyz.auriium.mattlib2.Mattlib2Exception;
import xyz.auriium.mattlib2.hardware.config.PIDComponent;
import xyz.auriium.mattlib2.loop.IMattlibHooked;
import xyz.auriium.yuukonstants.exception.ExplainedException;

public class RotationalPIDBrain implements IPIDBrain, IMattlibHooked {

    final PIDComponent component;
    final PIDController internalController = new PIDController(0,0,0);

    public RotationalPIDBrain(PIDComponent component) {
        this.component = component;

        mattRegister();
    }

    int index = 0;
    double lastReference = 0;
    double lastState = 0;

    double lastP = 0;
    double lastI = 0;
    double lastD = 0;


    @Override public void logPeriodic() {
        component.reportState(lastState);
        component.reportReference(lastReference);
        component.reportAtGoal(internalController.atSetpoint());
    }

    @Override public ExplainedException[] verifyInit() {
        internalController.setPID(component.pConstant(),component.iConstant(),component.dConstant());
        component.tolerance_pidUnits().ifPresent(internalController::setTolerance);
        internalController.enableContinuousInput(-Math.PI, Math.PI);

        return IMattlibHooked.super.verifyInit();
    }

    @Override public void tunePeriodic() {
        if (!MathUtil.isNear(component.pConstant(), lastP, 0.0001)) {
            internalController.setP(component.pConstant());
            lastP = component.pConstant();
            System.out.println("last: " + lastP + " now: " + component.pConstant());

        }

        if (!MathUtil.isNear(component.iConstant(), lastI, 0.0001)) {
            internalController.setI(component.pConstant());
            lastI = component.iConstant();
        }

        if (!MathUtil.isNear(component.dConstant(), lastD, 0.0001)) {
            internalController.setD(component.pConstant());
            lastD = component.dConstant();
        }
    }

    @Override public IPIDController spawn() {
        index++;
        internalController.reset();

        return new IPIDController() {
            final int ownIndex = index;

            @Override public double controlToReference_primeUnits(double setpoint_primeUnits, double state_primeUnits) {
                if (ownIndex != index) throw new Mattlib2Exception(
                        "nonMutexPidController",
                        "a pid controller at [" + component.selfPath().tablePath() + "] is being used twice",
                        "make your code not use the same pid controller at once"
                );

                lastReference = setpoint_primeUnits;
                lastState = state_primeUnits; //TODO do we want to wrap the state? let's wrap the state!


                if (component.tolerance_pidUnits().isPresent()) {

                }

                return internalController.calculate(state_primeUnits,setpoint_primeUnits);
            }

            @Override public boolean isAtSetpoint() {
                return internalController.atSetpoint();
            }
        };
    }
}
