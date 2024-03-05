package xyz.auriium.mattlib2.nt.consumers;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.util.datalog.DoubleArrayLogEntry;
import xyz.auriium.mattlib2.MattlibSettings;
import xyz.auriium.mattlib2.log.InitializableConsumer;

import java.util.function.Consumer;

public class TranslationConsumer implements InitializableConsumer<Translation2d> {

    final NetworkTableEntry entry;
    final DoubleArrayLogEntry logEntry;
    final MattlibSettings.LogLevel threshold;

    public TranslationConsumer(NetworkTableEntry entry, DoubleArrayLogEntry logEntry, MattlibSettings.LogLevel threshold) {
        this.entry = entry;
        this.logEntry = logEntry;
        this.threshold = threshold;
    }

    @Override
    public void accept(Translation2d translation2d) {
        if (!MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.LOG)) return;

        var data = new double[] { translation2d.getX(), translation2d.getY() };
        if (MattlibSettings.USE_TELEMETRY.isAt(threshold)) {
            entry.setDoubleArray(data);
        }
        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.LOG)) {
            logEntry.append(data);
        }
    }

    @Override public void initializeLogging() {
        accept(new Translation2d());
    }
}
