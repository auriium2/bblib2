package xyz.auriium.mattlib2;

import java.util.ArrayList;
import java.util.List;

/**
 * This class makes sure things are looped, it is a static repository for all loops
 */
public class MattLoopManager {

    final List<IPeriodicLooped> orderedThingsToBeLooped;

    public MattLoopManager(List<IPeriodicLooped> orderedThingsToBeLooped) {
        this.orderedThingsToBeLooped = orderedThingsToBeLooped;
    }

    public MattLoopManager() {
        this(new ArrayList<>());
    }

    public void markForLooping(IPeriodicLooped runnable) {
        orderedThingsToBeLooped.add(runnable);
    }


    public void runLoggingPeriodic() {
        for (IPeriodicLooped runnable : orderedThingsToBeLooped) {
            runnable.logPeriodic();
        }

        for (IPeriodicLooped runnable : orderedThingsToBeLooped) {
            runnable.tunePeriodic();
        }
    }

    /**
     * Exists so you can register a {@link IPeriodicLooped} in one line when instantiating an object
     * @param someThing
     * @return
     * @param <T>
     */
    public <T extends IPeriodicLooped> T registerAndReturn(T someThing) {
        orderedThingsToBeLooped.add(someThing);

        return someThing;
    }
}
