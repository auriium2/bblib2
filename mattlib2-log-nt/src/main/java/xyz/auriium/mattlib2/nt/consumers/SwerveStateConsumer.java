package xyz.auriium.mattlib2.nt.consumers;

import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.util.datalog.DoubleArrayLogEntry;
import xyz.auriium.mattlib2.MattlibSettings;
import xyz.auriium.mattlib2.log.InitializableConsumer;

import java.util.function.Consumer;

public class SwerveStateConsumer implements InitializableConsumer<SwerveModuleState[]> {

    final NetworkTableEntry entry;
    final DoubleArrayLogEntry logEntry;
    final MattlibSettings.LogLevel threshold;

    public SwerveStateConsumer(NetworkTableEntry entry, DoubleArrayLogEntry logEntry, MattlibSettings.LogLevel threshold) {
        this.entry = entry;
        this.logEntry = logEntry;
        this.threshold = threshold;
    }

    @Override
    public void accept(SwerveModuleState[] swerveModulePosition) {
        if (!MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.LOG)) return;

        double[] data = new double[swerveModulePosition.length * 2];

        for (int i = 0; i < swerveModulePosition.length; i++) {
            data[i * 2] = swerveModulePosition[i].angle.getRadians();
            data[i * 2 + 1] = swerveModulePosition[i].speedMetersPerSecond;
        }

        if (MattlibSettings.USE_TELEMETRY.isAt(threshold)) {
            entry.setDoubleArray(data);
        }
        if (MattlibSettings.USE_TELEMETRY.isAt(MattlibSettings.LogLevel.LOG)) {
            logEntry.append(data);
        }
    }

    @Override public void initializeLogging() {
        accept(new SwerveModuleState[0]);
    }
}
