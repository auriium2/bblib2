package xyz.auriium.mattlib2;

import java.util.Optional;
import java.util.function.Supplier;

public interface ITuneFeature {

    default void init() {};

    /**
     *
     * @param path
     * @param defaultValue
     * @return TODO IMPORTANT YOU CANNOT USE LAMBDAS HERE BECAUSE OF HOW THE BYTECODE HACKERY WORKS
     * @param <T>
     */
    <T> Optional<Supplier<T>> generateTuner(ProcessPath path, T defaultValue);

}
