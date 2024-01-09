package xyz.auriium.mattlib2.log.nt;

import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.networktables.NetworkTableEntry;

import java.util.function.Consumer;

public class SwervePosConsumer implements Consumer<SwerveModulePosition[]> {

    final NetworkTableEntry entry;

    public SwervePosConsumer(NetworkTableEntry entry) {
        this.entry = entry;
    }

    @Override
    public void accept(SwerveModulePosition[] swerveModulePosition) {

        double[] data = new double[swerveModulePosition.length * 2];

        for (int i = 0; i < swerveModulePosition.length; i++) {
            data[i * 2] = swerveModulePosition[i].angle.getRadians();
            data[i * 2 + 1] = swerveModulePosition[i].distanceMeters;
        }
        entry.setDoubleArray(data);
    }
}
