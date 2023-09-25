package xyz.auriium.mattlib2.nt;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import xyz.auriium.mattlib2.ITuneFeature;
import xyz.auriium.mattlib2.ProcessPath;

import java.util.Optional;
import java.util.function.Supplier;

public class NetworkTuneFeature implements ITuneFeature {


    //TODO this is hacky and dumb

    @Override
    public <T> Optional<Supplier<T>> generateTuner(ProcessPath path, T defaultValue) {
        return Optional.ofNullable(getCorrectSupplier(path, defaultValue));
    }

    <T> Supplier<T> getCorrectSupplier(ProcessPath path, T defaultValue) {
        Class<?> returnType = defaultValue.getClass();
        NetworkTableEntry entry = NetworkTableInstance.getDefault().getEntry(path.getAsTablePath());

        if (returnType == Double.class) { //handle doubles
            Supplier<Double> supplier = new InvocationSupplier<>(() -> entry.getDouble((Double) defaultValue));

            return (Supplier<T>) supplier;
        }

        if (returnType == Long.class) { //handle longs
            Supplier<Long> supplier = new InvocationSupplier<>(() -> entry.getInteger((Long) defaultValue));

            return (Supplier<T>) supplier;
        }

        if (returnType == String.class) {
            Supplier<String> supplier = new InvocationSupplier<>(() -> entry.getString((String) defaultValue));

            return (Supplier<T>) supplier;
        }


        //TODO handle double arrays

        return null;

    }
}
