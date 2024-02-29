package xyz.auriium.mattlib2.nt.suppliers;

import edu.wpi.first.networktables.NetworkTableEntry;
import xyz.auriium.mattlib2.MattlibSettings;
import xyz.auriium.mattlib2.log.InitializableConsumer;
import xyz.auriium.mattlib2.log.InitializableSupplier;

import java.util.function.Supplier;

public class IntSupplier implements InitializableSupplier<Integer> {

    final NetworkTableEntry entry;
    final int defaultValue;

    public IntSupplier(NetworkTableEntry entry, int defaultValue) {
        this.entry = entry;
        this.defaultValue = defaultValue;
    }

    public IntSupplier(NetworkTableEntry entry, Integer defaultValue) {
        this.entry = entry;
        this.defaultValue = defaultValue;
    }



    @Override public void initializeLogging() {
        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.VERBOSE_TELEMETRY)) {
            entry.setInteger(defaultValue);
        }
    }

    @Override public Integer get() {
        if (entry.getValue() == null) return defaultValue;
        return Math.toIntExact(entry.getValue().getInteger());
    }
}
