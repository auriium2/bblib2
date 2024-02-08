package xyz.auriium.mattlib2.hardware.routine;

import xyz.auriium.mattlib2.loop.ISubroutine;
import xyz.auriium.mattlib2.loop.Outcome;
import xyz.auriium.mattlib2.loop.simple.ISimpleSubroutine;

public class SubroutineMotorCanSpin implements ISimpleSubroutine {

    @Override public void runSetup(SetupOrders orders) {

    }

    @Override public Outcome<Void> runLogic(Orders orders, Void whiteboard) {
        return null;
    }
}
