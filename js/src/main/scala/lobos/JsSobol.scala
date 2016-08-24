package lobos

import scala.scalajs.js
import scala.scalajs.js.annotation._

@JSExport("lobos.Sobol")
class JsSobol(dims:Int, options:js.UndefOr[JsSobolOptions]) {

  import OptionsHelper._
  protected val params = option(options)(_.params, JsSobol.dfltParamId)
  protected val resolution = option(options)(_.resolution, Sobol.maxBits)

  protected val paramData = ParamCache(params)
  protected val seq = new Sobol(dims, resolution)(paramData)

  @JSExport
  def next = js.Array(seq.next():_*)

  @JSExport
  def take(n:Int):js.Array[js.Array[Double]] = js.Array(seq.take(n).map(js.Array(_:_*)).toArray:_*)

  @JSExport
  def count = seq.count
}

object JsSobol {
  val dfltParamId:String = "new-joe-kuo-6.1000"
}

@ScalaJSDefined
trait JsSobolOptions extends js.Object {
  val params:js.UndefOr[String]
  val resolution:js.UndefOr[Byte]
}
