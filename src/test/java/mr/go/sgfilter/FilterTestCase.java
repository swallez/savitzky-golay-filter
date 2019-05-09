package mr.go.sgfilter;

import static org.junit.Assert.assertEquals;
import mr.go.sgfilter.ContinuousPadder;
import mr.go.sgfilter.Linearizer;
import mr.go.sgfilter.MeanValuePadder;
import mr.go.sgfilter.RamerDouglasPeuckerFilter;
import mr.go.sgfilter.SGFilter;
import mr.go.sgfilter.ZeroEliminator;

import org.junit.Test;

public class FilterTestCase {

  private void assertCoeffsEqual(double[] coeffs, double[] tabularCoeffs) {
    for (int i = 0; i < tabularCoeffs.length; i++) {
      assertEquals(tabularCoeffs[i],
          coeffs[i],
          0.001);
    }
  }

  private void assertResultsEqual(double[] results, double[] real) {
    for (int i = 0; i < real.length; i++) {
      assertEquals(real[i],
          results[i],
          0.001);
    }
  }

  private void assertResultsEqual(float[] results, double[] real) {
    for (int i = 0; i < real.length; i++) {
      assertEquals(real[i],
          results[i],
          0.1);
    }
  }

  @Test
  public final void testComputeSGCoefficients() {
    double[] coeffs = SGFilter.computeSGCoefficients(5,
        5,
        2);
    double[] tabularCoeffs = new double[]{
        -0.084,
        0.021,
        0.103,
        0.161,
        0.196,
        0.207,
        0.196,
        0.161,
        0.103,
        0.021,
        -0.084
    };
    assertEquals(11,
        coeffs.length);
    assertCoeffsEqual(coeffs,
        tabularCoeffs);
    coeffs = SGFilter.computeSGCoefficients(5, 5, 4);
    tabularCoeffs = new double[]{0.042,
        -0.105,
        -0.023,
        0.140,
        0.280,
        0.333,
        0.280,
        0.140,
        -0.023,
        -0.105,
        0.042};
    assertEquals(11,
        coeffs.length);
    assertCoeffsEqual(coeffs,
        tabularCoeffs);
    coeffs = SGFilter.computeSGCoefficients(4,
        0,
        2);
    tabularCoeffs = new double[]{0.086,
        -0.143,
        -0.086,
        0.257,
        0.886};
    assertEquals(5,
        coeffs.length);
    assertCoeffsEqual(coeffs,
        tabularCoeffs);
  }

  @Test
  public final void testDouglasPeuckerFilter() {
    double[] data = new double[]{2.9,
        1.3,
        1.5,
        1.6,
        1.6,
        1,
        1.5,
        2,
        1.5,
        1,
        1,
        1,
        1,
        1,
        1};
    double[] result = new RamerDouglasPeuckerFilter(0.5).filter(data);
    double[] real = new double[]{2.9, 1.3, 1.6, 1, 2, 1, 1};
    assertResultsEqual(result, real);
  }

  @Test
  public final void testMeanValuePadderLeft() {
    double[] data = new double[]{0, 0, 0, 0, 0,
        8915.06,
        8845.53,
        9064.17,
        8942.09,
        8780.87,
        8916.81,
        8934.24,
        9027.06,
        9160.79,
        7509.14};
    double[] real = new double[]{8909.544000000002, 8909.544000000002,
        8909.544000000002, 8909.544000000002,
        8909.544000000002,
        8915.06,
        8845.53,
        9064.17,
        8942.09,
        8780.87,
        8916.81,
        8934.24,
        9027.06,
        9160.79,
        7509.14};
    new MeanValuePadder(10, true, false).apply(data);
    assertResultsEqual(data, real);
  }

  @Test
  public final void testLinearizer() {
    double[] data = new double[]{6945.43,
        0,
        0,
        7221.76,
        4092.77,
        6607.28,
        6867.01};
    double[] real = new double[]{6945.43,
        0,
        0,
        2202.426666666667,
        4404.853333333333,
        6607.280000000001,
        6867.01};
    new Linearizer(0.08f).apply(data);
    assertResultsEqual(data, real);
  }

  @Test
  public final void testMeanValuePadderRight() {
    double[] data = new double[]{8915.06,
        8845.53,
        9064.17,
        8942.09,
        8780.87,
        8916.81,
        8934.24,
        9027.06,
        9160.79,
        7509.14,
        0, 0, 0, 0};
    double[] real = new double[]{8915.06,
        8845.53,
        9064.17,
        8942.09,
        8780.87,
        8916.81,
        8934.24,
        9027.06,
        9160.79,
        7509.14,
        8709.608, 8709.608, 8709.608, 8709.608};
    new MeanValuePadder(10, false, true).apply(data);
    assertResultsEqual(data, real);
  }

  @Test
  public final void testSmooth() {
    float[] data = new float[]{8.91f,
        8.84f,
        9.06f,
        8.94f,
        8.78f};
    float[] leftPad = new float[]{8.91f,
        8.93f,
        9.02f,
        9.16f,
        7.50f};
    double[] realResult = new double[]{8.56394, 8.740239999999998, 8.962772,
        9.077350000000001, 8.80455};

    double[] coeffs = SGFilter.computeSGCoefficients(5,
        5,
        4);
    ContinuousPadder padder1 = new ContinuousPadder(false,
        true);
    SGFilter sgFilter = new SGFilter(5,
        5);
    sgFilter.appendPreprocessor(padder1);
    float[] smooth = sgFilter.smooth(data,
        leftPad,
        new float[0],
        coeffs);
    assertResultsEqual(smooth,
        realResult);
  }

  @Test
  public final void testSmoothWithBias() {
    double[] coeffs5_5 = SGFilter.computeSGCoefficients(5,
        5,
        4);
    double[] coeffs5_4 = SGFilter.computeSGCoefficients(5,
        4,
        4);
    double[] coeffs4_5 = SGFilter.computeSGCoefficients(4,
        5,
        4);
    float[] data = new float[]{1.26f,
        1.83f,
        1.83f,
        1.83f,
        1.83f,
        1.81f,
        1.81f,
        1.88f,
        1.88f,
        1.84f,
        1.84f,
        1.84f,
        1.84f};
    double[] real = new double[]{1.7939,
        1.80085,
        1.83971,
        1.85462,
        1.8452};
    SGFilter sgFilter = new SGFilter(5,
        5);
    float[] smooth = sgFilter.smooth(data,
        4,
        9,
        1,
        new double[][]{
            coeffs5_5,
            coeffs5_4,
            coeffs4_5});
    assertResultsEqual(smooth,
        real);
  }
}
