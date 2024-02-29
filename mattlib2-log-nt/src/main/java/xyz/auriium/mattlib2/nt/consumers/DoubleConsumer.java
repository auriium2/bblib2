package xyz.auriium.mattlib2.nt.consumers;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import xyz.auriium.mattlib2.MattlibSettings;
import xyz.auriium.mattlib2.log.InitializableConsumer;

public class DoubleConsumer implements InitializableConsumer<Double> {

    final NetworkTableEntry entry;
    final DoubleLogEntry logEntry;

    public DoubleConsumer(NetworkTableEntry entry, DoubleLogEntry logEntry) {
        this.entry = entry;
        this.logEntry = logEntry;
    }


    @Override public void initializeLogging() {
        accept(0d);
    }

    @Override public void accept(Double aDouble) {
        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.VERBOSE_TELEMETRY)) {
            entry.setDouble(aDouble);
        }
        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.LOG)) {
            logEntry.append(aDouble);
        }
    }
}
