package xyz.auriium.mattlib2.nt.suppliers;

import edu.wpi.first.networktables.NetworkTableEntry;

import java.util.function.Supplier;

/**
 * Public supplier delegate so that generated classes can access the lambdas contained within this class
 * If ByteBuddy tries to bind a normal lambda to the return values of the generated delegate class
 * it will not be able to, because the lambda can sometimes be created in the body of a method and therefore be hidden
 * from ByteBuddy. However hiding the lambda inside this class will give us access to private lambdas
 * in exchange for a little overhead due to the invocation passing through here
 * @param <T>
 */
public class NTValueSupplier<T> implements Supplier<T> {

    final NetworkTableEntry entry;
    final Class<T> castTo;
    final T useIfNot;

    public NTValueSupplier(NetworkTableEntry entry, Class<T> castTo, T useIfNot) {
        this.entry = entry;
        this.castTo = castTo;
        this.useIfNot = useIfNot;
    }

    @Override
    public T get() {
        var value1 = entry.getValue();
        if (value1 == null) {
            return useIfNot;
        }
        var value2 = value1.getValue();
        if (value2 == null) {
            return useIfNot;
        }

        return castTo.cast(value2);
    }
}
