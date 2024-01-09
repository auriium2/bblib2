package xyz.auriium.mattlib2;

import xyz.auriium.mattlib2.checker.CompileCheckLoader;
import xyz.auriium.mattlib2.checker.CompileCheckRegistry;
import xyz.auriium.mattlib2.checker.MattlibCompileChecker;

import java.util.*;

public class Mattlib {

    public static final MattlibCompileChecker COMPILE_CHECKER;
    public static final MattlibLooper LOOPER;

    static {
        COMPILE_CHECKER = initCompileChecker();
        LOOPER = new MattlibLooper();
    }



    private static MattlibCompileChecker initCompileChecker() {
        ClassLoader classLoader = Mattlib.class.getClassLoader();
        ServiceLoader<CompileCheckLoader> loader = ServiceLoader.load(CompileCheckLoader.class, classLoader);
        Iterator<CompileCheckLoader> it = loader.iterator();

        CompileCheckRegistry registry = new CompileCheckRegistry();

        while (it.hasNext()) {
            registry = it.next().fillRegistry(registry);
        }

        return new MattlibCompileChecker(registry.gimme());
    }

}
