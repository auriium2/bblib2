package xyz.auriium.mattlib2.nt.consumers;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.util.datalog.IntegerLogEntry;
import edu.wpi.first.util.datalog.StringLogEntry;
import xyz.auriium.mattlib2.MattlibSettings;
import xyz.auriium.mattlib2.log.InitializableConsumer;

public class StringConsumer implements InitializableConsumer<String> {

    final NetworkTableEntry entry;
    final StringLogEntry logEntry;

    public StringConsumer(NetworkTableEntry entry, StringLogEntry logEntry) {
        this.entry = entry;
        this.logEntry = logEntry;
    }

    @Override public void initializeLogging() {
        accept("");
    }

    @Override public void accept(String s) {
        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.VERBOSE_TELEMETRY)) {
            entry.setString(s);
        }
        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.LOG)) {
            logEntry.append(s);
        }
    }
}
