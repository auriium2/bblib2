package xyz.auriium.mattlib2.hardware;

import edu.wpi.first.math.Vector;
import edu.wpi.first.math.numbers.N1;
import xyz.auriium.mattlib2.IRaw;

/**
 * Represents something that can be driven
 */
public interface IActuator extends IControlEffortReceiver<N1>, IRaw {

    /**
     * @param voltage The voltage that the actuator should be driven at, ranged -12 to 12.
     *                - Voltages outside of that range will be rejected loudly
     *                - If this motor is inverted, the voltage you give it will be internally inverted as well
     */
    void setToVoltage(double voltage);

    /**
     * @param percent_zeroToOne A percentage to drive the actuator at, ranged from 0 to 1.
     *                          - Percentages outside of that range will be rejected loudly
     *                          - If this motor is inverted, the movement of the motor will be internally inverted as well
     */
    void setToPercent(double percent_zeroToOne);

    /**
     *
     * @return amps
     */
    double reportCurrentNow_amps();

    /**
     *
     * @return volts
     */
    double reportVoltageNow();

    double reportTemperatureNow();

    @Override
    default void handleControlEffort(Vector<N1> inputVector_u) {
        setToVoltage(inputVector_u.get(0,0));
    }
}
