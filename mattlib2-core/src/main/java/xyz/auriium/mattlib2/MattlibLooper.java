package xyz.auriium.mattlib2;

import xyz.auriium.mattlib2.loop.IMattlibHooked;
import xyz.auriium.yuukonstants.exception.ExplainedException;
import yuukonfig.core.ArrayUtil;

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

    public ExplainedException[] runPostInit() {

        ExplainedException[] problems = new ExplainedException[0];
        for (IMattlibHooked runnable : orderedThingsToBeLooped) {
            problems = ArrayUtil.combine(problems, runnable.verifyInit());
            runnable.verify2Init();
        }

        return problems;
    }

    public void runPeriodicLoop() {

        for (IMattlibHooked runnable : orderedThingsToBeLooped) {
            runnable.logicPeriodic();
        }

        for (IMattlibHooked runnable : orderedThingsToBeLooped) {
            runnable.alwaysLogPeriodic();
        }

        for (IMattlibHooked runnable : orderedThingsToBeLooped) {
            runnable.logPeriodic();
        }


        for (IMattlibHooked runnable : orderedThingsToBeLooped) {
            runnable.tunePeriodic();
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
