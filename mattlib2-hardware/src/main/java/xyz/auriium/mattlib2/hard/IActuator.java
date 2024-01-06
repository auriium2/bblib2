package xyz.auriium.mattlib2.hard;

import edu.wpi.first.math.Vector;
import edu.wpi.first.math.numbers.N1;

import javax.measure.Quantity;
import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.ElectricPotential;
import javax.measure.quantity.Volume;

/**
 * Represents something that can be driven
 */
public interface IActuator extends IControlEffortReceiver<N1> {

    /**
     * @param voltage The voltage that the actuator should be driven at, typically ranged -12 to 12 but not always
     */
    void setToVoltage(double voltage);

    /**
     * @param percent_zeroToOne The percent that
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

    @Override
    default void handleControlEffort(Vector<N1> inputVector_u) {
        setToVoltage(inputVector_u.get(0,0));
    }
}
