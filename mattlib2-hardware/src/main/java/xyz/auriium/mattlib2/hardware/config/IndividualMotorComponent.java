package xyz.auriium.mattlib2.hardware.config;

import xyz.auriium.mattlib2.hardware.OperationMode;
import xyz.auriium.mattlib2.log.INetworkedComponent;
import xyz.auriium.mattlib2.log.annote.Conf;
import xyz.auriium.mattlib2.log.annote.Log;

/**
 * A config for all networked parts of a motor.
 * Since it is logging, it is individual to a motor and cannot be shared without problems.
 */
public interface IndividualMotorComponent extends INetworkedComponent {

    /**
     *
     * @return The can bus id of this device
     */
    @Conf("id") int id();

    /**
     *
     * @param voltage the voltage the motor is currently being supplied
     */
    @Log("voltage") void reportVoltageGiven(double voltage);

    /**
     *
     * @param current the quantity of current the motor is currently consuming
     */
    @Log("current") void reportCurrentDraw(double current);

    @Log("temperature_celsius") void reportTemperature(double temperatureCelsius);
    //@Log("operation_mode") void reportOpMode(OperationMode opMode);

    @Log("position_mechanismRotations") void reportMechanismRotations(double mechanismRotations);
    @Log("velocity_mechanismRotationsPerSecond") void reportMechanismVelocity(double mechanismRotationsPerSecond);

    @Log("forwardLimit_triggered") void reportFwLimitTriggered(boolean triggered);
    @Log("reverseLimit_triggered") void reportRvLimitTriggered(boolean triggered);


}
