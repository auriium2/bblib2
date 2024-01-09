package xyz.auriium.mattlib2.hardware.config;

import xyz.auriium.mattlib2.log.INetworkedComponent;
import xyz.auriium.mattlib2.log.annotation.Log;

/**
 * Often very specific details of a pid controller
 */
public interface IndividualPIDComponent extends INetworkedComponent {

    @Log("pid_error")
    void reportError(double error);

    @Log("pid_output")
    void reportOutput(double output);

}
