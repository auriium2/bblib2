package xyz.auriium.mattlib2.loop.general;

import xyz.auriium.mattlib2.Exceptions;
import xyz.auriium.mattlib2.loop.ISubroutine;
import xyz.auriium.mattlib2.loop.Outcome;
/*

public class SequentialSubroutine<I,O> implements ISubroutine<I,O> {

    public interface CoalescingFunction<O> {
        O coalesce(O[] childrenOutput);
    }

    final ISubroutine<I,O>[] orderedRoutines;
    final CoalescingFunction<O> coalescingFunction;

    public SequentialSubroutine(ISubroutine<I, O>[] orderedRoutines, CoalescingFunction<O> coalescingFunction) {
        this.coalescingFunction = coalescingFunction;
        this.orderedRoutines = orderedRoutines;

        if (orderedRoutines.length == 0) throw Exceptions.BAD_ROUTINE_LENGTH();
    }

    int nextIndex = 0;
    ISubroutine<I,O> loaded = null;
    O[] childrenOutput =

    int maxIndex() {
        return orderedRoutines.length - 1;
    }

    @Override
    public void runSetup(SetupOrders orders) {
        if (orders != SetupOrders.AWAKEN) return;
        nextIndex = 0;
        loaded = orderedRoutines[0];
    }

    @Override
    public Outcome<O> runLogic(Orders orders, I whiteboard) {
        if (orders == Orders.CONTINUE) {

            Outcome<O> outcome = loaded.runLogic(Orders.CONTINUE, whiteboard);
            if (outcome.type() == Outcome.Type.FAIL) {
                //entire group should fail
                return outcome; //failure should bubble up
            }

            if (outcome.type() == Outcome.Type.SUCCESS) {
                //go to the next one
                loaded.runSetup(SetupOrders.DIE);

                int predictedNextIndex = nextIndex + 1;
                if (predictedNextIndex > maxIndex()) {
                    //the entire routine is done

                    return Outcome.success()

                }




            }


        }



        return null;
    }
}
*/
