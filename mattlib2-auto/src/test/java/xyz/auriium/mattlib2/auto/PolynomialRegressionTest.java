package xyz.auriium.mattlib2.auto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.auriium.mattlib2.auto.ff.PolynomialRegression;

class PolynomialRegressionTest {



    @Test
    public void testPolynomialRegressionIsSane() {
        double[] x = {10, 20, 40, 80, 160, 200};
        double[] y = {100, 350, 1500, 6700, 20160, 40000};
        PolynomialRegression regression = new PolynomialRegression(x, y, 40);

        Assertions.assertEquals(100, regression.predict(10), 1);
        Assertions.assertEquals(0.997, regression.R2(), 0.01);
    }

}