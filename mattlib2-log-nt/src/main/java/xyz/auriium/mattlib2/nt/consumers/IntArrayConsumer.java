package xyz.auriium.mattlib2.nt.consumers;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.util.datalog.IntegerArrayLogEntry;
import xyz.auriium.mattlib2.MattlibSettings;
import xyz.auriium.mattlib2.log.InitializableConsumer;

import java.util.function.Consumer;

public class IntArrayConsumer implements InitializableConsumer<int[]> {

    final NetworkTableEntry entry;
    final IntegerArrayLogEntry logEntry;

    public IntArrayConsumer(NetworkTableEntry entry, IntegerArrayLogEntry logEntry) {
        this.entry = entry;
        this.logEntry = logEntry;
    }

    @Override public void initializeLogging() {
        accept(new int[0]);
    }

    @Override public void accept(int[] aLong) {
        if (!MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.LOG)) return;

        long[] longArray = new long[aLong.length];
        for(int i = 0; i < aLong.length; i++) longArray[i] = aLong[i];

        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.VERBOSE_TELEMETRY)) {
            entry.setIntegerArray(longArray);
        }
        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.LOG)) {
            logEntry.append(longArray);
        }
    }
}
