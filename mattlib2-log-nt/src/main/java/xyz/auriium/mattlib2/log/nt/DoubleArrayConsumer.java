package xyz.auriium.mattlib2.log.nt;

import edu.wpi.first.networktables.NetworkTableEntry;

import java.util.function.Consumer;

public class DoubleArrayConsumer implements Consumer<double[]> {

    final NetworkTableEntry entry;

    public DoubleArrayConsumer(NetworkTableEntry entry) {
        this.entry = entry;
    }

    @Override
    public void accept(double[] doubles) {
        entry.setDoubleArray(doubles);
    }
}
