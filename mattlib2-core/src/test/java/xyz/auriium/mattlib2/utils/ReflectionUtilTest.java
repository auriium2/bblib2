package xyz.auriium.mattlib2.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.auriium.mattlib2.Mattlib2Exception;
import xyz.auriium.mattlib2.log.INetworkedComponent;
import xyz.auriium.mattlib2.log.annote.Conf;
import xyz.auriium.mattlib2.log.annote.Log;
import xyz.auriium.mattlib2.log.annote.Tune;

import static org.junit.jupiter.api.Assertions.*;

public class ReflectionUtilTest {

    public interface TestingConfig extends INetworkedComponent {
        @Conf("a") @Log("a") void twoAnnotes(int i);
        void noAnnotes();
        @Log("b") void twoLogs(int i, int j);
        @Log("e") void noLogs();
        @Tune("k") int tuneWithParam(int param);
        @Log("l") int logWithReturn();
    }

    @Test
    public void testReflectionUtilShouldCatchIdiots() {
        Assertions.assertThrows(Mattlib2Exception.class, () -> {
            ReflectionUtil.checkMattLog(TestingConfig.class.getMethod("twoAnnotes", int.class));
        });

        Assertions.assertThrows(Mattlib2Exception.class, () -> {
            ReflectionUtil.checkMattLog(TestingConfig.class.getMethod("noAnnotes"));
        });

        Assertions.assertThrows(Mattlib2Exception.class, () -> {
            ReflectionUtil.checkMattLog(TestingConfig.class.getMethod("twoLogs", int.class, int.class));
        });

        Assertions.assertThrows(Mattlib2Exception.class, () -> {
            ReflectionUtil.checkMattLog(TestingConfig.class.getMethod("noLogs"));
        });

        Assertions.assertThrows(Mattlib2Exception.class, () -> {
            ReflectionUtil.checkMattLog(TestingConfig.class.getMethod("tuneWithParam", int.class));
        });

        Assertions.assertThrows(Mattlib2Exception.class, () -> {
            ReflectionUtil.checkMattLog(TestingConfig.class.getMethod("logWithReturn"));
        });
    }


}