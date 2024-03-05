package xyz.auriium.mattlib2.nt.consumers;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.util.datalog.DoubleArrayLogEntry;
import edu.wpi.first.util.datalog.IntegerArrayLogEntry;
import xyz.auriium.mattlib2.MattlibSettings;
import xyz.auriium.mattlib2.log.InitializableConsumer;

import java.util.function.Consumer;

public class DoubleArrayConsumer implements InitializableConsumer<double[]> {

    final NetworkTableEntry entry;
    final DoubleArrayLogEntry logEntry;
    final MattlibSettings.LogLevel threshold;

    public DoubleArrayConsumer(NetworkTableEntry entry, DoubleArrayLogEntry logEntry, MattlibSettings.LogLevel threshold) {
        this.entry = entry;
        this.logEntry = logEntry;
        this.threshold = threshold;
    }

    @Override public void initializeLogging() {
        accept(new double[0]);
    }

    @Override public void accept(double[] aLong) {
        if (!MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.LOG)) return;


        if (MattlibSettings.USE_TELEMETRY.isAt(threshold)) {
            entry.setDoubleArray(aLong);
        }
        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.LOG)) {
            logEntry.append(aLong);
        }
    }
}
