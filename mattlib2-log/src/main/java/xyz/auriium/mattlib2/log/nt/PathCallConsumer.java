package xyz.auriium.mattlib2.log.nt;

import java.util.function.Consumer;

/**
 * See {@link NTValueSupplier}
 * @param <T>
 */
public class PathCallConsumer<T> implements Consumer<T> {
    final Consumer<T> delegate;

    public PathCallConsumer(Consumer<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void accept(T t) {
        delegate.accept(t);
    }
}
