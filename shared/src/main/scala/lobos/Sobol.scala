package lobos

import Sobol._

import scala.math.{ceil, log, pow}

/**
 * A low-discrepency Sobol sequence generator.
 *
 * @param dims The number of dimensions per point.
 * @param resolution The resolution of the sequence in bits. This determines the number of direction values used to compute each dimension
 * and thus the maximum length of the sequence (e.g. 63 bits will provide a sequence of 2^63 elements).
 * @param params The parameters used to initialize the sequence. Default values are provided courtesy of Stephen Joe
 *               and Frances Kuo (see http://web.maths.unsw.edu.au/~fkuo/sobol) and support up to 21,201 dimensions
 */
class Sobol(dims:Int, resolution:Byte=maxBits)(implicit val params:SobolParams)
  extends Iterator[IndexedSeq[Double]] {

  val maxDims = params.maxDims

  /* Number of samples returned thus far */
  var count = 0

  /* Last point computed (unscaled) */
  var lastX:Option[IndexedSeq[Long]] = None

  /** Sanity checks */
  require(resolution > 0 && resolution <= maxBits, s"Sobol sequence bit resolution must be between 1 and $maxBits.")
  require(dims <= params.maxDims, s"Sobol sequence can have a max of ${params.maxDims} dimensions.")

  /** Initialize direction values for each dimension (direction/index tuples) */
  val directionsByDim = (1 to dims).map {
    case 1 => (1 to resolution) map { b => 1L << (resolution - b) }
    case dim => {
      /* Import the parameters needed to prepare this dimension's direction vector */
      val p = params.getParams(dim)
      import p._

      /* Shift initial directions */
      val dirs = Array.fill(resolution) { 0L }
      for (i <- 1 to s)
        dirs(i - 1) = m(i - 1) << (resolution - i)

      /* Compute remaining directions */
      for (i <- s + 1 to resolution) {
        dirs(i - 1) = dirs(i - s - 1) ^ (dirs(i - s - 1) >> s)

        for (k <- 1 to s - 1)
          dirs(i - 1) ^= a(s - k - 1) * dirs(i - k - 1)
      }

      dirs.toIndexedSeq
    }
  }.zipWithIndex

  /** The maximum expected length of the sequence as determined by the resolution */
  val maxLength = valsForBits(resolution)

  /** Will produce up to 'maxLength' values */
  override def hasNext = count < maxLength

  /** Produce next point in the sequence */
  override def next = {
    val x = lastX match {
      /* Initial point, zero */
      case None => Vector.fill(dims) { 0L }
      /* Subsequent points */
      case Some(_last) => {
        val c = rightMostZero(count-1)
        directionsByDim map { case (d, i) => _last(i) ^ d(c) }
      }
    }

    /* Update mutable state */
    count += 1
    lastX = Some(x)

    /* Scale and return */
    x.map(_.toDouble / pow(2, resolution))
  }
}

/** Static stuff */
object Sobol {

  /** Maximum bits-per-dimension employed by this sequence (`Double` significand is 53 bits) */
  val maxBits:Byte = 53
  
  /** Computes minimum bits needed to represent 'n' values */
  def bitsForVals(n:Long) = n match { case n if (n < 1) => 0; case 1 => 1; case n => ceil(log(n) / log(2)).toInt }

  /** Computes number of values representable by 'n' bits */
  def valsForBits(n:Int) = n match { case 0 => 0L; case n => pow(2, n).toLong }

  /** Returns zero-based index of the rightmost zero. Used for the Gray code optimization. */
  def rightMostZero(n:Long):Int = if ((n & 1) == 0) 0 else rightMostZero(n >>> 1) + 1
}
