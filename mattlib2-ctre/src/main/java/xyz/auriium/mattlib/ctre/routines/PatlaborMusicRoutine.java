package xyz.auriium.mattlib.ctre.routines;

import com.ctre.phoenix6.Orchestra;
import xyz.auriium.mattlib2.loop.Outcome;
import xyz.auriium.mattlib2.loop.simple.ISimpleSubroutine;

/**
 * lol
 */
public class PatlaborMusicRoutine implements ISimpleSubroutine {


    final Orchestra orchestra;

    PatlaborMusicRoutine(Orchestra orchestra) {
        this.orchestra = orchestra;
    }

    @Override
    public void runSetup(SetupOrders orders) {
        if (orders == SetupOrders.AWAKEN) {
            orchestra.play();
        }

        if (orders == SetupOrders.DIE) {
            orchestra.stop();
        }
    }

    @Override
    public Outcome<Void> runLogic(Orders orders, Void whiteboard) {
        if (orchestra.isPlaying()) return Outcome.working();

        return Outcome.success();
    }

}
