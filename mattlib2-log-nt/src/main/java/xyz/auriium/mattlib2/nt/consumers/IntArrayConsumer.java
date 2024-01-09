package xyz.auriium.mattlib2.nt.consumers;

import edu.wpi.first.networktables.NetworkTableEntry;

import java.util.function.Consumer;

public class IntArrayConsumer implements Consumer<Integer[]> {

    final NetworkTableEntry entry;

    public IntArrayConsumer(NetworkTableEntry entry) {
        this.entry = entry;
    }

    @Override
    public void accept(Integer[] integers) {
        long[] longArray = new long[integers.length];
        for (int i = 0; i < longArray.length; i++) {
            longArray[i] = integers[i];
        }

        entry.setIntegerArray(longArray);
    }
}
