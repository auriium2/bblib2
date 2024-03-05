package xyz.auriium.mattlib2.nt.consumers;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.util.datalog.BooleanArrayLogEntry;
import edu.wpi.first.util.datalog.DoubleArrayLogEntry;
import xyz.auriium.mattlib2.MattlibSettings;
import xyz.auriium.mattlib2.log.InitializableConsumer;

import java.util.function.Consumer;

public class BooleanArrayConsumer implements InitializableConsumer<boolean[]> {


    final NetworkTableEntry entry;
    final BooleanArrayLogEntry logEntry;
    final MattlibSettings.LogLevel threshold;

    public BooleanArrayConsumer(NetworkTableEntry entry, BooleanArrayLogEntry logEntry, MattlibSettings.LogLevel threshold) {
        this.entry = entry;
        this.logEntry = logEntry;
        this.threshold = threshold;
    }

    @Override public void initializeLogging() {
        accept(new boolean[0]);
    }

    @Override public void accept(boolean[] aLong) {
        if (!MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.LOG)) return;


        if (MattlibSettings.USE_TELEMETRY.isAt(threshold)) {
            entry.setBooleanArray(aLong);
        }
        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.LOG)) {
            logEntry.append(aLong);
        }
    }
}
