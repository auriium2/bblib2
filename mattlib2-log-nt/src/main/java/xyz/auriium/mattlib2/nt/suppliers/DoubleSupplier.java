package xyz.auriium.mattlib2.nt.suppliers;

import edu.wpi.first.networktables.NetworkTableEntry;
import xyz.auriium.mattlib2.MattlibSettings;
import xyz.auriium.mattlib2.log.InitializableSupplier;

public class DoubleSupplier implements InitializableSupplier<Double> {

    final NetworkTableEntry entry;
    final double defaultValue;

    public DoubleSupplier(NetworkTableEntry entry, double defaultValue) {
        this.entry = entry;
        this.defaultValue = defaultValue;
    }

    public DoubleSupplier(NetworkTableEntry entry, Double defaultValue) {
        this.entry = entry;
        this.defaultValue = defaultValue;
    }



    @Override public void initializeLogging() {
        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.VERBOSE_TELEMETRY)) {
            entry.setDouble(defaultValue);
        }
    }

    @Override public Double get() {
        if (entry.getValue() == null) return defaultValue;
        return entry.getValue().getDouble();
    }
}
