package xyz.auriium.mattlib2.hardware;


import edu.wpi.first.math.Num;
import edu.wpi.first.math.Vector;

/**
 * Represents something that can receive a control effort
 * @param <N> the size of the control effort vector u
 */
public interface IControlEffortReceiver<N extends Num> {

    /**
     *
     * @param inputVector_u the native input vector of the system described by this receiver
     */
    void handleControlEffort(Vector<N> inputVector_u);

}
