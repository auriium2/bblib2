package xyz.auriium.mattlib2.nt.suppliers;

import edu.wpi.first.networktables.NetworkTableEntry;

import java.util.function.Supplier;

public class PureNTIntArraySupplier implements Supplier<int[]> {

    final NetworkTableEntry entry;

    public PureNTIntArraySupplier(NetworkTableEntry entry) {
        this.entry = entry;
    }

    @Override
    public int[] get() {
        long[] longArray =  entry.getIntegerArray(new long[0]);
        int[] intArray = new int[longArray.length];
        for (int i = 0; i < longArray.length; i++) {
            intArray[i] = Math.toIntExact(longArray[i]);
        }

        return intArray;
    }
}
