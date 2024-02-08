package xyz.auriium.mattlib2.loop.simple;

import xyz.auriium.mattlib2.loop.ISubroutine;
import xyz.auriium.mattlib2.loop.Outcome;

public interface ISimpleSubroutine extends ISubroutine<Void, Void> {

    /**
     * The method describing the order-outcome loop. The routine takes orders, performs actions, then describes the outcome of it's mission
     *
     * @param orders
     */
    default Outcome<Void> runLogic(Orders orders) {
        return runLogic(orders, null);
    }

}
