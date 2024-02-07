package xyz.auriium.mattlib2.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.auriium.mattlib2.utils.AngleUtil;

class AngleUtilTest {

    @Test
    void wrapRotations() {
        double initialRotations = 0.5; //half a rotation
        double after = AngleUtil.normalizeRotations(initialRotations);

        Assertions.assertEquals(0.5, after);


        double initialRotations2 = 1.5; //one and a half
        double after2 = AngleUtil.normalizeRotations(initialRotations2);

        Assertions.assertEquals(0.5, after2);

        double initialRotations3 = -1.5; //-1.5
        double after3 = AngleUtil.normalizeRotations(initialRotations3);

        Assertions.assertEquals(0.5, after3);
    }
}