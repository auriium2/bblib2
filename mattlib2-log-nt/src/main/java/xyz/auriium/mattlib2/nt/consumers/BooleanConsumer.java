package xyz.auriium.mattlib2.nt.consumers;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.util.datalog.BooleanLogEntry;
import edu.wpi.first.util.datalog.StringLogEntry;
import xyz.auriium.mattlib2.MattlibSettings;
import xyz.auriium.mattlib2.log.InitializableConsumer;

public class BooleanConsumer implements InitializableConsumer<Boolean> {

    final NetworkTableEntry entry;
    final BooleanLogEntry logEntry;

    public BooleanConsumer(NetworkTableEntry entry, BooleanLogEntry logEntry) {
        this.entry = entry;
        this.logEntry = logEntry;
    }

    @Override public void initializeLogging() {
        accept(false);
    }

    @Override
    public void accept(Boolean s) {
        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.VERBOSE_TELEMETRY)) {
            entry.setBoolean(s);
        }
        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.LOG)) {
            logEntry.append(s);
        }
    }
}
