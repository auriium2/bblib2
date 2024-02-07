package xyz.auriium.mattlib2.log;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProcessPathTest {

    @Test
    public void testGoOneBackWorks() {
        ProcessPath path = ProcessPath.of("hi/die");

        Assertions.assertEquals("hi",path.goOneBack().get().tablePath());

    }


}