package xyz.auriium.mattlib2.nt.consumers;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.util.datalog.IntegerLogEntry;
import xyz.auriium.mattlib2.MattlibSettings;
import xyz.auriium.mattlib2.log.InitializableConsumer;

public class LongConsumer implements InitializableConsumer<Long> {

    final NetworkTableEntry entry;
    final IntegerLogEntry logEntry;
    final MattlibSettings.LogLevel threshold;

    public LongConsumer(NetworkTableEntry entry, IntegerLogEntry logEntry, MattlibSettings.LogLevel threshold) {
        this.entry = entry;
        this.logEntry = logEntry;
        this.threshold = threshold;
    }


    @Override public void accept(Long integer) {
        if (MattlibSettings.USE_TELEMETRY.isAt(threshold)) {
            entry.setInteger(integer);
        }
        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.LOG)) {
            logEntry.append(integer);
        }
    }

    @Override public void initializeLogging() {
        accept(0L);
    }
}
