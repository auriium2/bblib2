package xyz.auriium.mattlib2.nt;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableEvent;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;

public class SpecialUpdateSupplier implements BooleanSupplier, NetworkTable.TableEventListener {

    final NetworkTableEntry entry;

    final AtomicLong lastReadAt = new AtomicLong(System.currentTimeMillis());

    public SpecialUpdateSupplier(NetworkTableEntry entry) {
        this.entry = entry;
    }


    @Override
    public boolean getAsBoolean() {
        //has it changed since it was start up'd?




        return false;
    }

    @Override
    public void accept(NetworkTable table, String key, NetworkTableEvent event) {
        if (!event.is(NetworkTableEvent.Kind.kValueAll)) return;
        //it has changed

        lastReadAt.updateAndGet(l -> System.currentTimeMillis());

    }
}
