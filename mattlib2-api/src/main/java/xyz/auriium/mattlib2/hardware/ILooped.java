package xyz.auriium.mattlib2.hardware;

/**
 * Represents a device that needs a loop to function
 */
public interface ILooped {

    /**
     * This should run in robotPeriodic
     */
    void loop();

}
