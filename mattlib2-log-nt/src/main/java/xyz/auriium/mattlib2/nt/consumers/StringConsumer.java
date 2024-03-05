package xyz.auriium.mattlib2.nt.consumers;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.util.datalog.IntegerLogEntry;
import edu.wpi.first.util.datalog.StringLogEntry;
import xyz.auriium.mattlib2.MattlibSettings;
import xyz.auriium.mattlib2.log.InitializableConsumer;

public class StringConsumer implements InitializableConsumer<String> {

    final NetworkTableEntry entry;
    final StringLogEntry logEntry;
    final MattlibSettings.LogLevel threshold;


    public StringConsumer(NetworkTableEntry entry, StringLogEntry logEntry, MattlibSettings.LogLevel threshold) {
        this.entry = entry;
        this.logEntry = logEntry;
        this.threshold = threshold;
    }

    @Override public void initializeLogging() {
        accept("");
    }

    @Override public void accept(String s) {
        if (MattlibSettings.USE_TELEMETRY.isAt(threshold)) {
            entry.setString(s);
        }
        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.LOG)) {
            logEntry.append(s);
        }
    }
}
