package xyz.auriium.mattlib2.nt.consumers;

import edu.wpi.first.math.Vector;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.util.datalog.DoubleArrayLogEntry;
import xyz.auriium.mattlib2.MattlibSettings;
import xyz.auriium.mattlib2.log.InitializableConsumer;

import java.util.function.Consumer;

public class Pose3Consumer implements InitializableConsumer<Pose3d> {


    final NetworkTableEntry entry;
    final DoubleArrayLogEntry logEntry;

    public Pose3Consumer(NetworkTableEntry entry, DoubleArrayLogEntry logEntry) {
        this.entry = entry;
        this.logEntry = logEntry;
    }

    @Override
    public void accept(Pose3d pose) {
        if (!MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.LOG)) return;

        Vector<N3> angle = pose.getRotation().getAxis();

        var arr = new double[] { pose.getX(), pose.getY(), pose.getZ(),angle.get(0,0), angle.get(1,0), angle.get(2,0) };
        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.VERBOSE_TELEMETRY)) {
            entry.setDoubleArray(arr);
        }
        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.LOG)) {
            logEntry.append(arr);
        }
    }


    @Override public void initializeLogging() {
        accept(new Pose3d());
    }
}
