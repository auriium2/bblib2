package xyz.auriium.mattlib2.nt.consumers;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.util.datalog.IntegerArrayLogEntry;
import edu.wpi.first.util.datalog.IntegerLogEntry;
import xyz.auriium.mattlib2.MattlibSettings;
import xyz.auriium.mattlib2.log.InitializableConsumer;

public class LongArrayConsumer implements InitializableConsumer<long[]> {

    final NetworkTableEntry entry;
    final IntegerArrayLogEntry logEntry;
    final MattlibSettings.LogLevel threshold;

    public LongArrayConsumer(NetworkTableEntry entry, IntegerArrayLogEntry logEntry, MattlibSettings.LogLevel threshold) {
        this.entry = entry;
        this.logEntry = logEntry;
        this.threshold = threshold;
    }

    @Override public void initializeLogging() {
        accept(new long[0]);
    }

    @Override public void accept(long[] aLong) {
        if (!MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.LOG)) return;

        if (MattlibSettings.USE_TELEMETRY.isAt(threshold)) {
            entry.setIntegerArray(aLong);
        }
        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.LOG)) {
            logEntry.append(aLong);
        }
    }
}
