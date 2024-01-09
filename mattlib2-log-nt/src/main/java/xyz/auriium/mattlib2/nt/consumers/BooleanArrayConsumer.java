package xyz.auriium.mattlib2.nt.consumers;

import edu.wpi.first.networktables.NetworkTableEntry;

import java.util.function.Consumer;

public class BooleanArrayConsumer implements Consumer<boolean[]> {


    final NetworkTableEntry entry;

    public BooleanArrayConsumer(NetworkTableEntry entry) {
        this.entry = entry;
    }

    @Override
    public void accept(boolean[] doubles) {
        entry.setBooleanArray(doubles);
    }
}
