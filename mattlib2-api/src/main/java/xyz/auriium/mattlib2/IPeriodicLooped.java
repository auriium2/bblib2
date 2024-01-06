package xyz.auriium.mattlib2;

import java.util.Optional;

/**
 * If something has this interface, you better make sure that it is registered in {@link MattLoopManager}
 * or the functions on it are otherwise called when they should be
 *
 * This interface defines that something has looping requirements in order to function
 */
public interface IPeriodicLooped {

    /**
     * This function should be run in robotInit of the robot.
     * It is intended to run initialization code that cannot fail
     * For stuff that can fail, put it in {@link #verifyInit()}
     */
    default void init() {

    }

    /**
     * This fn should be called after {@link #init()}, probably in robotInit
     * It is intended to run ONCE after init. It's body should make sure the class it is attached to is running ok
     * If not, it should return an exception (not throw it)
     */
    default Optional<Exception> verifyInit() {
        return Optional.empty();
    }

    /**
     * This function should be run in robot periodic
     * This function should also BE RUN BEFORE ALL USER ROBOT CODE  / SUBSYSTEM LOOPS
     */
    default void robotPeriodic() {

    }

    /**
     * This function should be run in robot periodic, optionally
     */
    default void logPeriodic() {};

    /**
     * This function should be run in robot periodic when tuning is enabled
     */
    default void tunePeriodic() {};

}
