package lobos

import scala.io.{Codec, Source}

/** Loads the 'SobolParams' from resource */
class JvmParams(path:String, codec:Codec=Codec.UTF8) extends SobolParams {

  import java.util.zip.GZIPInputStream

  override def getParams(dim:Int) = dimParams(dim - 2)
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


package object data {

//  import lobos._
 
  /** Implicit parameters available on import of this package */
  //implicit val newJoeKuo21k = new JvmParams("/new-joe-kuo-6.21201.gz");
  implicit object NewJoeKuo21k extends JvmParams("/new-joe-kuo-6.21201.gz")
}


package object breeze {
  
  import _root_.breeze.linalg.{DenseMatrix, DenseVector}

  implicit def indexedSeq2Vector(seq:IndexedSeq[Double]):DenseVector[Double] = DenseVector(seq:_*)
  implicit def indexedSeqIterator2Matrix(iterator:Iterator[IndexedSeq[Double]]):DenseMatrix[Double] = DenseMatrix(iterator.toSeq:_*)
}
