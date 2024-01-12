package xyz.auriium.mattlib2.log;

import net.bytebuddy.implementation.Implementation;
import yuukonstants.GenericPath;
import yuukonstants.exception.ExplainedException;

import java.util.Optional;

/**
 * One of these will be created every time the log component manipulator spins up
 */
public interface IMethodGenerator {


    /**
     * This ID of the implementation array's return is used directly to implement that method
     */
    byte IDX_FOR_METHOD = 0;

    /**
     * This ID of the implementation array's return is used to implement the update function
     */
    byte IDX_FOR_UPDATE = 1;


    Implementation[] generateLog(int alphabeticIndexPerLog, GenericPath fullPath, Class<?> logType) throws NoSuchMethodException;
    Implementation[] generateTune(int alphabeticIndexPerLog, GenericPath f);


    Optional<ExplainedException> filterLog(Class<?> logType);
    Optional<ExplainedException> filterTune(Class<?> tuneType);
}
