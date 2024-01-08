package xyz.auriium.mattlib2.log.nt;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.NetworkTableEntry;

import java.util.function.Supplier;

public class Pose2dSupplier implements Supplier<Pose2d> {

    final NetworkTableEntry entry;
    public Pose2dSupplier(NetworkTableEntry entry) {
       this.entry = entry;
    }

    @Override
    public Pose2d get() {

        double[] doubles = entry.getDoubleArray(new double[] {0,0,0});
        return new Pose2d(new Translation2d(doubles[0], doubles[1]), new Rotation2d(doubles[3]));
    }
}
