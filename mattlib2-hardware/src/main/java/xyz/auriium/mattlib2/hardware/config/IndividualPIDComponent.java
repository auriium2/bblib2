package xyz.auriium.mattlib2.hardware.config;

import xyz.auriium.mattlib2.log.INetworkedComponent;
import xyz.auriium.mattlib2.log.annote.Log;

/**
 * Often very specific details of a pid controller
 */
public interface IndividualPIDComponent extends INetworkedComponent {

    @Log("pidReference") void reportReference(double ref);
    @Log("pidError") void reportError(double error);
    @Log("pidOutput") void reportOutput(double output);

}
