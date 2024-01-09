package xyz.auriium.mattlib2.nt.consumers;

import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.NetworkTableEntry;

import java.util.function.Consumer;

public class SwerveStateConsumer implements Consumer<SwerveModuleState[]> {

    final NetworkTableEntry entry;

    public SwerveStateConsumer(NetworkTableEntry entry) {
        this.entry = entry;
    }

    @Override
    public void accept(SwerveModuleState[] swerveModulePosition) {

        double[] data = new double[swerveModulePosition.length * 2];

        for (int i = 0; i < swerveModulePosition.length; i++) {
            data[i * 2] = swerveModulePosition[i].angle.getRadians();
            data[i * 2 + 1] = swerveModulePosition[i].speedMetersPerSecond;
        }
        entry.setDoubleArray(data);
    }
}
