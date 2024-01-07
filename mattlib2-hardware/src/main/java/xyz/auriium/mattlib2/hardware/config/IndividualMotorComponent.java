package xyz.auriium.mattlib2.hardware.config;

import xyz.auriium.mattlib2.log.INetworkedComponent;
import xyz.auriium.mattlib2.log.annotation.Conf;
import xyz.auriium.mattlib2.log.annotation.Log;

/**
 * A config for all networked parts of a motor.
 * Since it is logging, it is individual to a motor and cannot be shared without problems.
 */
public interface IndividualMotorComponent extends INetworkedComponent {

    @Conf
    int canId();

    @Log
    void logVoltageGiven(double voltage);

    @Log
    void logCurrentDraw(double current);

}
