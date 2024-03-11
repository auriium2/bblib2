package xyz.auriium.mattlib2.auto.model;

/**
 * Represents something that emits data continuously
 * @param <T>
 */
public interface ISignal<T> {

    /**
     * The signal should always be able to emit data
     * @return
     */
    T emit();

}
