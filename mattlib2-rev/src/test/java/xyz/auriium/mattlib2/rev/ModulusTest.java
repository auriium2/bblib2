package xyz.auriium.mattlib2.rev;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ModulusTest {

    @Test
    public void modulusShouldWorkAsExpected() {
        double piMod = 3.14 % 1;
        double negativePiMod = -3.14 % 1;

        double piModD = 3.14 % 1d;
        double negativePiModD = -3.14 % 1d;


        Assertions.assertEquals(0.14, piMod, 0.001);
        Assertions.assertEquals(-0.14, negativePiMod, 0.001);

        Assertions.assertEquals(0.14, piModD, 0.001);
        Assertions.assertEquals(-0.14, negativePiModD, 0.001);

    }

}
