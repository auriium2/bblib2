package xyz.auriium.mattlib2.log;

import java.util.function.Consumer;

public interface InitializableConsumer<T> extends Consumer<T> {

    void initializeLogging();

}
