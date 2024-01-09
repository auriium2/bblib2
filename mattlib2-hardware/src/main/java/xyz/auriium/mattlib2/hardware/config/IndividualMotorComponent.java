package xyz.auriium.mattlib2.hardware.config;

import xyz.auriium.mattlib2.log.INetworkedComponent;
import xyz.auriium.mattlib2.log.Conf;
import xyz.auriium.mattlib2.log.Log;

/**
 * A config for all networked parts of a motor.
 * Since it is logging, it is individual to a motor and cannot be shared without problems.
 */
public interface IndividualMotorComponent extends INetworkedComponent {

    /**
     *
     * @return The can bus id of this device
     */
    @Conf("id")
    int id();

    /**
     *
     * @param voltage the voltage the motor is currently being supplied
     */
    @Log("voltage")
    void logVoltageGiven(double voltage);

    /**
     *
     * @param current the quantity of current the motor is currently consuming
     */
    @Log("current")
    void logCurrentDraw(double current);

}
