package xyz.auriium.mattlib2.nt.consumers;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.NetworkTableEntry;

import java.util.function.Consumer;

public class TranslationConsumer implements Consumer<Translation2d> {

    final NetworkTableEntry entry;

    public TranslationConsumer(NetworkTableEntry entry) {
        this.entry = entry;
    }

    @Override
    public void accept(Translation2d translation2d) {
        entry.setDoubleArray(new double[] { translation2d.getX(), translation2d.getY() });
    }
}
