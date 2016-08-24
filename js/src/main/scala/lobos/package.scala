package lobos

import scala.scalajs.js
import scala.scalajs.js.annotation._


@js.native
trait LobosParamsWrapper extends js.Object {
  def decode(params:String):String = js.native
}

@js.native
object LobosGlobalScope extends js.GlobalScope {
  val LobosParams:LobosParamsWrapper = js.native
}

class JsParams(params:String) extends SobolParams {
  protected val module = LobosGlobalScope.LobosParams
  override def getParams(dim:Int) = dimParams(dim - 2)
  override def maxDims = dimParams.length + 1
  lazy protected val dimParams = module.decode(params)
    .split("\n").drop(1).map(_.split(" +"))
    .map(v => DimensionParams(v(0).toInt, v(2).toLong, v.drop(3).map(_.toLong)))
}

/* Caches the raw param data so we don't unpack it every time */
object ParamCache {
  protected val cache = scala.collection.mutable.HashMap.empty[String, JsParams]
  def apply(params:String) = cache.getOrElseUpdate(params, new JsParams(params))
}

/** Helper for providing scala APIs to javascript using the conventional javascript-style "options" object */
object OptionsHelper {
  def option[T, O <: js.Object](options:js.UndefOr[O])(get:(O)=>js.UndefOr[T], dflt:T):T = options.toOption match {
    case Some(opts) => get(opts).getOrElse(dflt); case None => dflt
  }
}
