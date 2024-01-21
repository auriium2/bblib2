package xyz.auriium.mattlib2;

import java.util.ArrayList;
import java.util.List;

/**
 * This class makes sure things are looped, it is a static repository for all loops
 */
public class MattlibLooper {

    final List<IPeriodicLooped> orderedThingsToBeLooped = new ArrayList<>();

    public void register(IPeriodicLooped runnable) {
        if (orderedThingsToBeLooped.contains(runnable)) return;
        orderedThingsToBeLooped.add(runnable);
    }


    public void runPreInit() {
        for (IPeriodicLooped runnable : orderedThingsToBeLooped) {
            runnable.preInit();
        }
    }

    public void runPostInit() {
        for (IPeriodicLooped runnable : orderedThingsToBeLooped) {
            runnable.verifyInit(); //TODO send those exceptions somewhere
        }
    }

    public void runPeriodicLoop() {

        for (IPeriodicLooped runnable : orderedThingsToBeLooped) {
            runnable.logicPeriodic();
        }

        if (MattlibSettings.USE_LOGGING) {
            for (IPeriodicLooped runnable : orderedThingsToBeLooped) {
                runnable.logPeriodic();
            }

            for (IPeriodicLooped runnable : orderedThingsToBeLooped) {
                runnable.tunePeriodic();
            }
        }

    }

    /**
     * Exists so you can register a {@link IPeriodicLooped} in one line when instantiating an object
     * @param someThing
     * @return
     * @param <T>
     */
    public <T extends IPeriodicLooped> T registerAndReturn(T someThing) {
        if (orderedThingsToBeLooped.contains(someThing)) return someThing;
        orderedThingsToBeLooped.add(someThing);

        return someThing;
    }
}
