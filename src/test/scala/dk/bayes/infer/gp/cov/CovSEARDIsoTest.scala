package dk.bayes.infer.gp.cov

import org.junit._
import Assert._
import scala.util.Random
import breeze.linalg.DenseMatrix
import scala.math._
import dk.bayes.math.linear.Matrix
import org.ejml.simple.SimpleMatrix
import breeze.linalg.DenseVector
import breeze.linalg.sum

class CovSEARDIsoTest {

  val n = 100
  val d = 20

  val rand = new Random(355675)

  val data = (1 to n).flatMap { i =>
    Array.fill(d)(rand.nextDouble())
  }.toArray

  val dataMatrix = Matrix(n, d, data)

  val logSf = log(2)
  val logEll = Array.fill(d)(log(10 * rand.nextDouble()))

  @Test def test = {

    val (cov, Some(covDfDsf), Some(covDfDell)) = CovSEARDIso(logSf, logEll).covWithD(dataMatrix)

    assertEquals(4, cov(0, 0), 0.0001)
    assertEquals(3.237, cov(1, 2), 0.0001)

    assertEquals(8, covDfDsf(0, 0), 0.0001)
    assertEquals(6.4740, covDfDsf(1, 2), 0.0001)

    assertEquals(0, covDfDell(0)(0, 0), 0.0001)
    assertEquals(0.1498, covDfDell(0)(1, 2), 0.0001)

    assertEquals(0, covDfDell(1)(0, 0), 0.0001)
    assertEquals(0.0730, covDfDell(1)(1, 2), 0.0001)

  }

}