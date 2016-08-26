package lobos

/** Provides unique 'DimensionParams' instances for each dimension */
trait SobolParams {
  def getParams(dim:Int):DimensionParams
  def maxDims:Int
}
