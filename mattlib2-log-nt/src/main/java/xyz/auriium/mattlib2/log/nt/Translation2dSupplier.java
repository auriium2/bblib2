package xyz.auriium.mattlib2.log.nt;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.networktables.NetworkTableEntry;

import java.util.function.Supplier;

public class Translation2dSupplier implements Supplier<Translation2d> {
    final NetworkTableEntry entry;
    public Translation2dSupplier(NetworkTableEntry entry) {
        this.entry = entry;
    }

    @Override
    public Translation2d get() {
        double[] doubles = entry.getDoubleArray(new double[] {0,0});
        return new Translation2d(doubles[0], doubles[1]);
    }
}
