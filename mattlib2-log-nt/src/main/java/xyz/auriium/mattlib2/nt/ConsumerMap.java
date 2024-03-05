package xyz.auriium.mattlib2.nt;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.util.datalog.DataLog;
import xyz.auriium.mattlib2.MattlibSettings;
import xyz.auriium.mattlib2.log.InitializableConsumer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class ConsumerMap {

    public interface ConsumerSupplier<T> {
        InitializableConsumer<T> make(NetworkTableEntry e, DataLog log, MattlibSettings.LogLevel threshold);
    }

    final Map<Class<?>, ConsumerSupplier<?>> backingMap = new HashMap<>();

    public <T> void register(Class<T> clazz, ConsumerSupplier<T> consoomer) {
        backingMap.put(clazz, consoomer);
    }

    public <T> void registerTwo(Class<T> clazz, Class<?> primitive, ConsumerSupplier<T> consoomer) {
        backingMap.put(clazz, consoomer);
        backingMap.put(primitive, consoomer);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<ConsumerSupplier<T>> of(Class<T> clazz) {
        //System.out.println("class: " + clazz + " and present?; " + backingMap.containsKey(clazz));
        if (!backingMap.containsKey(clazz)) return Optional.empty();

        return Optional.of((ConsumerSupplier<T>) backingMap.get(clazz));
    }

}
