package xyz.auriium.mattlib2.log;

import java.util.function.Supplier;

/**
 * Supplier that always returns the same thing
 */
public class FixedSupplier<T> implements Supplier<T> {

    final T data;

    public FixedSupplier(T data) {
        this.data = data;
    }

    @Override public T get() {
        return data;
    }
}
