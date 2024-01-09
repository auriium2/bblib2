package xyz.auriium.mattlib2.nt.consumers;

import edu.wpi.first.networktables.NetworkTableEntry;
import xyz.auriium.mattlib2.nt.suppliers.NTValueSupplier;

import java.util.function.Consumer;

/**
 * See {@link NTValueSupplier}
 * @param <T>
 */
public class NetworkTableConsumer<T> implements Consumer<T> {
    final NetworkTableEntry entry;

    public NetworkTableConsumer(NetworkTableEntry entry) {
        this.entry = entry;
    }

    @Override
    public void accept(T t) {
        entry.setValue(t);
    }
}
