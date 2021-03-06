package dk.bayes.math.gaussian.canonical

import org.junit._
import org.junit.Assert._

import DenseCanonicalGaussianTest._
import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import dk.bayes.math.linear._

object DenseCanonicalGaussianTest {

  val x = DenseCanonicalGaussian(3, 1.5)

  val a = DenseMatrix(-0.1)
  val yGivenx = DenseCanonicalGaussian(a, 2, 0.5)
}

class DenseCanonicalGaussianTest {

  /**
   * Tests for class constructor - illegal arguments
   */

  @Test(expected = classOf[IllegalArgumentException]) def constructor_wrong_dimensions_of_k_and_h: Unit = {
    new DenseCanonicalGaussian(new DenseMatrix(2, 2, Array(0d, 0, 0, 0)), DenseVector(0d), 0)
  }

  @Test(expected = classOf[IllegalArgumentException]) def constructor_k_matrix_is_not_square: Unit = {
    new DenseCanonicalGaussian(new DenseMatrix(3, 2, Array(0d, 0, 0, 0, 0, 0)), DenseVector(0, 0, 0), 0)
  }

  /**
   * Tests for class constructor - linear gaussian
   */

  @Test def constructor_linear_gaussian: Unit = {
    val gaussian1 = DenseCanonicalGaussian(a = DenseMatrix(0.7), b = 1.5, v = 2)
    val gaussian2 = DenseCanonicalGaussian(a = DenseMatrix(0.7), b = DenseVector(1.5), v = DenseMatrix(2d))
    assertGaussian(gaussian1, gaussian2)

    DenseCanonicalGaussian(DenseMatrix(1d, -1d).t, 0, 1e-12)
  }

  @Test def constructor_linear_gaussian_multivariate = {

    val gaussian = DenseCanonicalGaussian(a = new DenseMatrix(2, 2, Array(1d, 0.2, 0.4, 2)).t, b = DenseVector(0.4, 0.7), v = new DenseMatrix(2, 2, Array(1.2, 0.9, 0.9, 2.3)).t)

    val expectedK = new DenseMatrix(gaussian.k.rows, gaussian.k.cols, Array(0.9087179487179489, -0.23179487179487201, -0.9948717948717951, 0.2153846153846155, -0.231794871794872, 2.1394871794871793, 0.6871794871794874, -1.1384615384615384, -0.9948717948717951, 0.6871794871794874, 1.1794871794871797, -0.4615384615384617, 0.2153846153846155, -1.1384615384615384, -0.4615384615384617, 0.6153846153846154))
    assertTrue(isIdentical(expectedK, gaussian.k, 0.00001))

    assertTrue(isIdentical(DenseVector(-0.2471794871794872, -0.5220512820512819, 0.14871794871794874, 0.2461538461538461), gaussian.h, 0.0001))

    assertTrue(isIdentical(DenseVector(-0.2471794871794872, -0.5220512820512819, 0.14871794871794874, 0.2461538461538461), gaussian.h, 0.0001))

    assertEquals(-4.125566255003954, gaussian.g, 0.00001)
  }

  /**
   * Tests for pdf() method
   */

  @Test def pdf = {

    assertEquals(0.398942, DenseCanonicalGaussian(0, 1).pdf(0), 0.0001)
    assertEquals(0.2419, DenseCanonicalGaussian(0, 1).pdf(-1), 0.0001)

    assertEquals(0.10648, DenseCanonicalGaussian(2, 9).pdf(0), 0.0001)
    assertEquals(0.08065, DenseCanonicalGaussian(2, 9).pdf(-1), 0.0001)

    assertEquals(0.03707, DenseCanonicalGaussian(1.65, 0.5).pdf(0), 0.0001)
    assertEquals(0.4607, DenseCanonicalGaussian(1.65, 0.5).pdf(1.2), 0.0001)

    assertEquals(0.0336, DenseCanonicalGaussian(1.7, 0.515).pdf(0), 0.0001)

    assertEquals(0.01111, DenseCanonicalGaussian(DenseVector(Array(3, 1.7)), new DenseMatrix(2, 2, Array(1.5, -0.15, -0.15, 0.515))).pdf(DenseVector(3.5, 0)), 0.0001)
  }

  @Test def pdf_linear_gaussian_cpd = {

    assertEquals(0.03707, yGivenx.pdf(DenseVector(3.5, 0)), 0.0001)
  }

