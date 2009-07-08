import static java.lang.Math.pow;

import org.apache.commons.math.linear.RealMatrixImpl;

public class Main {

	/**
	 * @param args
	 *            - nl, nr, order
	 */
	public static void main(String[] args) {
		int nl = Integer.parseInt(args[0]);
		int nr = Integer.parseInt(args[1]);
		int degree = Integer.parseInt(args[2]);

		if (nl < 0 || nr < 0 || nl + nr < degree)
			throw new IllegalArgumentException("Bad arguments");
		RealMatrixImpl matrix = new RealMatrixImpl(degree + 1, degree + 1);
		double[][] a = matrix.getDataRef();
		double sum;
		for (int i = 0; i <= degree; i++) {
			for (int j = 0; j <= degree; j++) {
				sum = (i == 0 && j == 0) ? 1 : 0;
				for (int k = 1; k <= nr; k++)
					sum += pow(k, i + j);
				for (int k = 1; k <= nl; k++)
					sum += pow(-k, i + j);
				a[i][j] = sum;
			}
		}
		double[] b = new double[degree + 1];
		b[0] = 1;
		b = matrix.solve(b);
		for (int n = -nl; n <= nr; n++) {
			sum = b[0];
			for (int m = 1; m <= degree; m++)
				sum += b[m] * pow(n, m);
			System.out.print(String.format("%1$f\t", sum));
		}
	}

}
