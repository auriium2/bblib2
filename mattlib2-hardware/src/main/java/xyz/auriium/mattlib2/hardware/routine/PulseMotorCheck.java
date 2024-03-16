package xyz.auriium.mattlib2.hardware.routine;

import xyz.auriium.mattlib2.hardware.IActuator;
import xyz.auriium.mattlib2.loop.Outcome;
import xyz.auriium.mattlib2.loop.check.ICheckRoutine;

/**
 * This check verifies that a motor can be pulsed to a high voltage and move at all as a result
 * <br><br>
 * if: <br>
 *   - a voltage is applied to the motor for
 */
public class PulseMotorCheck implements ICheckRoutine<Void> {


    @Override public Outcome<Void> runLogic(Orders orders, Void whiteboard) {
        return null;
    }
}
