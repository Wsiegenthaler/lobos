package lobos

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSImport("./lobos-params.js", JSImport.Namespace)
object JsRawParams extends js.Object {
  def load(label:String):String = js.native
}

class JsParams(label:String) extends SobolParams {
  lazy protected val dimParams = JsRawParams.load(label)
    .split("\n").drop(1).map(_.split(" +"))
    .map(v => DimensionParams(v(0).toInt, v(2).toLong, v.drop(3).map(_.toLong)))
  override def getParams(dim:Int) = dimParams(dim - 2)
  override def maxDims = dimParams.length + 1
}

/* Caches the raw param data so we don't unpack it every time */
object ParamCache {
  protected val cache = scala.collection.mutable.HashMap.empty[String, JsParams]
  def apply(label:String) = cache.getOrElseUpdate(label, new JsParams(label))
}

/** Helper for providing scala APIs to javascript using the conventional javascript-style "options" object */
object OptionsHelper {
  def option[T, O <: js.Object](options:js.UndefOr[O])(get:(O)=>js.UndefOr[T], dflt:T):T = options.toOption match {
    case Some(opts) => get(opts).getOrElse(dflt); case None => dflt
  }
}
