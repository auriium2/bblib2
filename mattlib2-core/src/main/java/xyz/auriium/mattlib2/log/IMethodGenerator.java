package xyz.auriium.mattlib2.log;

import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.Implementation;
import xyz.auriium.yuukonstants.GenericPath;
import xyz.auriium.yuukonstants.exception.ExplainedException;

import java.util.Optional;
import java.util.function.Function;

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

    record Output(Implementation[] implementations, Function<DynamicType.Builder<Object>, DynamicType.Builder<Object>> applyToBuilder) {}


    Output generateLog(int alphabeticIndexPerLog, GenericPath fullPath, Class<?> logType);
    Output generateTune(int alphabeticIndexPerTune, GenericPath fullPath, Class<?> logType, Object datum);
    Output generateConf(int alphabeticIndexPerTune, GenericPath fullPath, Class<?> logType, Object datum);

    Optional<ExplainedException> filterLog(Class<?> logType);
    Optional<ExplainedException> filterTune(Class<?> tuneType);

    static boolean supportsUpdate(Implementation[] implementations) {
        return implementations.length > 1;
    }
}
