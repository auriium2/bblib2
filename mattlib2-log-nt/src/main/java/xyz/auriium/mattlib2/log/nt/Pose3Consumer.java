package xyz.auriium.mattlib2.log.nt;

import edu.wpi.first.math.Vector;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.networktables.NetworkTableEntry;

import java.util.function.Consumer;

public class Pose3Consumer implements Consumer<Pose3d> {


    final NetworkTableEntry entry;

    public Pose3Consumer(NetworkTableEntry entry) {
        this.entry = entry;
    }

    @Override
    public void accept(Pose3d pose) {
        Vector<N3> angle = pose.getRotation().getAxis();

        entry.setDoubleArray(new double[] { pose.getX(), pose.getY(), pose.getZ(),angle.get(0,0), angle.get(1,0), angle.get(2,0) });
    }
}
