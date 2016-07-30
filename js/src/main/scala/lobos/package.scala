package lobos

import scala.scalajs.js
import scala.scalajs.js.annotation._


@js.native
trait LobosParamsWrapper extends js.Object {
  def decode():String = js.native
  val rawBase64Gz:String = js.native
}

@js.native
object LobosGlobalScope extends js.GlobalScope {
  val LobosParams:LobosParamsWrapper = js.native
}

object JsParams extends SobolParams {
  protected val params = LobosGlobalScope.LobosParams
  override def getParams(dim:Int) = dimParams(dim - 2)
  override def maxDims = dimParams.length + 1
  protected val dimParams = params.decode().split("\n").drop(1).map(_.split(" +")).map(v => DimensionParams(v(0).toInt, v(2).toLong, v.drop(3).map(_.toLong)))
}

@JSExport @JSExportAll
class Sobol(dims:Int) {
  val seq = new SobolSequence(dims)(JsParams)
  def next = js.Array(seq.next():_*)
  def take(n:Int):js.Array[js.Array[Double]] = js.Array(seq.take(n).map(js.Array(_:_*)).toArray:_*)
  def count = seq.count
}
