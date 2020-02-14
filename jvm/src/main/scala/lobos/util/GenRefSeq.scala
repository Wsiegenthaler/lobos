package lobos.util

import lobos.Sobol
import lobos.params.NewJoeKuo21k

import java.io._

/**
 * Command line utility to generate a sequence and write it to a tsv file. The output
 * file is intended for cross validation with implementations in other languages.
 *
 * Usage from the sbt console:
 *   `project lobosJVM`
 *   `runMain lobos.util.GenRefSeq [name [length [dimensions]]]`
 */
object GenRefSeq {
   def main(args: Array[String]): Unit = {

     var (n, dims) = (2048, 10)
     var name = "REF_SEQ"

     val argn = args.length
     if (argn > 0) name = args(0).toUpperCase
     if (argn > 1) n = args(1).toInt
     if (argn > 2) dims = args(2).toInt
     
     var outfile = s"$name.tsv".toLowerCase

     println(s" > name = $name")
     println(s" > outfile = $outfile")
     println(s" > length = $n")
     println(s" > dims = $dims")

     val writer = new PrintWriter(new File(outfile))

     new Sobol(dims)
       .take(n)
       .map(point => point.map(v => v.toString()).mkString("\t"))
       .foreach(row => writer.println(row))
     
     writer.close()
   }
}
