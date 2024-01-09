package xyz.auriium.mattlib2.foxe;

import org.kordamp.jipsy.annotations.ServiceProviderFor;
import xyz.auriium.mattlib2.IMattLog;
import xyz.auriium.mattlib2.log.MattLogSPI;

@ServiceProviderFor(MattLogSPI.class)
public class FoxgloveMattLogSPI implements MattLogSPI {
    @Override
    public IMattLog createLogger() {
        return new FoxgloveMattLog();
    }

    @Override
    public byte priority() {
        return 2;
    }
}
