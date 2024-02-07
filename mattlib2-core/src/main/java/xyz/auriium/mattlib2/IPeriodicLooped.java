package xyz.auriium.mattlib2;


import xyz.auriium.yuukonstants.exception.ExplainedException;

import java.util.Optional;

/**
 * If something has this interface, you better make sure that it is registered in {@link MattlibLooper}
 * or the functions on it are otherwise called when they should be
 *
 * This interface defines that something has looping requirements in order to function
 */
public interface IPeriodicLooped {


    /**
     * This function should be called before mattlog is registered
     */
    default void preInit() {

    }

    /**
     * Registers the PeriodicLoop. This must be called.
     */
    default void mattRegister() { //I truly hate this, but WPI does it to simplify things and so will I
        Mattlib.LOOPER.register(this);
    }



    /**
     * This fn should be called probably in robotInit
     * It is intended to run ONCE after init. It's body should make sure the class it is attached to is running ok
     * If not, it should return an exception (not throw it)
     */
    default Optional<ExplainedException> verifyInit() {
        return Optional.empty();
    }


    //Stop gap
    default void verify2Init() {

    }

    /**
     * This function should be run in robot periodic
     * It is intended to be called to run operational logic that must happen periodically in order for the component to function
     * This function should also BE RUN BEFORE ALL USER ROBOT CODE  / SUBSYSTEM LOOPS
     */
    default void logicPeriodic() {

    }

    /**
     * This function should be run in robot periodic, optionally
     */
    default void logPeriodic() {};

    /**
     * This function should be run in robot periodic when tuning is enabled
     */
    default void tunePeriodic() {};

    /**
     * This function is run every time the robot is shutdown
     */
    default void shutdownHook() {}

}
