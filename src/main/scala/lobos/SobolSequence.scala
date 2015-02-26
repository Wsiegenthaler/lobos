package lobos

import SobolSequence._

import scala.io.{Codec, Source}
import scala.math.{ceil, log, pow}
import java.util.zip.GZIPInputStream


/**
 * A low-discrepency Sobol sequence generator.
 *
 * @param dims The number of dimensions per point.
 * @param maxLength The maximum expected length of the sequence. This determines the number of direction values used per dimension.
 * @param params The parameters used to initialize the sequence. Default values are provided courtesy of Stephen Joe
 *               and Frances Kuo (see http://web.maths.unsw.edu.au/~fkuo/sobol) and support up to 21,201 dimensions
 */
class SobolSequence(dims:Int, maxLength:Long=pow(2, maxBits).toLong, val params:SobolParams=defaultParams)
  extends Iterator[IndexedSeq[Double]] {

  val maxDims = params.maxDims

  /* Number of bits per dimension used to compute the sequence (more bits, longer sequences) */
  val bits = bitsForVals(maxLength)

  /* Number of samples returned thus far */
  var count = 0

  /* Last point computed (unscaled) */
  var lastX:Option[IndexedSeq[Long]] = None

  /** Sanity checks */
  require(maxLength <= valsForBits(maxBits), s"Sobol sequence can be no longer than 2^$maxBits.")
  require(dims <= params.maxDims, s"Sobol sequence can have a max of ${params.maxDims} dimensions.")

  /** Initialize direction values for each dimension */
  val directionsByDim = (1 to dims) map {
    case 1 => (1 to bits) map { b => 1L << (bits - b) }
    case d => {
      /* Import the parameters needed to prepare this dimension's direction vector */
      val p = params.getParams(d)
      import p._

      /* Shift initial directions */
      val dirs = Array.fill(bits) { 0L }
      for (i <- 1 to s)
        dirs(i - 1) = m(i - 1) << (bits - i)

      /* Compute remaining directions */
      for (i <- s + 1 to bits) {
        dirs(i - 1) = dirs(i - s - 1) ^ (dirs(i - s - 1) >> s)

        for (k <- 1 to s - 1)
          dirs(i - 1) ^= a(s - k - 1) * dirs(i - k - 1)
      }

      dirs.toIndexedSeq
    }
  }

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
        directionsByDim.zipWithIndex map { case (d, i) => _last(i) ^ d(c) }
      }
    }

    /* Update mutable state */
    count += 1
    lastX = Some(x)

    /* Scale and return */
    x.map(_.toDouble / pow(2, bits))
  }
}

/** Static stuff */
object SobolSequence {

  /** Maximum bits-per-dimension employed by this sequence (64bits per Long) */
  val maxBits = 64

  /** Default parameter file path */
  val defaultParamPath = "/new-joe-kuo-6.21201.gz"

  /** Default parameter values */
  lazy val defaultParams = loadParams(defaultParamPath)

  /** Loads the 'SobolParams' from resource */
  def loadParams(path:String, codec:Codec=Codec.UTF8) = new SobolParams {

    override def getParams(dim:Int) = dimParams(dim - 1)
    override def maxDims = dimParams.length + 1

    protected val dimParams = {
      /* Open file and decompress if necessary */
      val gzipPattern = ".+\\.gz$".r
      def resource = getClass.getResourceAsStream(path)
      val source = Source.fromInputStream(path match {
        case gzipPattern(_*) => new GZIPInputStream(resource)
        case _ => resource
      })

      /* Parse */
      for (line <- source.getLines.drop(1)) yield {
        val values = line.split("\\s+")
        DimensionParams(values(0).toInt, values(2).toLong, values.slice(3, values.length).map(_.toLong))
      }
    } toArray
  }

  /** Computes minimum bits needed to represent 'n' values */
  def bitsForVals(n:Long) = n match { case n if (n < 1) => 0; case 1 => 1; case n => ceil(log(n) / log(2)).toInt }

  /** Computes number of values representable by 'n' bits */
  def valsForBits(n:Int) = n match { case 0 => 0L; case n => pow(2, n).toLong }

  /** Returns zero-based index of the rightmost zero. Used for the Gray code optimization. */
  def rightMostZero(n:Long):Int = if ((n & 1) == 0) 0 else rightMostZero(n >>> 1) + 1
}