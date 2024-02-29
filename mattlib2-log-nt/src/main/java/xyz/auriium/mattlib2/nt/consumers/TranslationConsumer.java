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

    public TranslationConsumer(NetworkTableEntry entry, DoubleArrayLogEntry logEntry) {
        this.entry = entry;
        this.logEntry = logEntry;
    }

    @Override
    public void accept(Translation2d translation2d) {
        if (!MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.LOG)) return;

        var data = new double[] { translation2d.getX(), translation2d.getY() };
        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.VERBOSE_TELEMETRY)) {
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
