package xyz.auriium.mattlib2.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.auriium.mattlib2.IMattLogger;
import xyz.auriium.mattlib2.MattLog;
import xyz.auriium.mattlib2.ProcessPath;
import xyz.auriium.mattlib2.annotation.Conf;
import xyz.auriium.mattlib2.annotation.Log;
import xyz.auriium.mattlib2.components.IComponent;
import xyz.auriium.mattlib2.nt.InvocationSupplier;
import xyz.auriium.mattlib2.nt.PathCallConsumer;
import yuukonfig.core.annotate.Key;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FeatureTest {

    interface MyComponent extends IComponent {

        @Log
        @Key("double")
        void logDouble(double d);

        @Conf
        @Key("configurable")
        double confDouble();

    }

    static class MockMattLogger implements IMattLogger {

        boolean hasGenerated2 = false;
        boolean hasCalled2 = false;

        @Override
        public <T> Optional<Supplier<T>> generateTuner(ProcessPath path, T defaultValue) {
            hasGenerated2 = true;
            return Optional.of(new InvocationSupplier<>(() -> {
                hasCalled2 = true;
                return (T) new Object();
            }));
        }

        boolean hasGenerated = false;
        boolean hasCalled = false;

        @Override
        public <T> Optional<Consumer<T>> generateLogger(ProcessPath path, Class<T> type) {
            hasGenerated = true;
            return Optional.of(new PathCallConsumer<>( a -> {
                hasCalled = true;
            }));
        }
    }


    @Test
    public void testComponentLoadingStandard() {
        MockMattLogger logger = new MockMattLogger();
        MattLog log = new MattLog(logger);

        var future = log.loadFuture(MyComponent.class, "a/b");
        log.init();

        MyComponent component = future.join();

        Assertions.assertNotNull(component);
        Assertions.assertTrue(logger.hasGenerated);

        component.logDouble(2);

        Assertions.assertTrue(logger.hasCalled);

    }


}
