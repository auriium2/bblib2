package xyz.auriium.mattlib2.nt.suppliers;

import edu.wpi.first.networktables.NetworkTableEntry;
import xyz.auriium.mattlib2.MattlibSettings;
import xyz.auriium.mattlib2.log.InitializableSupplier;

public class BooleanSupplier implements InitializableSupplier<Boolean> {

    final NetworkTableEntry entry;
    final boolean defaultValue;

    public BooleanSupplier(NetworkTableEntry entry, boolean defaultValue) {
        this.entry = entry;
        this.defaultValue = defaultValue;
    }

    public BooleanSupplier(NetworkTableEntry entry, Boolean defaultValue) {
        this.entry = entry;
        this.defaultValue = defaultValue;
    }



    @Override public void initializeLogging() {
        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.VERBOSE_TELEMETRY)) {
            entry.setBoolean(defaultValue);
        }
    }

    @Override public Boolean get() {
        if (entry.getValue() == null) return defaultValue;
        return entry.getValue().getBoolean();
    }
}
