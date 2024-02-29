package xyz.auriium.mattlib2.nt.consumers;

import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.util.datalog.DoubleArrayLogEntry;
import xyz.auriium.mattlib2.MattlibSettings;
import xyz.auriium.mattlib2.log.InitializableConsumer;

import java.util.function.Consumer;

public class SwervePosConsumer implements InitializableConsumer<SwerveModulePosition[]> {

    final NetworkTableEntry entry;
    final DoubleArrayLogEntry logEntry;

    public SwervePosConsumer(NetworkTableEntry entry, DoubleArrayLogEntry logEntry) {
        this.entry = entry;
        this.logEntry = logEntry;
    }

    @Override
    public void accept(SwerveModulePosition[] swerveModulePosition) {
        if (!MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.LOG)) return;

        double[] data = new double[swerveModulePosition.length * 2];

        for (int i = 0; i < swerveModulePosition.length; i++) {
            data[i * 2] = swerveModulePosition[i].angle.getRadians();
            data[i * 2 + 1] = swerveModulePosition[i].distanceMeters;
        }

        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.VERBOSE_TELEMETRY)) {
            entry.setDoubleArray(data);
        }
        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.LOG)) {
            logEntry.append(data);
        }
    }

    @Override public void initializeLogging() {
        accept(new SwerveModulePosition[0]);
    }
}
