package xyz.auriium.mattlib2;

import java.util.ArrayDeque;
import java.util.Queue;

public class MattLib {

    static final Queue<RuntimeException> EXCEPTIONS = new ArrayDeque<>();

    public static Runnable wrapExceptionalRunnable(Runnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (RuntimeException e) {
                EXCEPTIONS.add(e);
            }
        };
    }


}
