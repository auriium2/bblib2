package xyz.auriium.mattlib2.loop.general;

import xyz.auriium.mattlib2.MattConsole;
import xyz.auriium.mattlib2.log.ProcessPath;
import xyz.auriium.mattlib2.loop.ISubroutine;
import xyz.auriium.mattlib2.loop.IntelligentHub;
import xyz.auriium.mattlib2.loop.Outcome;

import java.util.function.Consumer;
/*

*/
/**
 * Wrapper that checks how long something takes, assigning it a name
 *//*

public class WatchdogRoutine<I,O> implements ISubroutine<I,O> {

    final ISubroutine<I,O> wrappedRoutine;
    final ProcessPath path;
    final IntelligentHub hub;

    public WatchdogRoutine(ISubroutine<I, O> wrappedRoutine, ProcessPath path, IntelligentHub hub) {
        this.wrappedRoutine = wrappedRoutine;
        this.path = path;
        this.hub = hub;
    }

    long time = System.currentTimeMillis();

    @Override public void runSetup(SetupOrders orders) {
        if (orders == SetupOrders.AWAKEN) {
            time = System.currentTimeMillis();
        }

        if (orders == SetupOrders.DIE) {
            hub.reportWatchdogFinished(path, System.currentTimeMillis() - time);
        }

        wrappedRoutine.runSetup(orders);
    }

    @Override public Outcome<O> runLogic(Orders orders, I whiteboard) {
        return wrappedRoutine.runLogic(orders, whiteboard);
    }
}
*/
