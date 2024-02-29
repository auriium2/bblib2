package xyz.auriium.mattlib2.log;

import java.util.function.Supplier;

public interface InitializableSupplier<T> extends Supplier<T> {

    void initializeLogging();

}
