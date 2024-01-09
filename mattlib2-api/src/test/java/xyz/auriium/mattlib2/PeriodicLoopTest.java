package xyz.auriium.mattlib2;

import com.sun.source.tree.AssertTree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PeriodicLoopTest {

    public class LoopImplementor implements IPeriodicLooped {

        public boolean isTrigger = false;
        public LoopImplementor() {
            mattRegister();
        }

        @Override
        public void logicPeriodic() {
            isTrigger = true;
        }
    }

    public class LoopImplementor2 implements IPeriodicLooped {

        public boolean isTrigger = false;
        public LoopImplementor2() {

        }

        @Override
        public void logicPeriodic() {
            isTrigger = true;
        }
    }

    @Test
    public void mattRegisteredFunctionsShouldWork() {
        LoopImplementor l = new LoopImplementor();

        Mattlib.LOOPER.runPeriodicLoop();

        Assertions.assertTrue(l.isTrigger);
    }

    @Test
    public void mattUnregisteredFunctionsWontWork() {
        LoopImplementor2 l = new LoopImplementor2();

        Mattlib.LOOPER.runPeriodicLoop();

        Assertions.assertFalse(l.isTrigger);
    }

}
