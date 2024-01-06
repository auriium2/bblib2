package xyz.auriium.mattlib2.log;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IMattLogger extends Closeable {

    default void init() {};
    default void ready() {};

    /**
     *
     * @param path
     * @param defaultValue
     * @return TODO IMPORTANT YOU CANNOT USE LAMBDAS HERE BECAUSE OF HOW THE BYTECODE HACKERY WORKS
     * @param <T>
     */
    <T> Optional<Supplier<T>> generateTuner(ProcessPath path, T defaultValue);


    /**
     *
     * @param path
     * @param type
     * @return TODO IMPORTANT YOU CANNOT USE LAMBDAS HERE BECAUSE OF HOW THE BYTECODE HACKERY WORKS
     * @param <T>
     */
    <T> Optional<Consumer<T>> generateLogger(ProcessPath path, Class<T> type);




}
