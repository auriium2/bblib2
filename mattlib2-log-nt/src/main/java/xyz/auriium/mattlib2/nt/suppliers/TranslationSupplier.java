package xyz.auriium.mattlib2.nt.suppliers;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.NetworkTableEntry;

import java.util.function.Supplier;

public class TranslationSupplier implements Supplier<Translation2d> {
    final NetworkTableEntry entry;
    public TranslationSupplier(NetworkTableEntry entry) {
        this.entry = entry;
    }

    @Override
    public Translation2d get() {
        double[] doubles = entry.getDoubleArray(new double[] {0,0});
        return new Translation2d(doubles[0], doubles[1]);
    }
}
