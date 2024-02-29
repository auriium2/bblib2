package xyz.auriium.mattlib2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import xyz.auriium.mattlib2.Mattlib;

public class TestSPILoader {

    @Disabled
    @Test
    public void testShouldLoadSPI() {

        Assertions.assertEquals("NTMattLog", Mattlib.LOG.getClass().getSimpleName());

    }

}
