package lobos.util

import lobos.Sobol
import lobos.params.NewJoeKuo21k

/**
 * Benchmarks the number of points generated in a fixed time period
 *
 * Usage from the sbt console:
 *   `project lobosJVM`
 *   `runMain lobos.util.RateBenchmark [dims [dur [iters]]]`
 */
object RateBenchmark {

  def main(args: Array[String]): Unit = {
    var dims = 10
    var iterations = 20
    var seconds = 1

    val argn = args.length
    if (argn > 0) dims = args(0).toInt
    if (argn > 1) iterations = args(1).toInt
    if (argn > 2) seconds = args(2).toInt

    println(s" [ Dimensions ] = ${dims}d")
    println(s" [ Duration   ] = ${seconds}s")
    println(s" [ Iterations ] = ${iterations}x")

    val avg = (1 to iterations)
      .map(i => {
        val count = benchmark(dims, seconds)
        println(s"> Iteration $i => $count points")
        count
      }).sum / iterations

    println(s"> AVERAGE => $avg points")
  }
  
  def benchmark(dims: Int, seconds: Int): Long = {
    val sequence = new Sobol(dims=dims)
    val start = System.currentTimeMillis()
    
    sequence
      .takeWhile(p => (System.currentTimeMillis - start) < seconds * 1000)
      .size
  }
}
