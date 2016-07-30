package lobos

/** Provides unique 'DimensionParams' instances for each dimension */
trait SobolParams {
  def getParams(dim:Int):DimensionParams
  def maxDims:Int
}

/**
 * Precomputed primitive polynomial coefficients and initial direction values for a given dimension.
 *
 * @param d The one-based index of the dimension to be computed by these parameters
 * @param a The base-two coefficients of the primitive polynomial
 * @param m The initial direction values
 */
case class DimensionParams(val d:Int, val a:Long, m:Seq[Long]) {

  /** The number of precomputed directions */
  def s = m.length

  /** Returns the boolean coefficient at 'i', the zero-based index from the right */
  def a(i:Int):Long = 1 & (a >>> i)
}
