package xyz.auriium.mattlib2.log.nt;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Public supplier delegate so that generated classes can access the lambdas contained within this class
 * If ByteBuddy tries to bind a normal lambda to the return values of the generated delegate class
 * it will not be able to, because the lambda can sometimes be created in the body of a method and therefore be hidden
 * from ByteBuddy. However hiding the lambda inside this class will give us access to private lambdas
 * in exchange for a little overhead due to the invocation passing through here
 * @param <T>
 */
public class NTArraySupplier<T> implements Supplier<T> {

    final Function<T,T> function;
    final T alwaysUseDefault;

    public NTArraySupplier(Function<T, T> function, T alwaysUseDefault) {
        this.function = function;
        this.alwaysUseDefault = alwaysUseDefault;
    }

    @Override
    public T get() {

        return function.apply(alwaysUseDefault);
    }
}
