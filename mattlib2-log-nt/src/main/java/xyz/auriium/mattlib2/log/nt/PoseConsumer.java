package xyz.auriium.mattlib2.log.nt;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.NetworkTableEntry;

import java.util.function.Consumer;

public class PoseConsumer implements Consumer<Pose2d> {


    final NetworkTableEntry entry;

    public PoseConsumer(NetworkTableEntry entry) {
        this.entry = entry;
    }

    @Override
    public void accept(Pose2d pose2d) {
        entry.setDoubleArray(new double[] { pose2d.getX(), pose2d.getY(), pose2d.getRotation().getRadians() });
    }
}