  /**
   * Tests for marginalise() method
   */

  @Test def marginalise_y = {

    val marginalX = (x.extend(2, 0) * yGivenx).marginalise(1)

    assertTrue(isIdentical(DenseVector(3d), marginalX.mean, 0.0001))
    assertTrue(isIdentical(DenseMatrix(1.5), marginalX.variance, 0.0001))
  }

  @Test def marginalise_y_from_gaussian_cpd = {

    val marginalX = (yGivenx).marginalise(1)

    assertEquals(DenseVector(0d).toString(), marginalX.mean.toString())
    assertEquals(2.8823037615171174E17, marginalX.variance(0, 0), 0.00001)
  }

  @Test def marginalise_x_from_gaussian_cpd = {

    val marginalY = (yGivenx).marginalise(0)

    assertEquals(2, marginalY.mean(0), 0.0001)
    assertEquals(2.2517998136852475E15, marginalY.variance(0, 0), 0.0001)
  }
  @Test def marginalise_x = {

    val marginalY = (x.extend(2, 0) * yGivenx).marginalise(0)

    assertTrue(isIdentical(DenseVector(1.7), marginalY.mean, 0.0001))
    assertEquals(DenseMatrix(0.515).toString(), marginalY.variance.toString())
    assertEquals(0.03360, marginalY.pdf(DenseVector(0.0)), 0.0001)
  }

  @Test def marginalise_y_from_linear_gaussian_times_y_scenario_1 = {
    val y = DenseCanonicalGaussian(3, 1.5)

    val yGivenx = DenseCanonicalGaussian(DenseMatrix(1d), 0, 0.5)

    val marginalX = (yGivenx * y.extend(2, 1)).marginalise(1)

    assertEquals(3, marginalX.toGaussian.m, 0.0001)
    assertEquals(2, marginalX.toGaussian.v, 0.00001)

  }

  @Test def marginalise_y_from_linear_gaussian_times_y_scenario_2 = {
    val y = DenseCanonicalGaussian(3, 1.5)

    val yGivenx = DenseCanonicalGaussian(DenseMatrix(2d), 0, 0.5)

    val marginalX = (yGivenx * y.extend(2, 1)).marginalise(1)

    assertEquals(1.5, marginalX.toGaussian.m, 0.00001)
    assertEquals(0.5, marginalX.toGaussian.v, 0.00001)

  }

  @Test def marginalise_y_from_linear_gaussian_times_y_scenario_3 = {
    val y = DenseCanonicalGaussian(3, 1.5)

    val yGivenx = DenseCanonicalGaussian(DenseMatrix(1.0), 0.4, 0.5)

    val marginalX = (yGivenx * y.extend(2, 1)).marginalise(1)

    assertEquals(2.6, marginalX.toGaussian.m, 0.0001)
    assertEquals(2, marginalX.toGaussian.v, 0.0001)

  }

  @Test def marginalise_from_linear_gaussian_multivariate = {
    val mean = DenseVector(0.1, 0.2, 0.3)
    val variance = new DenseMatrix(3, 3, Array(1d, 0.1, 0.2, 0.1, 2, 0.4, 0.2, 0.4, 3))

    val gaussian = DenseCanonicalGaussian(mean, variance)

    val a = new DenseMatrix(2, 3, Array(1d, 0, 0, 0, 0, 1))
    val linear = DenseCanonicalGaussian(a, b = DenseVector(0d, 0), v = new DenseMatrix(2, 2, Array(1d, 0.1, 0.1, 2)))

    val marginal = (gaussian.extend(5, 0) * linear).marginal(3, 4)

    assertTrue(isIdentical(DenseVector(0.1, 0.3), marginal.mean, 0.0001))
    assertTrue("actual=" + marginal.variance, isIdentical(new DenseMatrix(2, 2, Array(2, 0.3, 0.3, 5)).t, marginal.variance, 0.0001))
  }

  /**
   * Tests for marginal()
   */
  @Test def marginal = {
    val y = DenseCanonicalGaussian(3, 1.5)

    val yGivenx = DenseCanonicalGaussian(DenseMatrix(1.0), 0.4, 0.5)

    val expectedMarginalX = (yGivenx * y.extend(2, 1)).marginalise(1)
    val actualMarginalY = (yGivenx * y.extend(2, 1)).marginal(0)

    assertEquals(expectedMarginalX.toGaussian.m, actualMarginalY.toGaussian.m, 0.0001)
    assertEquals(expectedMarginalX.toGaussian.v, actualMarginalY.toGaussian.v, 0.0001)
  }

