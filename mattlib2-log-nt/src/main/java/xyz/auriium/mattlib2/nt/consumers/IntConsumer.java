package xyz.auriium.mattlib2.nt.consumers;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.util.datalog.IntegerLogEntry;
import xyz.auriium.mattlib2.MattlibSettings;
import xyz.auriium.mattlib2.log.InitializableConsumer;

public class IntConsumer implements InitializableConsumer<Integer> {

    final NetworkTableEntry entry;
    final IntegerLogEntry logEntry;

    public IntConsumer(NetworkTableEntry entry, IntegerLogEntry logEntry) {
        this.entry = entry;
        this.logEntry = logEntry;
    }


    @Override public void accept(Integer integer) {
        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.VERBOSE_TELEMETRY)) {
            entry.setInteger(integer);
        }
        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.LOG)) {
            logEntry.append(integer);
        }

    }

    @Override public void initializeLogging() {
        accept(0);
    }
}
