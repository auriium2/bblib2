package xyz.auriium.mattlib2.auto.ff;

import org.ojalgo.matrix.MatrixR064;
import org.ojalgo.matrix.decomposition.QR;
import org.ojalgo.matrix.store.MatrixStore;
import xyz.auriium.mattlib2.Mattlib2Exception;
import xyz.auriium.yuukonstants.exception.ExplainedException;

public class FastPolynomialRegression {

    final MatrixR064 polyCoefficientsBeta; //n by 1 vector matrix
    final int polynomialDegree;

    public FastPolynomialRegression(MatrixR064 polyCoefficientsBeta, int polynomialDegree) {
        this.polyCoefficientsBeta = polyCoefficientsBeta;
        this.polynomialDegree = polynomialDegree;
    }

    /**
     * Tries to fit a polynomial regression to the data, if it fails it will try a lower degree until it succeeds
     * @param x
     * @param y
     * @param desiredDegree desired degree of polynomial to start with
     * @return
     * @throws ExplainedException if the qr decomposition is impossible
     */
    public static FastPolynomialRegression loadRankDeficient_iterative(double[] x, double[] y, int desiredDegree) throws ExplainedException {
        MatrixR064 y_columnVector = MatrixR064.FACTORY.column(y); //y.length by 1 matrix

        int actualDegree = desiredDegree;
        QR<Double> qr;

        while (true) {
            double[][] vandermondeMatrixComposition = new double[x.length][actualDegree + 1];

            for (int i = 0; i < x.length; i++) {
                for (int j = 0; j <= actualDegree; j++) {
                    vandermondeMatrixComposition[i][j] = Math.pow(x[i], j);
                }
            }

            MatrixR064 vandermondeMatrix = MatrixR064.FACTORY.rows(vandermondeMatrixComposition);

            qr = QR.R064.make(vandermondeMatrix);
            if (!qr.decompose(vandermondeMatrix)) {
                throw new Mattlib2Exception("badCompositionOfMatrix", "qr decomposition attempted but failed", "contact matt");
            }

            if (qr.isSolvable() && qr.isFullRank()) {
                break; //we found it!
            }

            //TODO can't we just take the decomposition once, figure out the rank of r, and then set the degree to that if needed and take the decomposition again? no need to iterate..
            actualDegree--;

            if (actualDegree <= 0) {
                throw new Mattlib2Exception("impossibleRegression", "regression of this dataset is impossible", "contact matt");
            }
        }


        MatrixR064 polyCoefficientsBeta = MatrixR064.FACTORY.copy(qr.getSolution(y_columnVector)); //rank by 1 column vector
        return new FastPolynomialRegression(polyCoefficientsBeta, actualDegree);
    }


    /**
     *
     * @param j
     * @return coefficient of power j
     */
    public double beta(int j) {
        var out =  polyCoefficientsBeta.get(j, 0);
        if (Math.abs(out) < 1E-4) return 0.0;
        return out;
    }

    public double predict(double x) {
        double y = 0.0;
        for (int j = polynomialDegree; j >= 0; j--) y = beta(j) + (x * y);
        return y;
    }

    public int actualDegree() {
        return polynomialDegree;
    }
}
