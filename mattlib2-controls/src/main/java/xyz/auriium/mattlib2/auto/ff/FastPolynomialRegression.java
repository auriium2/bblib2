package xyz.auriium.mattlib2.auto.ff;

import org.ojalgo.matrix.MatrixR064;
import org.ojalgo.matrix.decomposition.QR;
import xyz.auriium.yuukonstants.exception.ExplainedException;

//faster but not fast lol
public class FastPolynomialRegression {

    final MatrixR064 polyCoefficientsBeta; //n by 1 vector matrix
    final int polynomialDegree;

    public FastPolynomialRegression(MatrixR064 polyCoefficientsBeta, int polynomialDegree) {
        this.polyCoefficientsBeta = polyCoefficientsBeta;
        this.polynomialDegree = polynomialDegree;
    }

    public static FastPolynomialRegression loadFullRank(double[] x, double[] y, int polynomialDegree) throws ExplainedException {

        double[][] vandermondeMatrixComposition = new double[x.length][polynomialDegree + 1];
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j <= polynomialDegree; j++) {
                vandermondeMatrixComposition[i][j] = Math.pow(x[i], j);
            }
        }


        MatrixR064 vandermondeMatrix = MatrixR064.FACTORY.rows(vandermondeMatrixComposition);
        MatrixR064 qrSolution = MatrixR064.FACTORY.make(QR.R064.decompose(vandermondeMatrix).reconstruct());


        MatrixR064 yMatrix = MatrixR064.FACTORY.rows(y);
        MatrixR064 polyCoefficientsBeta = qrSolution.solve(yMatrix);

        return new FastPolynomialRegression(polyCoefficientsBeta, polynomialDegree);

    }

    public static FastPolynomialRegression loadRankDeficient_iterative(double[] x, double[] y, int degree) throws ExplainedException {

        int polynomialDegree = degree;
        MatrixR064 qrSolution;

        while (true) {
            double[][] vandermondeMatrixComposition = new double[x.length][polynomialDegree + 1];
            for (int i = 0; i < x.length; i++) {
                for (int j = 0; j <= polynomialDegree; j++) {
                    vandermondeMatrixComposition[i][j] = Math.pow(x[i], j);
                }
            }

            MatrixR064 vandermondeMatrix = MatrixR064.FACTORY.rows(vandermondeMatrixComposition);

            System.out.println(vandermondeMatrix.countRows() + " " + vandermondeMatrix.countColumns());

            qrSolution = MatrixR064.FACTORY.make(QR.R064.decompose(vandermondeMatrix).reconstruct());
            if (qrSolution.getRank() == degree + 1) {
                break;
            }

            degree--;

        }
        MatrixR064 yMatrix = MatrixR064.FACTORY.rows(y);
        MatrixR064 polyCoefficientsBeta = qrSolution.solve(yMatrix);

        return new FastPolynomialRegression(polyCoefficientsBeta, polynomialDegree);
    }


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
}
