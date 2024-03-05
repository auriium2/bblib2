package xyz.auriium.mattlib2.nt;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.util.datalog.DataLog;
import xyz.auriium.mattlib2.log.InitializableConsumer;
import xyz.auriium.mattlib2.log.InitializableSupplier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SupplierMap {

    public interface SupplierSupplier<T> {
        InitializableSupplier<T> make(NetworkTableEntry e, T defaultData);
    }

    final Map<Class<?>, SupplierSupplier<?>> backingMap = new HashMap<>();

    public <T> void register(Class<T> clazz, SupplierSupplier<T> consoomer) {
        backingMap.put(clazz, consoomer);
    }

    public <T> void registerTwo(Class<T> clazz, Class<?> primitive, SupplierSupplier<T> consoomer) {
        backingMap.put(clazz, consoomer);
        backingMap.put(primitive, consoomer);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<SupplierSupplier<T>> of(Class<T> clazz) {
        if (!backingMap.containsKey(clazz)) return Optional.empty();

        return Optional.of((SupplierSupplier<T>) backingMap.get(clazz));
    }

}
