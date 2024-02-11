package xyz.auriium.mattlib2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.auriium.mattlib2.loop.IMattlibHooked;
import xyz.auriium.yuukonstants.exception.ExplainedException;

import java.util.Optional;

public class MattlibLooperTest {

    public class ShitterUsingLoops implements IMattlibHooked {
        boolean initRun = false;

        public ShitterUsingLoops() {
            mattRegister();
        }

        @Override
        public ExplainedException[] verifyInit() {
            initRun = true;

            return new ExplainedException[0];
        }
    }

    @Test
    public void testInitShouldBeCalledWhenInitCorrectly() {
        var ok = new ShitterUsingLoops();

        Mattlib.LOOPER.runPreInit();
        Mattlib.LOOPER.runPostInit();

        Assertions.assertTrue(ok.initRun);

    }

    @Test
    public void testInitShouldNotWhenInitInCorrectly() {
        Mattlib.LOOPER.runPreInit();
        Mattlib.LOOPER.runPostInit();

        var ok = new ShitterUsingLoops();

        Assertions.assertFalse(ok.initRun);

    }

}
