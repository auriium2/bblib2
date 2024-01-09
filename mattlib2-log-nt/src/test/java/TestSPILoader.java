import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.auriium.mattlib2.Mattlib;

public class TestSPILoader {

    @Test
    public void testShouldLoadSPI() {

        Assertions.assertEquals("NTMattLog", Mattlib.LOG.getClass().getSimpleName());

    }

}
