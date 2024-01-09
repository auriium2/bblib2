package xyz.auriium.mattlib2.nt;

import org.kordamp.jipsy.annotations.ServiceProviderFor;
import xyz.auriium.mattlib2.IMattLog;
import xyz.auriium.mattlib2.log.MattLogSPI;

@ServiceProviderFor(MattLogSPI.class)
public class NTMattLogSPI implements MattLogSPI {
    @Override
    public IMattLog createLogger() {
        return new NTMattLog();
    }

    @Override
    public byte priority() {
        return 1;
    }
}
