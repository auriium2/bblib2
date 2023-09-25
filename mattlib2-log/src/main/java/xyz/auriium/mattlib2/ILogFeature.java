package xyz.auriium.mattlib2;

import java.util.Optional;
import java.util.function.Consumer;

public interface ILogFeature {

    default void init() {};

    /**
     *
     * @param path
     * @param type
     * @return TODO IMPORTANT YOU CANNOT USE LAMBDAS HERE BECAUSE OF HOW THE BYTECODE HACKERY WORKS
     * @param <T>
     */
    <T> Optional<Consumer<T>> generateLogger(ProcessPath path, Class<T> type);
    default void ready() {};
    default void stop() {};

}
