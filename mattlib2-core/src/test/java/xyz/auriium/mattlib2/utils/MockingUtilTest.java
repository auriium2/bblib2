package xyz.auriium.mattlib2.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.auriium.mattlib2.utils.MockingUtil;

/**
 * Mocking util only works if the class is visible to it
 */
public class MockingUtilTest {

    public static class StubClass {
        public int hello() {
            return 5;
        }
    }


    @Test
    public void shouldMock() {
        StubClass stubClass = MockingUtil.buddy(StubClass.class);

        Assertions.assertEquals(0, stubClass.hello());
    }

}