package xyz.auriium.mattlib2.log.nt;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import xyz.auriium.mattlib2.log.IMattLogger;
import xyz.auriium.mattlib2.log.ProcessPath;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NetworkMattLogger implements IMattLogger {


    @Override
    public <T> Optional<Supplier<T>> generateTuner(ProcessPath path, T defaultValue) {
        return Optional.ofNullable(getCorrectSupplier(path, defaultValue));
    }

    <T> Supplier<T> getCorrectSupplier(ProcessPath path, T defaultValue) {
        Class<?> returnType = defaultValue.getClass();
        NetworkTableEntry entry = NetworkTableInstance.getDefault().getEntry(path.getAsTablePath());


        if (returnType == Double.class) { //handle doubles
            Supplier<Double> supplier = new NTValueSupplier<>(entry, double.class);

            return (Supplier<T>) supplier;
        }

        if (returnType == Long.class) { //handle longs
            Supplier<Long> supplier = new NTValueSupplier<>(entry, long.class);

            return (Supplier<T>) supplier;
        }

        if (returnType == String.class) {
            Supplier<String> supplier = new NTValueSupplier<>(entry, String.class);

            return (Supplier<T>) supplier;
        }


        //TODO handle double arrays

        return null;

    }


    @Override
    public <T> Optional<Consumer<T>> generateLogger(ProcessPath path, Class<T> type) {
        if (type == Integer.class || type == int.class) {
            return Optional.of(new PathCallConsumer<>((a) -> NetworkTableInstance.getDefault().getEntry(path.getAsTablePath()).setInteger(Integer.toUnsignedLong((Integer) a))));
        }

        if (type == Double.class || type == double.class) {
            System.out.println(path.getAsTablePath());
            return Optional.of(new PathCallConsumer<>((a) -> NetworkTableInstance.getDefault().getEntry(path.getAsTablePath()).setDouble((Double) a)));
        }



        return Optional.empty();
    }

    @Override
    public void close() {

    }
}
