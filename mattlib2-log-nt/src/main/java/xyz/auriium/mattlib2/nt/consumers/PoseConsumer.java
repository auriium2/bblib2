package xyz.auriium.mattlib2.nt.consumers;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.util.datalog.DoubleArrayLogEntry;
import xyz.auriium.mattlib2.MattlibSettings;
import xyz.auriium.mattlib2.log.InitializableConsumer;

import java.util.function.Consumer;

public class PoseConsumer implements InitializableConsumer<Pose2d> {


    final NetworkTableEntry entry;
    final DoubleArrayLogEntry logEntry;
    final MattlibSettings.LogLevel threshold;

    public PoseConsumer(NetworkTableEntry entry, DoubleArrayLogEntry logEntry, MattlibSettings.LogLevel threshold) {
        this.entry = entry;
        this.logEntry = logEntry;
        this.threshold = threshold;
    }

    @Override
    public void accept(Pose2d pose2d) {
        if (!MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.LOG)) return;

        var arr = new double[] { pose2d.getX(), pose2d.getY(), pose2d.getRotation().getRadians() };
        if (MattlibSettings.USE_TELEMETRY.isAt(threshold)) {
            entry.setDoubleArray(arr);
        }
        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.LOG)) {
            logEntry.append(arr);
        }
    }


    @Override public void initializeLogging() {
        accept(new Pose2d());
    }
}
