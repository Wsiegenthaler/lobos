package lobos

import scala.scalajs.js
import scala.scalajs.js.annotation._


@js.native
trait LobosParamsWrapper extends js.Object {
  def decode(paramId:Option[String]):String = js.native
}

@js.native
object LobosGlobalScope extends js.GlobalScope {
  val LobosParams:LobosParamsWrapper = js.native
}

class JsParams(paramId:Option[String]=None) extends SobolParams {
  protected val module = LobosGlobalScope.LobosParams
  override def getParams(dim:Int) = dimParams(dim - 2)
  override def maxDims = dimParams.length + 1
  lazy protected val dimParams = module.decode(paramId).split("\n").drop(1).map(_.split(" +")).map(v => DimensionParams(v(0).toInt, v(2).toLong, v.drop(3).map(_.toLong)))
}

object ParamCache {
  protected val cache = scala.collection.mutable.HashMap.empty[Option[String], JsParams]
  def apply(paramId:Option[String]=None) = cache.getOrElseUpdate(paramId, new JsParams(paramId))
}

@JSExport("lobos.Sobol")
class JsSobol(dims:Int, paramId:Option[String]) {
  protected val params = ParamCache(paramId)
  protected val seq = new Sobol(dims)(params)

  @JSExport
  def next = js.Array(seq.next():_*)

  @JSExport
  def take(n:Int):js.Array[js.Array[Double]] = js.Array(seq.take(n).map(js.Array(_:_*)).toArray:_*)

  @JSExport
  def count = seq.count
}
