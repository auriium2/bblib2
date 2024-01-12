package xyz.auriium.mattlib2.log;

import org.kordamp.jipsy.annotations.ServiceProviderFor;
import xyz.auriium.mattlib2.IMattLog;

@ServiceProviderFor(MattLogSPI.class)
public class TestMattLogSPI implements MattLogSPI{
    @Override public IMattLog createLogger() {
        return null;
    }

    @Override public byte priority() {
        return 0;
    }
}
