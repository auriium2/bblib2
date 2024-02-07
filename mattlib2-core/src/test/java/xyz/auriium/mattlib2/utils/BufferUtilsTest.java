package xyz.auriium.mattlib2.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BufferUtilsTest {

    @Test
    void add() {
        String[] dumb = new String[] {"a", "b"};
        String[] dumb2 = BufferUtils.add(dumb, "c");

        Assertions.assertEquals("c", dumb2[2]);
        Assertions.assertEquals("a", dumb2[0]);

    }

    @Test
    void testAdd() {
        int[] dumb = new int[] {2,4};
        int[] dumb2 = BufferUtils.add(dumb, 6);

        Assertions.assertEquals(6, dumb2[2]);
        Assertions.assertEquals(2, dumb2[0]);
    }
}