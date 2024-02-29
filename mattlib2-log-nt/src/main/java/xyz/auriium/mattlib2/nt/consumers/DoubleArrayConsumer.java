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

    public DoubleArrayConsumer(NetworkTableEntry entry, DoubleArrayLogEntry logEntry) {
        this.entry = entry;
        this.logEntry = logEntry;
    }

    @Override public void initializeLogging() {
        accept(new double[0]);
    }

    @Override public void accept(double[] aLong) {
        if (!MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.LOG)) return;


        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.VERBOSE_TELEMETRY)) {
            entry.setDoubleArray(aLong);
        }
        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.LOG)) {
            logEntry.append(aLong);
        }
    }
}
