package xyz.auriium.mattlib2;

import xyz.auriium.mattlib2.loop.IMattlibHooked;

import java.util.ArrayList;
import java.util.List;

/**
 * This class makes sure things are looped, it is a static repository for all loops
 */
public class MattlibLooper {

    final List<IMattlibHooked> orderedThingsToBeLooped = new ArrayList<>();

    public void register(IMattlibHooked hook) {
        if (orderedThingsToBeLooped.contains(hook)) return;
        orderedThingsToBeLooped.add(hook);
    }


    public void runPreInit() {
        for (IMattlibHooked runnable : orderedThingsToBeLooped) {
            runnable.preInit();
        }
    }

    public void runPostInit() {
        for (IMattlibHooked runnable : orderedThingsToBeLooped) {
            runnable.verifyInit(); //TODO send those exceptions somewhere
            runnable.verify2Init();
        }
    }

    public void runPeriodicLoop() {

        for (IMattlibHooked runnable : orderedThingsToBeLooped) {
            runnable.logicPeriodic();
        }

        if (MattlibSettings.USE_LOGGING) {
            for (IMattlibHooked runnable : orderedThingsToBeLooped) {
                runnable.logPeriodic();
            }

            for (IMattlibHooked runnable : orderedThingsToBeLooped) {
                runnable.tunePeriodic();
            }
        }

    }

    /**
     * Exists so you can register a {@link IMattlibHooked} in one line when instantiating an object
     * @param someThing
     * @return
     * @param <T>
     */
    public <T extends IMattlibHooked> T registerAndReturn(T someThing) {
        if (orderedThingsToBeLooped.contains(someThing)) return someThing;
        orderedThingsToBeLooped.add(someThing);

        return someThing;
    }
}
