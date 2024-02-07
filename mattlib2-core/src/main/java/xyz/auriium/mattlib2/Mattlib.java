package xyz.auriium.mattlib2;

import xyz.auriium.mattlib2.checker.CompileCheckLoader;
import xyz.auriium.mattlib2.checker.CompileCheckRegistry;
import xyz.auriium.mattlib2.log.MattLogSPI;
import yuukonfig.core.YuuKonfig;

import java.util.*;

public class Mattlib {

    public static final MattlibCompileChecker COMPILE_CHECKER;
    public static final MattlibLooper LOOPER;
    public static final IMattLog LOG;

    static {
        COMPILE_CHECKER = initCompileChecker();
        LOOPER = new MattlibLooper();
        LOG = getLog();
    }



    private static IMattLog getLog() {
        ClassLoader classLoader = YuuKonfig.class.getClassLoader();
        ServiceLoader<MattLogSPI> loader = ServiceLoader.load(MattLogSPI.class, classLoader);
        Iterator<MattLogSPI> it = loader.iterator();

        MattLogSPI provider;

        if (!it.hasNext()) {
            provider = null;
        } else {
            List<MattLogSPI> providers = new ArrayList<>();
            do {
                providers.add(it.next());
            } while (it.hasNext());

            providers.sort((Comparator.comparingInt(MattLogSPI::priority)).reversed());

            provider = providers.get(0);
        }

        if (provider == null) {
            throw new IllegalArgumentException("No MattLog provider loaded!!");
        }

        return provider.createLogger();
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
