package xyz.auriium.mattlib2.nt;

import edu.wpi.first.networktables.NetworkTableInstance;
import xyz.auriium.mattlib2.ILogFeature;
import xyz.auriium.mattlib2.ProcessPath;

import java.util.Optional;
import java.util.function.Consumer;

public class NetworkLogFeature implements ILogFeature {
    @Override
    public <T> Optional<Consumer<T>> generateLogger(ProcessPath path, Class<T> type) {
        if (type == Integer.class || type == int.class) {
            return Optional.of(new PathCallConsumer<>((a) -> {
                NetworkTableInstance.getDefault().getEntry(path.getAsTablePath()).setInteger(Integer.toUnsignedLong((Integer) a));
            }));
        }

        if (type == Double.class || type == double.class) {
            System.out.println(path.getAsTablePath());
            return Optional.of(new PathCallConsumer<>((a) -> NetworkTableInstance.getDefault().getEntry(path.getAsTablePath()).setDouble((Double) a)));
        }




        return Optional.empty();
    }
}
