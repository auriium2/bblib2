package xyz.auriium.mattlib2.log;

/**
 * This interface is automatically added to all Mattlib2 generated components
 */
public interface IMustBeUpdated {

    /**
     * Auto-implemented classes use this function to push data to their backend. Make sure Mattlib2 calls this!
     * DO NOT let other classes outside mattlib2 call this
     */
    void pushDataToBackend();


}
