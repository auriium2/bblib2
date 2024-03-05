package xyz.auriium.mattlib2.loop;


import xyz.auriium.mattlib2.Mattlib;
import xyz.auriium.mattlib2.MattlibLooper;
import xyz.auriium.mattlib2.loop.simple.ISimpleSubroutine;
import xyz.auriium.yuukonstants.exception.ExplainedException;

import java.util.Optional;

/**
 * If something has this interface, you better make sure that it is registered in {@link MattlibLooper}
 * or the functions on it are otherwise called when they should be
 *
 * This interface defines that something has looping requirements in order to function
 */
public interface IMattlibHooked {

    /**
     * Registers the PeriodicLoop. This must be called.
     */
    default void mattRegister() { //I truly hate this, but WPI does it to simplify things and so will I
        Mattlib.LOOPER.register(this);
    }

    enum RoutineContext {
        PRE_BOOT,
        CORE_BOOT,
        VERIFY_BOOT,
    }



    /**
     * This function should be called before mattlog is registered
     */
    default void preInit() {

    }


    /**
     * This fn should be called probably in robotInit
     * It is intended to run ONCE after init. It's body should make sure the class it is attached to is running ok
     * If not, it should return an exception (not throw it)
     */
    default ExplainedException[] verifyInit() {
        return new ExplainedException[0];
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

    default void postLogicPeriodic() {}

    /**
     * This function will be logged even when logging is disabled
     */
    default void alwaysLogPeriodic() {}

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