  @Test def marginal_two_variables_from_3d_Gaussian = {

    val gaussian = DenseCanonicalGaussian(DenseVector(1.0, 2, 3), new DenseMatrix(3, 3, Array(1, 0.6, 0.8, 0.6, 2, 0.5, 0.8, 0.5, 3)))

    val marginal = gaussian.marginal(0, 2)
    assertTrue(isIdentical(DenseVector(1d, 3), marginal.mean, 0.0001))
    assertTrue("actual=" + marginal.variance, isIdentical(new DenseMatrix(2, 2, Array(1, 0.8, 0.8, 3)).t, marginal.variance, 0.0001))
  }
  /**
   * Tests for withEvidence() method
   */
  @Test def withEvidence_marginal_y = {

    val marginalY = yGivenx.withEvidence(0, 3.5)

    assertEquals(0.03707, marginalY.pdf(DenseVector(0.0)), 0.0001)
    assertTrue(isIdentical(DenseVector(1.65), marginalY.mean, 0.0001))
    assertTrue(isIdentical(DenseMatrix(0.5), marginalY.variance, 0.0001))
  }

  @Test def withEvidence_marginal_y_given_x = {

    val marginalY = (x.extend(2, 0) * yGivenx).withEvidence(0, 3.5) //CanonicalGaussian(Array(xId, yId), Matrix(Array(3, 1.7)), Matrix(2, 2, Array(1.5, -0.15, -0.15, 0.515))).withEvidence(xId, 3.5)

    assertTrue(isIdentical(DenseVector(1.65), marginalY.mean, 0.0001))
    assertTrue(isIdentical(DenseMatrix(0.5), marginalY.variance, 0.0001))
    assertEquals(0.0111, marginalY.pdf(DenseVector(0.0)), 0.0001d)
  }

  @Test def withEvidence_marginal_x_given_y = {

    val marginalX = (x.extend(2, 0) * yGivenx).withEvidence(1, 2.5)

    assertTrue(isIdentical(DenseVector(2.7669), marginalX.mean, 0.0001))
    assertTrue(isIdentical(DenseMatrix(1.4563), marginalX.variance, 0.0001))
    assertEquals(0.00712, marginalX.pdf(DenseVector(0.0)), 0.0001d)
  }

  @Test def withEvidence_marginal_x_given_y_version2 = {

    val marginalX = ((x.extend(2, 0) * yGivenx).withEvidence(1, 2.5))

    assertTrue(isIdentical(DenseVector(2.7669), marginalX.mean, 0.0001))
    assertTrue(isIdentical(DenseMatrix(1.4563), marginalX.variance, 0.0001))
    assertEquals(0.00712, marginalX.pdf(DenseVector(0.0)), 0.0001d)
  }

  @Test def withEvidence_marginal_x_given_y_version3 = {

    val marginalX = (yGivenx * x.extend(2, 0)).withEvidence(1, 2.5)
    assertTrue(isIdentical(DenseVector(2.7669), marginalX.mean, 0.0001))
    assertTrue(isIdentical(DenseMatrix(1.4563), marginalX.variance, 0.0001))

    assertEquals(0.00712, marginalX.pdf(DenseVector(0.0)), 0.0001d)
  }

  @Test def getMu_getSigma = {
    val gaussian = DenseCanonicalGaussian(DenseVector(1.65), DenseMatrix(0.5))
    assertEquals(DenseVector(1.65).toString(), gaussian.mean.toString())
    assertTrue(isIdentical(DenseMatrix(0.5), gaussian.variance, 0.0001))

    val (mean, variance) = (gaussian.mean, gaussian.variance)
    assertEquals(DenseVector(1.65).toString(), mean.toString())
    assertTrue(isIdentical(DenseMatrix(0.5), variance, 0.0001))
  }

  private def assertGaussian(expected: DenseCanonicalGaussian, actual: DenseCanonicalGaussian) = {
    assertTrue(isIdentical(expected.k, actual.k, 0.0001))
    assertTrue(isIdentical(expected.h, actual.h, 0.0001))
    assertEquals(expected.g, actual.g, 0.0001)
  }

}